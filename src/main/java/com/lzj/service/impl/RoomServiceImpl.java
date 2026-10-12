package com.lzj.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.lzj.dto.RoomInsertDTO;
import com.lzj.dto.RoomPageDTO;
import com.lzj.dto.RoomUpdateDTO;
import com.lzj.entity.Room;
import com.lzj.exception.ServiceException;
import com.lzj.mapper.LocationMapper;
import com.lzj.mapper.RoomMapper;
import com.lzj.result.ResultCode;
import com.lzj.service.RoomService;
import com.lzj.service.UserService;
import com.lzj.vo.PageVO;
import com.lzj.vo.RoomListVO;
import com.lzj.vo.RoomPageVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.lzj.entity.table.LocationTableDef.LOCATION;
import static com.lzj.entity.table.RoomTableDef.ROOM;

/**
 * 房间表 服务层实现。
 *
 * @author 李兆基
 * @since v1.0.0
 */
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room>  implements RoomService{
    @Resource
    private UserService userService;
    @Resource
    private LocationMapper locationMapper;
    @Override
    public void insert(Long userId, RoomInsertDTO dto) {
        String roomName = dto.roomName();

        // DQL - 判断用户是否存在
        userService.select(userId);

        // DQL - 判断房间名称是否重复
        if (QueryChain.of(mapper)
                .where(ROOM.ROOM_NAME.eq(roomName))
                .and(ROOM.USER_ID.eq(userId))
                .exists()) {
            throw new ServiceException(ResultCode.ROOM_NAME_REPEAT, "房间名称重复，userId=" + userId + "，roomName=" + roomName);
        }

        // 构建物品分类 ORM 实体
        Room room = new Room();
        BeanUtils.copyProperties(dto, room);
        room.setUserId(userId);
        room.setVersion(0);
        room.setIsDeleted(0);
        room.setCreateTime(LocalDateTime.now());
        room.setUpdateTime(LocalDateTime.now());

        // DML - 插入房间记录
        if (mapper.insert(room) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库添加失败");
        }
    }

    @Override
    public Room select(Long id) {
        // DQL - 根据主键查询房间记录
        Room room = QueryChain.of(mapper)
                .where(ROOM.ID.eq(id))
                .one();
        if (ObjUtil.isNull(room)) {
            throw new ServiceException(ResultCode.ROOM_NOT_FOUND, "房间不存在：id=" + id);
        }
        return room;
    }

    @Override
    public List<RoomListVO> list(Long userId) {
        // DQL - 查询该用户全部的房间信息，按排序字段升序，按创建时间降序
        return QueryChain.of(mapper)
                .where(ROOM.USER_ID.eq(userId))
                .orderBy(ROOM.SORT.asc(), ROOM.CREATE_TIME.desc())
                .listAs(RoomListVO.class);
    }
    @Override
    public PageVO<RoomPageVO> page(Long userId, RoomPageDTO dto) {
        String roomName = dto.roomName();

        // 构建查询条件 - 基础
        QueryChain<Room> queryChain = QueryChain.of(mapper)
                .where(ROOM.USER_ID.eq(userId));

        // 构建查询条件 - 房间名称（模糊搜索）
        if (StrUtil.isNotBlank(roomName)) {
            queryChain.and(ROOM.ROOM_NAME.like(roomName));
        }

        // DQL - 分页查询该用户的房间记录
        Page<RoomPageVO> page = queryChain
                .orderBy(ROOM.SORT.asc(), ROOM.CREATE_TIME.desc())
                .withRelations()
                .pageAs(new Page<>(dto.pageNumber(), dto.pageSize()), RoomPageVO.class);

        // 构建 VO 实体：Page<...> → PageVO<...>
        PageVO<RoomPageVO> pageVO = new PageVO<>();
        BeanUtils.copyProperties(page, pageVO);
        return pageVO;
    }

    @Override
    public long count(Long userId) {
        // DQL - 查询房间总数
        return QueryChain.of(mapper)
                .where(ROOM.USER_ID.eq(userId))
                .count();
    }

    @Override
    public void update(Long userId, RoomUpdateDTO dto) {
        String roomName = dto.roomName();
        Long id = dto.id();

        // DQL - 判断房间是否存在
        Room room = this.select(id);

        // DQL - 判断房间名称是否重复（排除自身）
        if (ObjectUtil.isNotNull(roomName) && !roomName.equals(room.getRoomName())) {
            if (QueryChain.of(mapper)
                    .where(ROOM.ROOM_NAME.eq(roomName))
                    .and(ROOM.USER_ID.eq(userId))
                    .and(ROOM.ID.ne(id))
                    .exists()) {
                throw new ServiceException(ResultCode.ROOM_NAME_REPEAT, "房间名称重复，roomName=" + roomName);
            }
        }

        // 构建房间 ORM 实体
        BeanUtils.copyProperties(dto, room);
        room.setUpdateTime(LocalDateTime.now());

        // DML - 更新房间记录
        if (mapper.update(room) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库更新失败");
        }
    }

    @Override
    public void delete(Long id) {

        // DQL 判断房间是否存在
        Room room = this.select(id);

        // DQL 判断该房间下是否存在位置
        if (QueryChain.of(locationMapper)
                .where(LOCATION.ROOM_ID.eq(id))
                .exists()) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "房间下存在位置，无法删除");
        }

        // 构建房间 ORM 实体
        room.setIsDeleted(id.intValue());
        room.setUpdateTime(LocalDateTime.now());

        // DML - 删除房间
        if (mapper.update(room) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库删除失败");
        }
    }

}
