package com.lzj.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/** @author 李兆基 */
@Schema(name = "房间VO，全查业务专用")
@Data
public class RoomListVO implements Serializable {
    @Schema(description = "主键")
    private Long id;
    @Schema(description = "房间名称")
    private String roomName;
    @Schema(description = "房间图标（Emoji）")
    private String emoji;
}
