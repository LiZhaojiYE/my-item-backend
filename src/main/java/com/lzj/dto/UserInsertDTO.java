package com.lzj.dto;
import cn.hutool.core.util.StrUtil;
import com.lzj.util.RegexUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** @author 李兆基 */
@Schema(name = "用户添加DTO", description = "用户注册请求参数")
public record UserInsertDTO(
        @Schema(description = "登录账号", example = "lizj")
        @NotBlank(message = "登录账号不能为空")
        @Size(min = 4, max = 64, message = "登录账号长度必须在4-64个字符之间")
        @Pattern(regexp = RegexUtil.USERNAME_REGEX, message = RegexUtil.USERNAME_REGEX_MSG)
        String username,
        
        @Schema(description = "登录密码", example = "lizhaoji123")
        @NotBlank(message = "登录密码不能为空")
        @Size(min = 8, max = 128, message = "密码长度必须在8-128个字符之间")
        String password,

        @Schema(description = "用户昵称", example = "周七不上班")
        @Size(min = 1, max = 128, message = "昵称长度必须在1-128个字符之间")
        @Pattern(regexp = RegexUtil.NICKNAME_REGEX, message = RegexUtil.NICKNAME_REGEX_MSG)
        String nickname,

        @Schema(description = "用户简介", example = "最高级别的系统管理员")
        @Size(max = 256, message = "简介长度不能超过256个字符")
        String summary
) {
    public UserInsertDTO {
        // 昵称默认值：如果为空或空白，使用 username 作为昵称
        nickname = StrUtil.isNotBlank(nickname) ? nickname : username;
        // 简介默认值：如果为空或空白，设置为默认值
        summary = StrUtil.isNotBlank(summary) ? summary : "这个人很懒，还没写简介";
    }
}
