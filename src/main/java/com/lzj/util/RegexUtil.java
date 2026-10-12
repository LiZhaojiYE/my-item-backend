package com.lzj.util;

/** @author 李兆基 */
public interface RegexUtil {
    String USERNAME_REGEX = "^[a-zA-Z][a-zA-Z0-9_]{3,63}$";
    String USERNAME_REGEX_MSG = "登录账号必须以字母开头，只能包含字母、数字、下划线";
    String NICKNAME_REGEX = "^[\\u4e00-\\u9fa5a-zA-Z0-9_\\s]{1,128}$";
    String NICKNAME_REGEX_MSG = "昵称只能包含中文、字母、数字、下划线和空格";
    String CATEGORY_NAME_REGEX = "^[\\u4e00-\\u9fa5a-zA-Z0-9_\\s]{1,128}$";
    String CATEGORY_NAME_REGEX_MSG = "物品分类名称只能包含中文、字母、数字、下划线和空格，长度1-128个字符";
    String ROOM_NAME_REGEX = "^[\\u4e00-\\u9fa5a-zA-Z0-9_\\s]{1,128}$";
    String ROOM_NAME_REGEX_MSG = "房间名称只能包含中文、字母、数字、下划线和空格，长度1-128个字符";
    String LOCATION_NAME_REGEX = "^[\\u4e00-\\u9fa5a-zA-Z0-9_\\s]{1,128}$";
    String LOCATION_NAME_REGEX_MSG = "位置名称只能包含中文、字母、数字、下划线和空格，长度1-128个字符";
    String ITEM_NAME_REGEX = "^[\\u4e00-\\u9fa5a-zA-Z0-9_\\s]{1,128}$";
    String ITEM_NAME_REGEX_MSG = "物品名称只能包含中文、字母、数字、下划线和空格，长度1-128个字符";
}
