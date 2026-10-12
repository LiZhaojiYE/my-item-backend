package com.lzj.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.io.Serial;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 物品表 实体类。
 *
 * @author 李兆基
 * @since v1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "物品表")
@Table(value = "item", schema = "myitem")
public class Item implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "主键")
    private Long id;

    /**
     * 外键（用户表）
     */
    @Schema(description = "外键（用户表）")
    private Long userId;

    /**
     * 外键（物品分类表）
     */
    @Schema(description = "外键（物品分类表）")
    private Long itemCategoryId;

    /**
     * 外键（位置表）
     */
    @Schema(description = "外键（位置表）")
    private Long locationId;

    /**
     * 物品名称
     */
    @Schema(description = "物品名称")
    private String itemName;

    /**
     * 物品图片（URL地址）
     */
    @Schema(description = "物品图片（URL地址）")
    private String pictureUrl;

    /**
     * 物品标签（逗号分隔）
     */
    @Schema(description = "物品标签（逗号分隔）")
    private String tags;

    /**
     * 排序值（越小越靠前）
     */
    @Schema(description = "排序值（越小越靠前）")
    private Integer sort;

    /**
     * 物品简介（用户对物品的介绍）
     */
    @Schema(description = "物品简介（用户对物品的介绍）")
    private String summary;

    /**
     * 物品备注（系统生成的AI标识）
     */
    @Schema(description = "物品备注（系统生成的AI标识）")
    private String remark;

    /**
     * 物品价格
     */
    @Schema(description = "物品价格")
    private Double price;

    /**
     * 物品数量
     */
    @Schema(description = "物品数量")
    private Integer count;

    /**
     * 物品使用率评分（1~5颗星，0表示未评级）
     */
    @Schema(description = "物品使用率评分（1~5颗星，0表示未评级）")
    private Integer usageScore;

    /**
     * 物品性价比评分（1~5颗星，0表示未评级）
     */
    @Schema(description = "物品性价比评分（1~5颗星，0表示未评级）")
    private Integer valueScore;

    /**
     * 物品状态（0=服役中 1=闲置中 2=出借中 3=维修中）
     */
    @Schema(description = "物品状态（0=服役中 1=闲置中 2=出借中 3=维修中）")
    private Integer itemStatus;

    /**
     * 物品去向（0=持有中 1=已消耗 2=已丢弃 3=已丢失 4=已转赠 5=已售出）
     */
    @Schema(description = "物品去向（0=持有中 1=已消耗 2=已丢弃 3=已丢失 4=已转赠 5=已售出）")
    private Integer itemOutcome;

    /**
     * 购入时间
     */
    @Schema(description = "购入时间")
    private LocalDate buyDate;

    /**
     * 状态变更时间
     */
    @Schema(description = "状态变更时间")
    private LocalDateTime statusChangeTime;

    /**
     * 去向变更时间
     */
    @Schema(description = "去向变更时间")
    private LocalDateTime outcomeChangeTime;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private LocalDate expireDate;

    /**
     * 数据版本（乐观锁版本号）
     */
    @Column(version = true)
    @Schema(description = "数据版本（乐观锁版本号）")
    private Integer version;

    /**
     * 逻辑删除（0=否 1=是）
     */
    @Column(isLogicDelete = true)
    @Schema(description = "逻辑删除（0=否 1=是）")
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
