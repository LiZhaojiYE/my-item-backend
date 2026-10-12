package com.lzj.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** @author 李兆基 */
@Schema(name = "用户修改密码DTO", description = "用户密码修改请求参数")
public record UserUpdatePasswordDTO(

        @Schema(description = "登录密码（旧）", example = "lizhaoji123")
        @NotBlank(message = "旧登录密码不能为空")
        @Size(min = 8, max = 128, message = "密码长度必须在8-128个字符之间")
        String oldPassword,

        @Schema(description = "登录密码（新）", example = "lizhaoji456")
        @NotBlank(message = "新登录密码不能为空")
        @Size(min = 8, max = 128, message = "密码长度必须在8-128个字符之间")
        String newPassword
) {}
