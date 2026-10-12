package com.lzj.dto;

import com.lzj.util.RegexUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** @author 李兆基 */
@Schema(name = "用户修改DTO", description = "用户信息修改请求参数")
public record UserUpdateDTO(

        @Schema(description = "用户昵称", example = "刘能")
        @Size(min = 1, max = 128, message = "昵称长度必须在1-128个字符之间")
        @Pattern(regexp = RegexUtil.NICKNAME_REGEX, message = RegexUtil.NICKNAME_REGEX_MSG)
        String nickname,

        @Schema(description = "用户简介", example = "村副主任")
        @Size(max = 256, message = "简介长度不能超过256个字符")
        String summary
) {}
