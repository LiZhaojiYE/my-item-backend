package com.lzj.controller;

import com.lzj.dto.RoomInsertDTO;
import com.lzj.dto.RoomPageDTO;
import com.lzj.dto.RoomUpdateDTO;
import com.lzj.entity.Room;
import com.lzj.service.RoomService;
import com.lzj.vo.PageVO;
import com.lzj.vo.RoomListVO;
import com.lzj.vo.RoomPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房间表 控制层。
 *
 * @author 李兆基
 * @since v1.0.0
 */
@RestController
@Tag(name = "房间表接口")
@RequestMapping("/api/v1/room")
public class RoomController {
    @Resource
    private RoomService roomService;
    @PostMapping("insert")
    @Operation(summary = "添加房间")
    public boolean insert(@RequestBody @Validated @Parameter(description = "房间添加DTO") RoomInsertDTO dto,
                          HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        roomService.insert(userId, dto);
        return true;
    }

    @GetMapping("select/{id}")
    @Operation(summary = "查询房间（按主键）")
    public Room select(@PathVariable("id") @Parameter(description = "房间主键") Long id) {
        return roomService.select(id);
    }

    @GetMapping("list")
    @Operation(summary = "查询房间（全部）")
    public List<RoomListVO> list(HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        return roomService.list(userId);
    }

    @GetMapping("page")
    @Operation(summary = "查询房间（分页）")
    public PageVO<RoomPageVO> page(RoomPageDTO dto, HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        return roomService.page(userId, dto);
    }

    @GetMapping("count")
    @Operation(summary = "统计房间总数")
    public long count(HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        return roomService.count(userId);
    }

    @PutMapping("update")
    @Operation(summary = "修改房间")
    public boolean update(@RequestBody @Validated @Parameter(description = "房间修改DTO") RoomUpdateDTO dto,
                          HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        roomService.update(userId, dto);
        return true;
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除房间")
    public boolean delete(@PathVariable("id") @Parameter(description = "房间主键") Long id) {
        roomService.delete(id);
        return true;
    }

}
