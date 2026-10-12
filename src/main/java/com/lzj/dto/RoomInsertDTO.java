package com.lzj.dto;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** @author 李兆基 */
@Schema(name = "房间添加DTO", description = "房间添加请求参数")
public record RoomInsertDTO(
        @Schema(description = "房间名称", example = "房间名称（测试）")
        @NotBlank(message = "房间名称不能为空")
        @Size(min = 1, max = 128, message = "房间名称长度必须在1-128个字符之间")
        String roomName,

        @Schema(description = "分类图标（Emoji）", example = "📱")
        @Size(max = 32, message = "Emoji长度不能超过32个字符")
        String emoji,

        @Schema(description = "排序值（越小越靠前）", example = "1")
        Integer sort,

        @Schema(description = "分类简介", example = "分类简介（测试）")
        @Size(max = 65535, message = "简介长度不能超过65535个字符")
        String summary
) {
    public RoomInsertDTO {
        // Emoji 默认值：为空时使用默认图标
        emoji = StrUtil.isNotBlank(emoji) ? emoji : "📦";
        // sort 默认值：为空时使用 0
        sort = sort != null ? sort : 0;
        // 简介默认值：如果为空或空白，设置为默认值
        summary = StrUtil.isNotBlank(summary) ? summary : "该房间暂无简介";
    }
}
