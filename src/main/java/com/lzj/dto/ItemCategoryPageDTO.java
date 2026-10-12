package com.lzj.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** @author 李兆基 */
@Schema(name = "物品分类分页DTO")
public record ItemCategoryPageDTO(
        @Schema(description = "当前第几页") Integer pageNumber,
        @Schema(description = "每页多少条") Integer pageSize,
        @Schema(description = "物品分类名称（模糊搜索）") String categoryName) {
}
