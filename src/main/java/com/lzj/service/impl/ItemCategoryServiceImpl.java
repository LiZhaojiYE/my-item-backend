package com.lzj.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.lzj.dto.ItemCategoryInsertDTO;
import com.lzj.dto.ItemCategoryPageDTO;
import com.lzj.dto.ItemCategoryUpdateDTO;
import com.lzj.entity.ItemCategory;
import com.lzj.exception.ServiceException;
import com.lzj.mapper.ItemCategoryMapper;
import com.lzj.mapper.ItemMapper;
import com.lzj.result.ResultCode;
import com.lzj.service.ItemCategoryService;
import com.lzj.service.UserService;
import com.lzj.vo.ItemCategoryListVO;
import com.lzj.vo.ItemCategoryPageVO;
import com.lzj.vo.PageVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.lzj.entity.table.ItemCategoryTableDef.ITEM_CATEGORY;
import static com.lzj.entity.table.ItemTableDef.ITEM;

/**
 * 物品分类表 服务层实现。
 *
 * @author 李兆基
 * @since v1.0.0
 */
@Service
public class ItemCategoryServiceImpl extends ServiceImpl<ItemCategoryMapper, ItemCategory>  implements ItemCategoryService{
    @Resource
    private UserService userService;
    @Resource
    private ItemMapper itemMapper;

    @Override
    public void insert(Long userId, ItemCategoryInsertDTO dto) {
        String categoryName = dto.categoryName();

        // DQL - 判断用户是否存在
        userService.select(userId);

        // DQL - 判断物品分类名称是否重复
        if (QueryChain.of(mapper)
                .where(ITEM_CATEGORY.CATEGORY_NAME.eq(categoryName))
                .and(ITEM_CATEGORY.USER_ID.eq(userId))
                .exists()) {
            throw new ServiceException(ResultCode.CATEGORY_NAME_REPEAT, "物品分类名称重复，userId=" + userId + "，categoryName=" + categoryName);
        }

        // 构建物品分类 ORM 实体
        ItemCategory itemCategory = new ItemCategory();
        BeanUtils.copyProperties(dto, itemCategory);
        itemCategory.setUserId(userId);
        itemCategory.setVersion(0);
        itemCategory.setIsDeleted(0);
        itemCategory.setCreateTime(LocalDateTime.now());
        itemCategory.setUpdateTime(LocalDateTime.now());

        // DML - 插入物品分类记录
        if (mapper.insert(itemCategory) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库添加失败");
        }
    }

    @Override
    public ItemCategory select(Long id) {

        // DQL - 根据主键查询物品分类记录
        ItemCategory itemCategory = QueryChain.of(mapper)
                .where(ITEM_CATEGORY.ID.eq(id))
                .one();
        if (ObjUtil.isNull(itemCategory)) {
            throw new ServiceException(ResultCode.ITEM_CATEGORY_NOT_FOUND, "物品分类不存在：id=" + id);
        }
        return itemCategory;
    }

    @Override
    public List<ItemCategoryListVO> list(Long userId) {

        // DQL - 查询该用户全部的物品分类信息，按排序字段升序，按创建时间降序
        return QueryChain.of(mapper)
                .where(ITEM_CATEGORY.USER_ID.eq(userId))
                .orderBy(ITEM_CATEGORY.SORT.asc(), ITEM_CATEGORY.CREATE_TIME.desc())
                .listAs(ItemCategoryListVO.class);
    }

    @Override
    public PageVO<ItemCategoryPageVO> page(Long userId, ItemCategoryPageDTO dto) {
        String categoryName = dto.categoryName();

        // 构建查询条件 - 基础
        QueryChain<ItemCategory> queryChain = QueryChain.of(mapper)
                .where(ITEM_CATEGORY.USER_ID.eq(userId));

        // 构建查询条件 - 分类名称（模糊搜索）
        if (StrUtil.isNotBlank(categoryName)) {
            queryChain.and(ITEM_CATEGORY.CATEGORY_NAME.like(categoryName));
        }

        // DQL - 分页查询该用户的物品分类记录
        Page<ItemCategoryPageVO> page = queryChain
                .orderBy(ITEM_CATEGORY.SORT.asc(), ITEM_CATEGORY.CREATE_TIME.desc())
                .withRelations()
                .pageAs(new Page<>(dto.pageNumber(), dto.pageSize()), ItemCategoryPageVO.class);

        // 构建 VO 实体：Page<...> → PageVO<...>
        PageVO<ItemCategoryPageVO> pageVO = new PageVO<>();
        BeanUtils.copyProperties(page, pageVO);
        return pageVO;
    }

    @Override
    public long count(Long userId) {
        // DQL - 查询物品分类总数
        return QueryChain.of(mapper)
                .where(ITEM_CATEGORY.USER_ID.eq(userId))
                .count();
    }
    @Override
    public void update(Long userId, ItemCategoryUpdateDTO dto) {
        String categoryName = dto.categoryName();
        Long id = dto.id();

        // DQL - 判断物品分类是否存在
        ItemCategory category = this.select(id);

        // DQL - 判断物品分类名称是否重复（排除自身）
        if (ObjectUtil.isNotNull(categoryName) && !categoryName.equals(category.getCategoryName())) {
            if (QueryChain.of(mapper)
                    .where(ITEM_CATEGORY.CATEGORY_NAME.eq(categoryName))
                    .and(ITEM_CATEGORY.USER_ID.eq(userId))
                    .and(ITEM_CATEGORY.ID.ne(id))
                    .exists()) {
                throw new ServiceException(ResultCode.CATEGORY_NAME_REPEAT, "物品分类名称重复，categoryName=" + categoryName);
            }
        }

        // 构建物品分类 ORM 实体
        BeanUtils.copyProperties(dto, category);
        category.setUpdateTime(LocalDateTime.now());

        // DML - 更新物品分类记录
        if (mapper.update(category) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库更新失败");
        }
    }
    @Override
    public void delete(Long id) {

        // DQL - 判断物品分类是否存在
        ItemCategory itemCategory = this.select(id);

        // DQL - 判断该物品分类下是否存在物品
        if (QueryChain.of(itemMapper)
                .where(ITEM.ITEM_CATEGORY_ID.eq(id))
                .exists()) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "物品分类下存在物品，无法删除");
        }

        // 构建物品分类 ORM 实体
        itemCategory.setIsDeleted(id.intValue());
        itemCategory.setUpdateTime(LocalDateTime.now());

        // DML - 删除物品分类
        if (mapper.update(itemCategory) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库删除失败");
        }
    }

}
