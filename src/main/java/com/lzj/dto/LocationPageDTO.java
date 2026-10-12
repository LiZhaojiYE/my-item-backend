package com.lzj.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** @author 李兆基 */
@Schema(name = "位置分页DTO")  
public record LocationPageDTO(  
        @Schema(description = "当前第几页") Integer pageNumber,  
        @Schema(description = "每页多少条") Integer pageSize,  
        @Schema(description = "房间主键") @NotNull(message = "房间主键不能为空") Long roomId,  
        @Schema(description = "位置名称（模糊搜索）") String locationName) {  
}
