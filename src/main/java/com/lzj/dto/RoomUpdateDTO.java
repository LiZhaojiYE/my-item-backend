package com.lzj.dto;
import cn.hutool.core.util.StrUtil;
import com.lzj.util.RegexUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** @author 李兆基 */
@Schema(name = "房间修改DTO", description = "修改房间请求参数")
public record RoomUpdateDTO(
        @Schema(description = "房间主键", example = "1")
        @NotNull(message = "房间主键不能为空")
        Long id,

        @Schema(description = "房间名称", example = "主卧")
        @Size(min = 1, max = 128, message = "房间名称长度必须在1-128个字符之间")
        @Pattern(regexp = RegexUtil.ROOM_NAME_REGEX, message = RegexUtil.ROOM_NAME_REGEX_MSG)
        String roomName,

        @Schema(description = "房间图标（Emoji）", example = "🏨")
        @Size(max = 32, message = "Emoji长度不能超过32个字符")
        String emoji,

        @Schema(description = "排序值（越小越靠前）", example = "1")
        Integer sort,

        @Schema(description = "房间简介", example = "进门左手边第一个卧室")
        @Size(max = 65535, message = "简介长度不能超过65535个字符")
        String summary
) {
    public RoomUpdateDTO {
        // Emoji 默认值：为空时使用默认图标
        emoji = StrUtil.isNotBlank(emoji) ? emoji : "📦";
        // sort 默认值：为空时使用 0
        sort = sort != null ? sort : 0;
        // 简介默认值：如果为空或空白，设置为默认值
        summary = StrUtil.isNotBlank(summary) ? summary : "该房间暂无简介";
    }
}
