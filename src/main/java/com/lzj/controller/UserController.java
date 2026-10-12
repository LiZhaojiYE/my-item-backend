package com.lzj.controller;

import com.lzj.dto.AccountLoginDTO;
import com.lzj.dto.UserInsertDTO;
import com.lzj.dto.UserUpdateDTO;
import com.lzj.dto.UserUpdatePasswordDTO;
import com.lzj.entity.User;
import com.lzj.service.UserService;
import com.lzj.vo.AccountLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户表 控制层。
 *
 * @author 李兆基
 * @since v1.0.0
 */
@RestController
@Tag(name = "用户表接口")
@RequestMapping("/api/v1/user")
public class UserController {

    @Resource
    private UserService userService;
    @PostMapping("insert")
    @Operation(summary = "注册新用户")
    public boolean insert(@RequestBody @Validated @Parameter(description = "用户添加DTO") UserInsertDTO dto) {
        userService.insert(dto);
        return true;
    }

    @PostMapping("loginByAccount")
    @Operation(summary = "通过账号和密码登录系统")
    public AccountLoginVO loginByAccount(@RequestBody @Validated @Parameter(description = "账号密码登录DTO") AccountLoginDTO dto) {
        return userService.loginByAccount(dto);
    }

    @GetMapping("select")
    @Operation(summary = "查看个人信息")
    public User select(HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        return userService.select(userId);
    }

    @PutMapping("update")
    @Operation(summary = "修改个人信息")
    public boolean update(@RequestBody @Validated @Parameter(description = "用户修改DTO") UserUpdateDTO dto,
                          HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        userService.update(userId, dto);
        return true;
    }

    @PutMapping("updatePassword")
    @Operation(summary = "修改个人密码")
    public boolean updatePassword(@RequestBody @Validated @Parameter(description = "用户修改密码DTO") UserUpdatePasswordDTO dto,
                                  HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        userService.updatePassword(userId, dto);
        return true;
    }

    @PostMapping("uploadAvatar")
    @Operation(summary = "上传头像")
    public String uploadAvatar(@RequestParam("avatar") @Parameter(description = "头像文件") MultipartFile avatarFile,
                               HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        return userService.uploadAvatar(userId, avatarFile);
    }

    @DeleteMapping("delete")
    @Operation(summary = "注销用户")
    public boolean delete(HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        userService.delete(userId);
        return true;
    }

}
