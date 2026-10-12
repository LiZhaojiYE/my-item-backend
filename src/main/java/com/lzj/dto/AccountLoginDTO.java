package com.lzj.dto;
import com.lzj.util.RegexUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** @author 李兆基 */
@Schema(name = "账号登录DTO", description = "账号密码登录请求参数")
public record AccountLoginDTO(
        @Schema(description = "登录账号", example = "lizj")
        @NotBlank(message = "登录账号不能为空")
        @Size(min = 4, max = 64, message = "登录账号长度必须在4-64个字符之间")
        @Pattern(regexp = RegexUtil.USERNAME_REGEX, message = RegexUtil.USERNAME_REGEX_MSG)
        String username,

        @Schema(description = "登录密码", example = "lizhaoji123")
        @NotBlank(message = "登录密码不能为空")
        @Size(min = 8, max = 128, message = "密码长度必须在8-128个字符之间")
        String password
) {}
