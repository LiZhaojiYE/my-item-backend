package com.lzj.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/** @author 李兆基 */
@Schema(name = "位置VO，全查业务专用")
@Data
public class LocationListVO implements Serializable {
    @Schema(description = "主键")
    private Long id;
    @Schema(description = "位置名称")
    private String locationName;
    @Schema(description = "位置图标（Emoji）")
    private String emoji;
}
