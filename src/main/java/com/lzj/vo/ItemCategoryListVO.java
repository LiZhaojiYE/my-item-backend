package com.lzj.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/** @author 李兆基 */
@Schema(name = "物品分类VO，全查业务专用")
@Data
public class ItemCategoryListVO implements Serializable {
    @Schema(description = "主键")
    private Long id;
    @Schema(description = "物品分类名称")
    private String categoryName;
    @Schema(description = "分类图标（Emoji）")
    private String emoji;
}
