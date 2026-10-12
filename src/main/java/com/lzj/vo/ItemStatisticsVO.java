package com.lzj.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/** @author 李兆基 */
@Schema(name = "物品统计VO")
@Data
public class ItemStatisticsVO implements Serializable {
    @Schema(description = "总价值")
    private Double totalPrice;
    @Schema(description = "总数量")
    private Long totalCount;
    @Schema(description = "不同状态的物品数量")
    private Map<Integer, Long> statusCountMap;
    @Schema(description = "不同去向的物品数量")
    private Map<Integer, Long> outcomeCountMap;
}
