package com.lzj.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.io.Serial;

/**
 * 用户表 表定义层。
 *
 * @author 李兆基
 * @since v1.0.0
 */
public class UserTableDef extends TableDef {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户表
     */
    public static final UserTableDef USER = new UserTableDef();

    /**
     * 主键
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 用户简介
     */
    public final QueryColumn SUMMARY = new QueryColumn(this, "summary");

    /**
     * 数据版本（乐观锁版本号）
     */
    public final QueryColumn VERSION = new QueryColumn(this, "version");

    /**
     * 用户昵称
     */
    public final QueryColumn NICKNAME = new QueryColumn(this, "nickname");

    /**
     * 登录账号
     */
    public final QueryColumn USERNAME = new QueryColumn(this, "username");

    /**
     * 用户头像（URL地址）
     */
    public final QueryColumn AVATAR_URL = new QueryColumn(this, "avatar_url");

    /**
     * 逻辑删除（0=否 1=是）
     */
    public final QueryColumn IS_DELETED = new QueryColumn(this, "is_deleted");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 登录密码（哈希值）
     */
    public final QueryColumn PASSWORD_HASH = new QueryColumn(this, "password_hash");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, USERNAME, PASSWORD_HASH, NICKNAME, AVATAR_URL, SUMMARY, VERSION, CREATE_TIME, UPDATE_TIME};

    public UserTableDef() {
        super("myitem", "user");
    }

    private UserTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public UserTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new UserTableDef("myitem", "user", alias));
    }

}
