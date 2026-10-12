package com.lzj.vo;

import cn.hutool.core.util.ObjUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lzj.entity.Item;
import com.lzj.entity.Room;
import com.mybatisflex.annotation.RelationOneToMany;
import com.mybatisflex.annotation.RelationOneToOne;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/** @author 李兆基 */
@Schema(name = "位置VO，分页业务专用")
@Data
public class LocationPageVO implements Serializable {
    @Schema(description = "主键")
    private Long id;
    @JsonIgnore
    @Schema(description = "外键（房间表）")
    private Long roomId;
    @Schema(description = "位置名称")
    private String locationName;
    @Schema(description = "位置图标（Emoji）")
    private String emoji;
    @Schema(description = "排序值（越小越靠前）")
    private Integer sort;
    @Schema(description = "位置简介")
    private String summary;

    @JsonIgnore
    @Schema(description = "该位置所属房间")
    @RelationOneToOne(selfField = "roomId", targetField = "id")
    private Room room;

    @JsonIgnore
    @Schema(description = "该位置下的物品列表")
    @RelationOneToMany(selfField = "id", targetField = "locationId")
    private List<Item> items;

    @Schema(description = "该分类下的物品数量")
    public long getItemCount() {
        if (ObjUtil.isNull(items)) return 0L;
        return items.size();
    }

    @Schema(description = "该位置所属房间的名称")
    public String getRoomName() {
        if (ObjUtil.isNull(room)) return "暂未分配房间";
        return room.getRoomName();
    }
}
