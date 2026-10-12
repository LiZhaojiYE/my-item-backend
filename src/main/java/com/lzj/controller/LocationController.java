package com.lzj.controller;

import com.lzj.dto.LocationInsertDTO;
import com.lzj.dto.LocationPageDTO;
import com.lzj.dto.LocationUpdateDTO;
import com.lzj.entity.Location;
import com.lzj.service.LocationService;
import com.lzj.vo.LocationListVO;
import com.lzj.vo.LocationPageVO;
import com.lzj.vo.PageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 位置表 控制层。
 *
 * @author 李兆基
 * @since v1.0.0
 */
@RestController
@Tag(name = "位置表接口")
@RequestMapping("/api/v1/location")
public class LocationController {
    @Resource
    private LocationService locationService;
    @PostMapping("insert")
    @Operation(summary = "添加位置")
    public boolean insert(@RequestBody @Validated @Parameter(description = "位置添加DTO") LocationInsertDTO dto) {
        locationService.insert(dto);
        return true;
    }
    @GetMapping("select/{id}")
    @Operation(summary = "查询位置（按主键）")
    public Location select(@PathVariable("id") @Parameter(description = "位置主键") Long id) {
        return locationService.select(id);
    }
    @GetMapping("list/{roomId}")
    @Operation(summary = "查询指定房间内的位置（全部）")
    public List<LocationListVO> listByRoomId(@PathVariable("roomId") @Parameter(description = "房间主键") Long roomId) {
        return locationService.listByRoomId(roomId);
    }

    @GetMapping("page")
    @Operation(summary = "查询指定房间内的位置（分页）")
    public PageVO<LocationPageVO> pageByRoomId(@Validated LocationPageDTO dto) {
        return locationService.pageByRoomId(dto);
    }

    @PutMapping("update")
    @Operation(summary = "修改位置")
    public boolean update(@RequestBody @Validated @Parameter(description = "位置修改DTO") LocationUpdateDTO dto) {
        locationService.update(dto);
        return true;
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除位置")
    public boolean delete(@PathVariable("id") @Parameter(description = "位置主键") Long id) {
        locationService.delete(id);
        return true;
    }

}
