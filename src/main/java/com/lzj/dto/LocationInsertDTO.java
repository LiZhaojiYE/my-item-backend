package com.lzj.dto;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** @author 李兆基 */
@Schema(name = "位置添加DTO", description = "位置添加请求参数")
public record LocationInsertDTO(
        @Schema(description = "房间主键", example = "1")
        @NotNull(message = "所属房间主键不能为空")
        Long roomId,

        @Schema(description = "位置名称", example = "床头柜")
        @NotBlank(message = "位置名称不能为空")
        @Size(min = 1, max = 128, message = "位置名称长度必须在1-128个字符之间")
        String locationName,

        @Schema(description = "位置图标（Emoji）", example = "🚪")
        @Size(max = 32, message = "Emoji长度不能超过32个字符")
        String emoji,

        @Schema(description = "排序值（越小越靠前）", example = "1")
        Integer sort,

        @Schema(description = "位置简介", example = "左边的玻璃床头柜")
        @Size(max = 65535, message = "简介长度不能超过65535个字符")
        String summary
) {
    public LocationInsertDTO {
        // Emoji 默认值：为空时使用默认图标
        emoji = StrUtil.isNotBlank(emoji) ? emoji : "📦";
        // sort 默认值：为空时使用 0
        sort = sort != null ? sort : 0;
        // 简介默认值：如果为空或空白，设置为默认值
        summary = StrUtil.isNotBlank(summary) ? summary : "该位置暂无简介";
    }
}
