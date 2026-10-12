package com.lzj.controller;

import com.lzj.dto.ItemCategoryInsertDTO;
import com.lzj.dto.ItemCategoryPageDTO;
import com.lzj.dto.ItemCategoryUpdateDTO;
import com.lzj.entity.ItemCategory;
import com.lzj.service.ItemCategoryService;
import com.lzj.vo.ItemCategoryListVO;
import com.lzj.vo.ItemCategoryPageVO;
import com.lzj.vo.PageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物品分类表 控制层。
 *
 * @author 李兆基
 * @since v1.0.0
 */
@RestController
@Tag(name = "物品分类表接口")
@RequestMapping("/api/v1/itemCategory")
public class ItemCategoryController {

    @Resource
    private ItemCategoryService itemCategoryService;
    @PostMapping("insert")
    @Operation(summary = "添加物品分类")
    public boolean insert(@RequestBody @Validated @Parameter(description = "物品添加DTO") ItemCategoryInsertDTO dto,
                          HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        itemCategoryService.insert(userId, dto);
        return true;
    }

    @GetMapping("select/{id}")
    @Operation(summary = "查询物品分类（按主键）")
    public ItemCategory select(@PathVariable("id") @Parameter(description = "物品分类ID") Long id) {
        return itemCategoryService.select(id);
    }

    @GetMapping("list")
    @Operation(summary = "查询物品分类（全部）")
    public List<ItemCategoryListVO> list(HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        return itemCategoryService.list(userId);
    }

    @GetMapping("page")
    @Operation(summary = "查询物品分类（分页）")
    public PageVO<ItemCategoryPageVO> page(ItemCategoryPageDTO dto, HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        return itemCategoryService.page(userId, dto);
    }

    @GetMapping("count")
    @Operation(summary = "统计物品分类总数")
    public long count(HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        return itemCategoryService.count(userId);
    }

    @PutMapping("update")
    @Operation(summary = "修改物品分类")
    public boolean update(@RequestBody @Validated @Parameter(description = "物品分类修改DTO") ItemCategoryUpdateDTO dto,
                          HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        itemCategoryService.update(userId, dto);
        return true;
    }
    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除物品分类")
    public boolean delete(@PathVariable("id") @Parameter(description = "物品分类主键") Long id) {
        itemCategoryService.delete(id);
        return true;
    }

}
