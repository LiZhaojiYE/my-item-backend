package com.lzj.vo;
import cn.hutool.core.util.ObjUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lzj.entity.Location;
import com.mybatisflex.annotation.RelationOneToMany;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/** @author 李兆基 */
@Schema(name = "房间VO，分页业务专用")
@Data
public class RoomPageVO implements Serializable {
    @Schema(description = "主键")
    private Long id;
    @Schema(description = "房间名称")
    private String roomName;
    @Schema(description = "房间图标（Emoji）")
    private String emoji;
    @Schema(description = "排序值（越小越靠前）")  
	private Integer sort;
    @Schema(description = "房间简介")
    private String summary;

    @JsonIgnore
    @Schema(description = "该房间下的位置列表")
    @RelationOneToMany(selfField = "id", targetField = "roomId")
    private List<Location> locations;

    @Schema(description = "该分类下的位置数量")
    public long getLocationCount() {
        if (ObjUtil.isNull(locations)) return 0L;
        return locations.size();
    }
}
