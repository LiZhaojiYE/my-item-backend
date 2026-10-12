package com.lzj.service;

import com.lzj.dto.LocationInsertDTO;
import com.lzj.dto.LocationPageDTO;
import com.lzj.dto.LocationUpdateDTO;
import com.lzj.entity.Location;
import com.lzj.vo.LocationListVO;
import com.lzj.vo.LocationPageVO;
import com.lzj.vo.PageVO;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 位置表 服务层。
 *
 * @author 李兆基
 * @since v1.0.0
 */
public interface LocationService extends IService<Location> {
    void insert(LocationInsertDTO dto);
    Location select(Long id);
    List<LocationListVO> listByRoomId(Long roomId);
    PageVO<LocationPageVO> pageByRoomId(LocationPageDTO dto);
    void update(LocationUpdateDTO dto);
    void delete(Long id);

}
