package com.lzj.agent.tool;
import cn.hutool.core.util.ObjectUtil;
import com.lzj.entity.Item;
import com.lzj.mapper.ItemCategoryMapper;
import com.lzj.mapper.ItemMapper;
import com.mybatisflex.core.query.QueryChain;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

import static com.lzj.entity.table.ItemCategoryTableDef.ITEM_CATEGORY;
import static com.lzj.entity.table.ItemTableDef.ITEM;

/** @author 周航宇 */
@Slf4j
@Component
public class ItemTool {

    @Resource
    private ItemMapper itemMapper;
    @Resource
    private ItemCategoryMapper itemCategoryMapper;

    @Tool(name = "queryItemDetail", description = "根据物品名称查询该物品的完整详细信息")
    public String queryItemDetail(
            @ToolParam(description = "用户ID") String userId,
            @ToolParam(description = "物品名称") String itemName) {
        log.info("✅调用 queryItemDetail，参数：userId={}, itemName={}", userId, itemName);
        // 查询物品详细信息
        Item item = QueryChain.of(itemMapper)
                .where(ITEM.ITEM_NAME.like(itemName))
                .and(ITEM.USER_ID.eq(userId))
                .one();
        if (ObjectUtil.isNull(item)) {
            return "未查询到物品详细信息" + itemName;
        }

        // 查询物品详细信息
        String itemCategoryName = QueryChain.of(itemCategoryMapper)
                .select(ITEM_CATEGORY.CATEGORY_NAME)
                .where(ITEM_CATEGORY.ID.eq(item.getItemCategoryId()))
                .objAs(String.class);

        // 处理查询结果
        String result = """
                名称：%s<br/>
                类别：%s<br/>
                标签：%s<br/>
                简介：%s<br/>
                价格：%s元<br/>
                库存：%s<br/>
                使用率：%s颗星<br/>
                性价比：%s颗星<br/>
                购入日期：%s<br/>
                过期日期：%s<br/>
                物品状态：%s<br/>
                状态变更：%s<br/>
                物品去向：%s<br/>
                去向变更：%s<br/>
                """.formatted(
                item.getItemName(),
                itemCategoryName,
                item.getTags(),
                item.getSummary(),
                item.getPrice(),
                item.getCount(),
                item.getUsageScore(),
                item.getValueScore(),
                ObjectUtil.isNull(item.getBuyDate()) ? "无" : item.getBuyDate().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")),
                ObjectUtil.isNull(item.getExpireDate()) ? "无" : item.getExpireDate().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")),
                this.formatItemStatus(item.getItemStatus()),
                ObjectUtil.isNull(item.getStatusChangeTime()) ? "无" : item.getStatusChangeTime().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH时mm分")),
                this.formatItemOutcome(item.getItemOutcome()),
                ObjectUtil.isNull(item.getOutcomeChangeTime()) ? "无" : item.getOutcomeChangeTime().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH时mm分"))
                
        );
        log.info("✅调用 queryItemDetail，结果：result={}", result);
        return result;
    }

    private String formatItemStatus(Integer itemStatusCode) {
        if (0 == itemStatusCode) return "服役中";
        if (1 == itemStatusCode) return "闲置中";
        if (2 == itemStatusCode) return "出借中";
        if (3 == itemStatusCode) return "维修中";
        return "未知状态";
    }

    private String formatItemOutcome(Integer itemOutcomeCode) {
        if (0 == itemOutcomeCode) return "持有中";
        if (1 == itemOutcomeCode) return "已消耗";
        if (2 == itemOutcomeCode) return "已丢弃";
        if (3 == itemOutcomeCode) return "已丢失";
        if (4 == itemOutcomeCode) return "已转赠";
        if (5 == itemOutcomeCode) return "已售出";
        return "未知状态";
    }
}
