package com.lzj.result;

import lombok.Getter;

/** @author 李兆基 */
@Getter
public enum ResultCode {
    // 成功
    SUCCESS(1000, "请求成功"),
    // 服务器错误
    SERVER_ERROR(2000, "服务器异常"),
    MYSQL_ERROR(2001, "数据操作失败"),
    MINIO_ERROR(2002, "文件操作失败"),
    // 请求参数错误
    ILLEGAL_PARAM(3000, "请求参数错误"),
    ACCOUNT_ILLEGAL(3001, "账号密码错误"),
    OLD_PASSWORD_ILLEGAL(3002, "旧密码错误"),
    DATETIME_ILLEGAL(3003, "时间设定错误"),
    // 记录重复
    REPEAT_RECORD(4000, "记录重复"),
	USERNAME_REPEAT(4001, "登录账号重复"),
    CATEGORY_NAME_REPEAT(4002, "物品分类名称重复"),
    ITEM_NAME_REPEAT(4003, "物品名称重复"),
    ROOM_NAME_REPEAT(4004, "房间名称重复"),
    LOCATION_NAME_REPEAT(4005, "位置名称重复"),
    // 记录不存在
    RECORD_NOT_FOUND(5000, "记录不存在"),
    USER_NOT_FOUND(5001, "用户不存在"),
    ITEM_CATEGORY_NOT_FOUND(5002, "物品分类不存在"),
    ROOM_NOT_FOUND(5003, "房间不存在"),
    LOCATION_NOT_FOUND(5004, "位置不存在"),
    ITEM_NOT_FOUND(5005, "物品不存在"),  
	// 权限不足  
	PERMISSION_DENIED(6000, "权限不足");

    private final int CODE;
    private final String MESSAGE;

    ResultCode(int code, String msg) {
        this.CODE = code;
        this.MESSAGE = msg;
    }
}
