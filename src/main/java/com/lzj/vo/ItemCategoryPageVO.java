package com.lzj.vo;
import cn.hutool.core.util.ObjUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lzj.entity.Item;
import com.mybatisflex.annotation.RelationOneToMany;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/** @author 李兆基 */
@Schema(name = "物品分类VO，分页业务专用")
@Data
public class ItemCategoryPageVO implements Serializable {
    @Schema(description = "主键")
    private Long id;
    @Schema(description = "物品分类名称")
    private String categoryName;
    @Schema(description = "物品分类图标（Emoji）")
    private String emoji;
    @Schema(description = "排序值（越小越靠前）")
    private Integer sort;
    @Schema(description = "物品分类简介")
    private String summary;
    
    @JsonIgnore
    @Schema(description = "该分类下的商品列表")
    @RelationOneToMany(selfField = "id", targetField = "itemCategoryId")
    private List<Item> items;

    @Schema(description = "该分类下的商品数量")
    public long getItemCount() {
        if (ObjUtil.isNull(items)) return 0L;
        return items.size();
    }
}
