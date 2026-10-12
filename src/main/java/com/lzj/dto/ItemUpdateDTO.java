package com.lzj.dto;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lzj.util.RegexUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** @author 李兆基 */
@Schema(name = "物品修改DTO", description = "物品修改请求参数")
public record ItemUpdateDTO(
        @Schema(description = "物品主键", example = "1")
        @NotNull(message = "物品主键不能为空")
        Long id,

        @Schema(description = "物品分类主键", example = "1")
        @NotNull(message = "所属物品分类主键不能为空")
        Long itemCategoryId,

        @Schema(description = "位置主键", example = "1")
        @NotNull(message = "所属位置主键不能为空")
        Long locationId,

        @Schema(description = "物品名称", example = "手机")
        @NotBlank(message = "物品名称不能为空")
        @Size(min = 1, max = 128, message = "物品名称长度必须在1-128个字符之间")
        @Pattern(regexp = RegexUtil.ITEM_NAME_REGEX, message = RegexUtil.ITEM_NAME_REGEX_MSG)
        String itemName,

        @Schema(description = "物品标签", example = "性价比,贵重物品,随身携带")
        String tags,

        @Schema(description = "排序值（越小越靠前）", example = "1")
        Integer sort,

        @Schema(description = "物品简介", example = "新买的手机")
        @Size(max = 65535, message = "简介长度不能超过65535个字符")
        String summary,

        @Schema(description = "物品价格", example = "7999.99")
        @NotNull(message = "物品价格不能为空")
        Double price,

        @Schema(description = "物品数量", example = "1")
        Integer count,

        @Schema(description = "物品使用率评分（1~5颗星，0表示未评级）", example = "1")
        Integer usageScore,

        @Schema(description = "物品性价比评分（1~5颗星，0表示未评级）", example = "1")
        Integer valueScore,

        @Schema(description = "物品状态（0=服役中，1=闲置中，2=出借中，3=维修中）", example = "1")
        Integer itemStatus,

        @Schema(description = "物品去向（0=持有中，1=已消耗，2=已丢弃，3=已丢失，4=已转增，5=已售出）", example = "1")
        Integer itemOutcome,

        @Schema(description = "物品购买日期", example = "2023-01-01")
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
        LocalDate buyDate,

        @Schema(description = "物品过期日期", example = "2026-01-01")
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
        LocalDate expireDate
) {
    public ItemUpdateDTO {
        sort = ObjUtil.isNotNull(sort) ? sort : 0;
        summary = StrUtil.isNotEmpty(summary) ? summary : "该物品暂无简介";
        count = ObjUtil.isNotNull(count) ? count : 1;
        usageScore = ObjUtil.isNotNull(usageScore) ? usageScore : 0;
        valueScore = ObjUtil.isNotNull(valueScore) ? valueScore : 0;
        itemStatus = ObjUtil.isNotNull(itemStatus) ? itemStatus : 0;
        itemOutcome = ObjUtil.isNotNull(itemOutcome) ? itemOutcome : 0;
    }
}
