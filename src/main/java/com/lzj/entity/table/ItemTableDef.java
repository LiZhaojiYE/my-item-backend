package com.lzj.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.io.Serial;

/**
 * 物品表 表定义层。
 *
 * @author 李兆基
 * @since v1.0.0
 */
public class ItemTableDef extends TableDef {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 物品表
     */
    public static final ItemTableDef ITEM = new ItemTableDef();

    /**
     * 主键
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 排序值（越小越靠前）
     */
    public final QueryColumn SORT = new QueryColumn(this, "sort");

    /**
     * 物品标签（逗号分隔）
     */
    public final QueryColumn TAGS = new QueryColumn(this, "tags");

    /**
     * 物品数量
     */
    public final QueryColumn COUNT = new QueryColumn(this, "count");

    /**
     * 物品价格
     */
    public final QueryColumn PRICE = new QueryColumn(this, "price");

    /**
     * 物品备注（系统生成的AI标识）
     */
    public final QueryColumn REMARK = new QueryColumn(this, "remark");

    /**
     * 外键（用户表）
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 购入时间
     */
    public final QueryColumn BUY_DATE = new QueryColumn(this, "buy_date");

    /**
     * 物品简介（用户对物品的介绍）
     */
    public final QueryColumn SUMMARY = new QueryColumn(this, "summary");

    /**
     * 数据版本（乐观锁版本号）
     */
    public final QueryColumn VERSION = new QueryColumn(this, "version");

    /**
     * 物品名称
     */
    public final QueryColumn ITEM_NAME = new QueryColumn(this, "item_name");

    /**
     * 逻辑删除（0=否 1=是）
     */
    public final QueryColumn IS_DELETED = new QueryColumn(this, "is_deleted");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 过期时间
     */
    public final QueryColumn EXPIRE_DATE = new QueryColumn(this, "expire_date");

    /**
     * 物品状态（0=服役中 1=闲置中 2=出借中 3=维修中）
     */
    public final QueryColumn ITEM_STATUS = new QueryColumn(this, "item_status");

    /**
     * 外键（位置表）
     */
    public final QueryColumn LOCATION_ID = new QueryColumn(this, "location_id");

    /**
     * 物品图片（URL地址）
     */
    public final QueryColumn PICTURE_URL = new QueryColumn(this, "picture_url");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 物品使用率评分（1~5颗星，0表示未评级）
     */
    public final QueryColumn USAGE_SCORE = new QueryColumn(this, "usage_score");

    /**
     * 物品性价比评分（1~5颗星，0表示未评级）
     */
    public final QueryColumn VALUE_SCORE = new QueryColumn(this, "value_score");

    /**
     * 物品去向（0=持有中 1=已消耗 2=已丢弃 3=已丢失 4=已转赠 5=已售出）
     */
    public final QueryColumn ITEM_OUTCOME = new QueryColumn(this, "item_outcome");

    /**
     * 外键（物品分类表）
     */
    public final QueryColumn ITEM_CATEGORY_ID = new QueryColumn(this, "item_category_id");

    /**
     * 状态变更时间
     */
    public final QueryColumn STATUS_CHANGE_TIME = new QueryColumn(this, "status_change_time");

    /**
     * 去向变更时间
     */
    public final QueryColumn OUTCOME_CHANGE_TIME = new QueryColumn(this, "outcome_change_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, USER_ID, ITEM_CATEGORY_ID, LOCATION_ID, ITEM_NAME, PICTURE_URL, TAGS, SORT, SUMMARY, REMARK, PRICE, COUNT, USAGE_SCORE, VALUE_SCORE, ITEM_STATUS, ITEM_OUTCOME, BUY_DATE, STATUS_CHANGE_TIME, OUTCOME_CHANGE_TIME, EXPIRE_DATE, VERSION, CREATE_TIME, UPDATE_TIME};

    public ItemTableDef() {
        super("myitem", "item");
    }

    private ItemTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public ItemTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new ItemTableDef("myitem", "item", alias));
    }

}
