package com.lzj.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.lzj.dto.LocationInsertDTO;
import com.lzj.dto.LocationPageDTO;
import com.lzj.dto.LocationUpdateDTO;
import com.lzj.entity.Location;
import com.lzj.exception.ServiceException;
import com.lzj.mapper.ItemMapper;
import com.lzj.mapper.LocationMapper;
import com.lzj.result.ResultCode;
import com.lzj.service.LocationService;
import com.lzj.service.RoomService;
import com.lzj.vo.LocationListVO;
import com.lzj.vo.LocationPageVO;
import com.lzj.vo.PageVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.lzj.entity.table.ItemTableDef.ITEM;
import static com.lzj.entity.table.LocationTableDef.LOCATION;

/** @author 李兆基 */
@Service
public class LocationServiceImpl extends ServiceImpl<LocationMapper, Location> implements LocationService {
    @Resource
    private RoomService roomService;
    @Resource
    private ItemMapper itemMapper;
    @Override
    public void insert(LocationInsertDTO dto) {
        String locationName = dto.locationName();
        Long roomId = dto.roomId();

        // DQL - 判断房间是否存在
        roomService.select(roomId);

        // DQL - 判断位置名称是否重复
        if (QueryChain.of(mapper)
                .where(LOCATION.LOCATION_NAME.eq(locationName))
                .and(LOCATION.ROOM_ID.eq(roomId))
                .exists()) {
            throw new ServiceException(ResultCode.LOCATION_NAME_REPEAT, "位置名称重复，roomId=" + roomId + "，locationName=" + locationName);
        }

        // 构建位置表 ORM 实体
        Location location = new Location();
        BeanUtils.copyProperties(dto, location);
        location.setVersion(0);
        location.setIsDeleted(0);
        location.setCreateTime(LocalDateTime.now());
        location.setUpdateTime(LocalDateTime.now());

        // DML - 插入位置记录
        if (mapper.insert(location) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库添加失败");
        }
    }

    @Override
    public Location select(Long id) {
        // DQL - 根据主键查询位置记录
        Location location = QueryChain.of(mapper)
                .where(LOCATION.ID.eq(id))
                .one();
        if (ObjUtil.isNull(location)) {
            throw new ServiceException(ResultCode.LOCATION_NOT_FOUND, "位置不存在：id=" + id);
        }
        return location;
    }

    @Override
    public List<LocationListVO> listByRoomId(Long roomId) {

        // DQL - 判断房间是否存在
        roomService.select(roomId);

        // DQL - 查询该房间中全部的位置信息
        return QueryChain.of(mapper)
                .where(LOCATION.ROOM_ID.eq(roomId))
                .orderBy(LOCATION.SORT.asc(), LOCATION.CREATE_TIME.desc())
                .listAs(LocationListVO.class);
    }

    @Override
    public PageVO<LocationPageVO> pageByRoomId(LocationPageDTO dto) {
        String locationName = dto.locationName();
        Long roomId = dto.roomId();

        // DQL - 判断房间是否存在
        roomService.select(roomId);

        // 构建查询条件 - 基础
        QueryChain<Location> queryChain = QueryChain.of(mapper)
                .where(LOCATION.ROOM_ID.eq(roomId));

        // 构建查询条件 - 位置名称（模糊搜索）
        if (StrUtil.isNotBlank(locationName)) {
            queryChain.and(LOCATION.LOCATION_NAME.like(locationName));
        }

        // DQL - 分页查询该房间的位置记录
        Page<LocationPageVO> page = queryChain
                .orderBy(LOCATION.SORT.asc(), LOCATION.ID.desc())
                .withRelations()
                .pageAs(new Page<>(dto.pageNumber(), dto.pageSize()), LocationPageVO.class);

        // 构建 VO 实体
        PageVO<LocationPageVO> pageVO = new PageVO<>();
        BeanUtils.copyProperties(page, pageVO);
        return pageVO;
    }

    @Override
    public void update(LocationUpdateDTO dto) {
        String locationName = dto.locationName();
        Long id = dto.id();
        Long roomId = dto.roomId();

        // DQL - 判断位置是否存在
        Location location = this.select(id);

        // DQL - 判断位置名称是否重复（排除自身）
        if (ObjectUtil.isNotNull(locationName) && !locationName.equals(location.getLocationName())) {
            if (QueryChain.of(mapper)
                    .where(LOCATION.LOCATION_NAME.eq(locationName))
                    .and(LOCATION.ID.ne(id))
                    .and(LOCATION.ROOM_ID.eq(roomId))
                    .exists()) {
                throw new ServiceException(ResultCode.LOCATION_NAME_REPEAT, "位置名称重复，roomId=" + roomId + "，locationName=" + locationName);
            }
        }

        // 构建位置 ORM 实体
        BeanUtils.copyProperties(dto, location);
        location.setUpdateTime(LocalDateTime.now());

        // DML - 更新位置记录
        if (mapper.update(location) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库更新失败");
        }
    }

    @Override
    public void delete(Long id) {

        // DQL 判断位置是否存在
        Location location = this.select(id);

        // DQL 判断该位置下是否存在物品
        if (QueryChain.of(itemMapper)
                .where(ITEM.LOCATION_ID.eq(id))
                .exists()) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "位置下存在物品，无法删除");
        }

        // 构建位置 ORM 实体
        location.setIsDeleted(id.intValue());
        location.setUpdateTime(LocalDateTime.now());

        // DML - 删除位置
        if (mapper.update(location) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库删除失败");
        }
    }

}
