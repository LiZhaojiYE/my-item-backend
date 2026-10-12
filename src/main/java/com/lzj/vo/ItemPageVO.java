package com.lzj.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Schema(name = "物品分类VO，分页业务专用")
@Data
public class ItemPageVO implements Serializable {
    @Schema(description = "主键")
    private Long id;
    @Schema(description = "物品名称")
    private String itemName;
    @Schema(description = "物品图片（URL地址）")
    private String pictureUrl;
    @Schema(description = "物品标签（逗号分隔）")
    private String tags;
    @Schema(description = "排序值（越小越靠前）")
    private Integer sort;
    @Schema(description = "物品价格")
    private Double price;
    @Schema(description = "物品数量")
    private Integer count;
    @Schema(description = "购入时间")
    private LocalDate buyDate;
    @Schema(description = "物品状态（0=服役中 1=闲置中 2=出借中 3=维修中）")
    private Integer itemStatus;
    @Schema(description = "物品去向（0=持有中 1=已消耗 2=已丢弃 3=已丢失 4=已转赠 5=已售出）")
    private Integer itemOutcome;
    @Schema(description = "该物品已使用天数")
    private Integer usedDays;
    @Schema(description = "该物品的日均成本")
    private Double dailyCost;

    public void calculateFields() {
        LocalDate buyDate = this.getBuyDate();
        if (buyDate != null) {
            // 计算物品已使用天数
            long days = ChronoUnit.DAYS.between(buyDate, LocalDate.now());
            this.usedDays = (int) Math.max(days, 0);
            // 获取物品价格
            Double price = this.getPrice();
            if (price != null && price > 0 && this.usedDays > 0) {
                // 计算物品的日均成本：物品价格 / 物品已使用天数
                this.dailyCost = Math.round(price / this.usedDays * 100.0) / 100.0;
            } else {
                this.dailyCost = 0.0;
            }
        } else {
            this.usedDays = 0;
            this.dailyCost = 0.0;
        }
    }
}
