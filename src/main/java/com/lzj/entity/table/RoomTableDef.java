package com.lzj.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.io.Serial;

/**
 * 房间表 表定义层。
 *
 * @author 李兆基
 * @since v1.0.0
 */
public class RoomTableDef extends TableDef {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 房间表
     */
    public static final RoomTableDef ROOM = new RoomTableDef();

    /**
     * 主键
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 排序值（越小越靠前）
     */
    public final QueryColumn SORT = new QueryColumn(this, "sort");

    /**
     * 房间图标（Emoji）
     */
    public final QueryColumn EMOJI = new QueryColumn(this, "emoji");

    /**
     * 外键（用户表）
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 房间简介
     */
    public final QueryColumn SUMMARY = new QueryColumn(this, "summary");

    /**
     * 数据版本（乐观锁版本号）
     */
    public final QueryColumn VERSION = new QueryColumn(this, "version");

    /**
     * 房间名称
     */
    public final QueryColumn ROOM_NAME = new QueryColumn(this, "room_name");

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
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, USER_ID, ROOM_NAME, EMOJI, SORT, SUMMARY, VERSION, CREATE_TIME, UPDATE_TIME};

    public RoomTableDef() {
        super("myitem", "room");
    }

    private RoomTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public RoomTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new RoomTableDef("myitem", "room", alias));
    }

}
