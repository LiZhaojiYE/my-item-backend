package com.lzj.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** @author 李兆基 */
@Schema(name = "房间分页DTO")  
public record RoomPageDTO(  
        @Schema(description = "当前第几页") Integer pageNumber,  
        @Schema(description = "每页多少条") Integer pageSize,  
        @Schema(description = "房间名称（模糊搜索）") String roomName) {  
}
