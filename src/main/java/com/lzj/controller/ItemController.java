package com.lzj.controller;

import com.lzj.dto.ItemInsertDTO;
import com.lzj.dto.ItemPageDTO;
import com.lzj.dto.ItemUpdateDTO;
import com.lzj.service.ItemService;
import com.lzj.vo.ItemPageVO;
import com.lzj.vo.ItemStatisticsVO;
import com.lzj.vo.ItemVO;
import com.lzj.vo.PageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 物品表 控制层。
 *
 * @author 李兆基
 * @since v1.0.0
 */
@RestController
@Tag(name = "物品表接口")
@RequestMapping("/api/v1/item")
public class ItemController {

    @Resource
    private ItemService itemService;

    @PostMapping("insert")
    @Operation(summary = "添加物品")
    public boolean insert(@RequestBody @Validated @Parameter(description = "物品添加DTO") ItemInsertDTO dto,
                          HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        itemService.insert(userId, dto);
        return true;
    }

    @GetMapping("select/{id}")
    @Operation(summary = "查询物品（按主键）")
    public ItemVO select(@PathVariable("id") @Parameter(description = "物品主键") Long id) {
        return itemService.select(id);
    }

    @GetMapping("page")
    @Operation(summary = "查询物品（分页）")
    public PageVO<ItemPageVO> pageByRoomId(@Validated ItemPageDTO dto,
                                           HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        return itemService.page(userId, dto);
    }

    @GetMapping("statistics")
    @Operation(summary = "统计物品数据", description = "个人物品数据包括总数量，总价值，状态分布数据和去向分布数据")
    public ItemStatisticsVO statistics(HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        return itemService.statistics(userId);
    }

    @PutMapping("update")
    @Operation(summary = "修改物品")
    public boolean update(@RequestBody @Validated @Parameter(description = "物品修改DTO") ItemUpdateDTO dto,
                          HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        itemService.update(userId, dto);
        return true;
    }

    @PostMapping("uploadItemPicture/{itemId}")
    @Operation(summary = "上传物品图片")
    public String uploadItemPicture(
            @PathVariable("itemId") @Parameter(description = "物品主键") Long itemId,
            @RequestParam("itemPicture") @Parameter(description = "物品图片") MultipartFile itemPicture) {
        return itemService.uploadItemPicture(itemId, itemPicture);
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除物品")
    public boolean delete(@PathVariable("id") @Parameter(description = "物品主键") Long id) {
        itemService.delete(id);
        return true;
    }

}
