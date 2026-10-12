package com.lzj.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 房间表 实体类。
 *
 * @author 李兆基
 * @since v1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "房间表")
@Table(value = "room", schema = "myitem")
public class Room implements Serializable {

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
     * 房间名称
     */
    @Schema(description = "房间名称")
    private String roomName;

    /**
     * 房间图标（Emoji）
     */
    @Schema(description = "房间图标（Emoji）")
    private String emoji;

    /**
     * 排序值（越小越靠前）
     */
    @Schema(description = "排序值（越小越靠前）")
    private Integer sort;

    /**
     * 房间简介
     */
    @Schema(description = "房间简介")
    private String summary;

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
