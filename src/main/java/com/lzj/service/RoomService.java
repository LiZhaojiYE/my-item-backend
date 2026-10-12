package com.lzj.service;

import com.lzj.dto.RoomInsertDTO;
import com.lzj.dto.RoomPageDTO;
import com.lzj.dto.RoomUpdateDTO;
import com.lzj.entity.Room;
import com.lzj.vo.PageVO;
import com.lzj.vo.RoomListVO;
import com.lzj.vo.RoomPageVO;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 房间表 服务层。
 *
 * @author 李兆基
 * @since v1.0.0
 */
public interface RoomService extends IService<Room> {
    void insert(Long userId, RoomInsertDTO dto);
    Room select(Long id);
    List<RoomListVO> list(Long userId);
    PageVO<RoomPageVO> page(Long userId, RoomPageDTO dto);
    long count(Long userId);
    void update(Long userId, RoomUpdateDTO dto);
    void delete(Long id);

}
