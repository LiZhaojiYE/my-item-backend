package com.lzj.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.lzj.dto.ItemInsertDTO;
import com.lzj.dto.ItemPageDTO;
import com.lzj.dto.ItemUpdateDTO;
import com.lzj.entity.Item;
import com.lzj.exception.ServiceException;
import com.lzj.mapper.ItemMapper;
import com.lzj.result.ResultCode;
import com.lzj.service.ItemCategoryService;
import com.lzj.service.ItemService;
import com.lzj.service.LocationService;
import com.lzj.service.UserService;
import com.lzj.util.MinioUtil;
import com.lzj.vo.ItemPageVO;
import com.lzj.vo.ItemStatisticsVO;
import com.lzj.vo.ItemVO;
import com.lzj.vo.PageVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lzj.entity.table.ItemTableDef.ITEM;
/** @author 李兆基 */
@Slf4j
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {
    @Value("${myitem.minio.bucket-name}")
    private String MINIO_BUCKET_NAME;
    @Value("${myitem.minio.item-picture-dir}")
    private String MINIO_ITEM_PICTURE_DIR;
    @Value("${myitem.minio.default-item-picture}")
    private String MINIO_DEFAULT_ITEM_PICTURE;

    private final UserService userService;
    private final ItemCategoryService itemCategoryService;
    private final LocationService locationService;
    private final ChatClient chatClient;

    public ItemServiceImpl(UserService userService,
                           ItemCategoryService itemCategoryService,
                           LocationService locationService,
                           ChatClient.Builder chatClientBuilder) {
        this.userService = userService;
        this.itemCategoryService = itemCategoryService;
        this.locationService = locationService;
        this.chatClient = chatClientBuilder.build();
    }
    @Override
    public void insert(Long userId, ItemInsertDTO dto) {
        Long itemCategoryId = dto.itemCategoryId();
        Long locationId = dto.locationId();
        String itemName = dto.itemName();
        String summary = dto.summary();

        // DQL - 判断用户是否存在
        userService.select(userId);
        // DQL - 判断物品分类是否存在
        itemCategoryService.select(itemCategoryId);
        // DQL - 判断位置是否存在
        locationService.select(locationId);

        // DQL - 判断物品名称是否重复
        if (QueryChain.of(mapper)
                .where(ITEM.ITEM_NAME.eq(itemName))
                .and(ITEM.USER_ID.eq(userId))
                .exists()) {
            throw new ServiceException(ResultCode.ITEM_NAME_REPEAT, "物品名称重复，itemName=" + itemName);
        }

        // 使用 AI 分析出 remark
        String aiRemark = chatClient.prompt()
                .system("""
                    # 角色定位
                    你是一个物品分析专员，负责根据物品的名称和描述来分析出物品的分类。
                    # 输入规则
                    - 物品的名称为：%s
                    - 物品的描述为：%s
                    # 输出规则
                    - 只要是能吃的物品，包括零食、生鲜、粮油、调料、熟食、面点、饮品、水果、干货、速冻食品、罐头、酒水、滋补品、半成品食材等 → 美食
                    - 只要是能穿的物品，包括服饰、首饰、手表、鞋、包、头饰、面饰、颈部或肢体的配饰、腰带、鞋袜、季节性风格单品等 → 穿搭
                    - 除此之外 → 其它
                    - 最终分析的结果只能是 “美食”，“穿搭” 和 “其它” 三种，不允许返回其它任何的结果。
                    """.formatted(itemName, summary))
                .call()
                .content();
        log.info("✅分析出物品分类备注 remark={}", aiRemark);

        // 构建物品表 ORM 实体
        Item item = new Item();
        BeanUtils.copyProperties(dto, item);
        item.setUserId(userId);
        item.setPictureUrl(MINIO_DEFAULT_ITEM_PICTURE);
        item.setRemark(aiRemark);
        item.setVersion(0);
        item.setIsDeleted(0);
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        // DML - 插入物品记录
        if (mapper.insert(item) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库添加失败");
        }
    }

    @Override
    public ItemVO select(Long id) {
        // DQL - 根据主键查询物品记录
        Item item = QueryChain.of(mapper)
                .where(ITEM.ID.eq(id))
                .one();
        if (ObjUtil.isNull(item)) {
            throw new ServiceException(ResultCode.ITEM_NOT_FOUND, "物品不存在：id=" + id);
        }

        // 构建物品 VO 实体
        ItemVO vo = new ItemVO();
        BeanUtils.copyProperties(item, vo);
        // 计算使用天数和日均价格
        vo.calculateFields();
        return vo;
    }

    @Override
    public PageVO<ItemPageVO> page(Long userId, ItemPageDTO dto) {
        Long locationId = dto.locationId();
        Long itemCategoryId = dto.itemCategoryId();
        String itemName = dto.itemName();
        Integer itemStatus = dto.itemStatus();
        Integer itemOutcome = dto.itemOutcome();

        // 构建查询条件 - 基础
        QueryChain<Item> queryChain = QueryChain.of(mapper)
                .where(ITEM.USER_ID.eq(userId));

        // 构建查询条件 - 位置主键
        if (ObjUtil.isNotNull(locationId)) {
            queryChain.and(ITEM.LOCATION_ID.eq(locationId));
        }

        // 构建查询条件 - 物品分类主键
        if (ObjUtil.isNotNull(itemCategoryId)) {
            queryChain.and(ITEM.ITEM_CATEGORY_ID.eq(itemCategoryId));
        }

        // 构建查询条件 - 物品名称（模糊搜索）
        if (StrUtil.isNotBlank(itemName)) {
            queryChain.and(ITEM.ITEM_NAME.like(itemName));
        }

        // 构建查询条件 - 物品状态
        if (ObjUtil.isNotNull(itemStatus)) {
            queryChain.and(ITEM.ITEM_STATUS.eq(itemStatus));
        }

        // 构建查询条件 - 物品去向
        if (ObjUtil.isNotNull(itemOutcome)) {
            queryChain.and(ITEM.ITEM_OUTCOME.eq(itemOutcome));
        }

        // DQL - 分页查询该房间的位置记录
        Page<ItemPageVO> page = queryChain
                .orderBy(ITEM.SORT.asc(), ITEM.CREATE_TIME.desc())
                .pageAs(new Page<>(dto.pageNumber(), dto.pageSize()), ItemPageVO.class);
        // 计算使用天数和日均价格
        page.getRecords().forEach(ItemPageVO::calculateFields);
        // 构建 VO 实体：Page<...> → PageVO<...>
        PageVO<ItemPageVO> pageVO = new PageVO<>();
        BeanUtils.copyProperties(page, pageVO);
        return pageVO;
    }

    @Override
    public ItemStatisticsVO statistics(Long userId) {
        Map<Integer, Long> statusCountMap = new HashMap<>();
        Map<Integer, Long> outcomeCountMap = new HashMap<>();
        Double totalPrice = 0.00;
        Long totalCount = 0L;

        // SQL - 获取个人全部物品
        List<Item> items = QueryChain.of(mapper)
                .and(ITEM.USER_ID.eq(userId))
                .list();

        // 统计不同状态的物品数量
        for (Item item : items) {
            // 计算物品总价值
            totalPrice += item.getPrice();
            // 统计物品总数量
            totalCount++;
            // 统计不同状态的物品数量：第一个参数为状态值，第二个参数为默认值，第三个参数为合并操作
            statusCountMap.merge(item.getItemStatus(), 1L, Long::sum);
            // 统计不同去向的物品数量：第一个参数为去向值，第二个参数为默认值，第三个参数为合并操作
            outcomeCountMap.merge(item.getItemOutcome(), 1L, Long::sum);
        }

        // 构建 VO 实体
        ItemStatisticsVO vo = new ItemStatisticsVO();
        vo.setTotalCount(totalCount);
        vo.setTotalPrice(totalPrice);
        vo.setStatusCountMap(statusCountMap);
        vo.setOutcomeCountMap(outcomeCountMap);
        return vo;
    }

    @Override
    public void update(Long userId, ItemUpdateDTO dto) {
        Long id = dto.id();
        String itemName = dto.itemName();
        Long itemCategoryId = dto.itemCategoryId();
        Long locationId = dto.locationId();
        Integer itemStatus = dto.itemStatus();
        Integer itemOutcome = dto.itemOutcome();

        // DQL - 根据主键查询物品
        Item item = QueryChain.of(mapper)
                .where(ITEM.ID.eq(id))
                .one();
        if (ObjUtil.isNull(item)) {
            throw new ServiceException(ResultCode.ITEM_NOT_FOUND, "物品不存在：id=" + dto.id());
        }

        // DQL - 判断物品名称是否重复（排除自身）
        if (ObjectUtil.isNotNull(itemName) && !itemName.equals(item.getItemName())) {
            if (QueryChain.of(mapper)
                    .where(ITEM.ITEM_NAME.eq(itemName))
                    .and(ITEM.USER_ID.eq(userId))
                    .and(ITEM.ID.ne(id))
                    .exists()) {
                throw new ServiceException(ResultCode.ITEM_NAME_REPEAT, "物品名称重复，itemName=" + itemName);
            }
        }

        // DQL - 判断物品分类是否存在
        if (ObjectUtil.isNotNull(itemCategoryId) && !itemCategoryId.equals(item.getItemCategoryId())) {
            itemCategoryService.select(itemCategoryId);
        }

        // DQL - 判断位置是否存在
        if (ObjectUtil.isNotNull(locationId) && !locationId.equals(item.getLocationId())) {
            locationService.select(locationId);
        }

        // 若修改了物品状态，则更新物品状态变更时间
        if (ObjUtil.isNotNull(itemStatus) && !itemStatus.equals(item.getItemStatus())) {
            item.setStatusChangeTime(LocalDateTime.now());
        }

        // 若修改了物品去向，则更新物品去向变更时间
        if (ObjUtil.isNotNull(itemOutcome) && !itemOutcome.equals(item.getItemOutcome())) {
            item.setOutcomeChangeTime(LocalDateTime.now());
        }

        // 构建 ORM 实体
        BeanUtils.copyProperties(dto, item);
        item.setUpdateTime(LocalDateTime.now());

        // DML - 更新物品记录
        if (mapper.update(item) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库更新失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String uploadItemPicture(Long id, MultipartFile itemPicture) {

        // DQL - 根据主键查询物品记录
        Item item = QueryChain.of(mapper)
                .where(ITEM.ID.eq(id))
                .one();
        if (ObjUtil.isNull(item)) {
            throw new ServiceException(ResultCode.ITEM_NOT_FOUND, "物品不存在：id=" + id);
        }

        // 备份旧文件地址
        String oldPictureUrl = item.getPictureUrl();
        log.info("✅【uploadItemPicture】获取到旧物品图片，pictureUrl={}", oldPictureUrl);

        // 上传新文件到 MinIO
        String newPictureUrl = MinioUtil.upload(itemPicture, MINIO_ITEM_PICTURE_DIR, MINIO_BUCKET_NAME);
        log.info("✅【uploadItemPicture】上传新物品图片到 MinIO，pictureUrl={}", newPictureUrl);

        // DML - 更新物品图片
        item.setPictureUrl(newPictureUrl);
        item.setUpdateTime(LocalDateTime.now());
        if (mapper.update(item) <= 0) {
            log.warn("✅【uploadItemPicture】更新物品图片失败，itemId={}", id);
            MinioUtil.delete(newPictureUrl, MINIO_ITEM_PICTURE_DIR, MINIO_BUCKET_NAME);
            log.warn("✅【uploadItemPicture】从 MinIO 中删除新上传的物品图片，pictureUrl={}", newPictureUrl);
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库更新失败");
        }
        log.info("✅【uploadItemPicture】更新物品图片成功，itemId={}", id);

        // 从 MinIO 中删除旧文件（数据库更新成功后）
        if (ObjectUtil.isNotNull(oldPictureUrl) && !oldPictureUrl.equals(MINIO_DEFAULT_ITEM_PICTURE)) {
            MinioUtil.delete(oldPictureUrl, MINIO_ITEM_PICTURE_DIR, MINIO_BUCKET_NAME);
            log.info("✅【uploadItemPicture】从 MinIO 中删除旧物品图片，pictureUrl={}", oldPictureUrl);
        } else {
            log.info("✅【uploadItemPicture】旧物品图片为空或默认物品图片，无需删除");
        }

        // 返回文件名称
        return newPictureUrl;
    }

    @Override
    public void delete(Long id) {

        // DQL 判断物品是否存在
        Item item = QueryChain.of(mapper)
                .where(ITEM.ID.eq(id))
                .one();
        if (ObjUtil.isNull(item)) {
            throw new ServiceException(ResultCode.ITEM_NOT_FOUND, "物品不存在：id=" + id);
        }

        // 构建物品 ORM 实体
        item.setIsDeleted(id.intValue());
        item.setUpdateTime(LocalDateTime.now());

        // DML - 删除物品
        if (mapper.update(item) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库删除失败");
        }
    }

}
