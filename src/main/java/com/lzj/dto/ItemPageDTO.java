package com.lzj.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** @author 李兆基 */
@Schema(name = "物品分页DTO")
public record ItemPageDTO(
        @Schema(description = "当前第几页") Integer pageNumber,
        @Schema(description = "每页多少条") Integer pageSize,
        @Schema(description = "位置主键") Long locationId,
        @Schema(description = "物品分类主键") Long itemCategoryId,
        @Schema(description = "物品名称（模糊搜索）") String itemName,
        @Schema(description = "物品状态") Integer itemStatus,
        @Schema(description = "物品去向") Integer itemOutcome) {
}
