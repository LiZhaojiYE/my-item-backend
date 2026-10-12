package com.lzj.agent.tool;
import cn.hutool.core.collection.CollUtil;
import com.lzj.entity.Item;
import com.lzj.mapper.ItemMapper;
import com.mybatisflex.core.query.QueryChain;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.lzj.entity.table.ItemTableDef.ITEM;

/** @author 周航宇 */
@Slf4j
@Component
public class OutfitTool {

    @Resource
    private ItemMapper itemMapper;

    @Tool(name = "queryOutfit", description = "根据用户ID查询该用户的所有穿搭数据")
    public String queryOutfit(@ToolParam(description = "用户ID") String userId) {
        log.info("✅执行 OutfitTool → queryOutfit() 工具，获取到用户ID：{}", userId);
        // 获取用户所有穿搭列表
        List<Item> items = QueryChain.of(itemMapper)
                .select(ITEM.ITEM_NAME, ITEM.SUMMARY)
                .where(ITEM.REMARK.eq("穿搭"))
                .and(ITEM.USER_ID.eq(userId))
                .orderBy(ITEM.USAGE_SCORE.desc())
                .limit(30) // 最多取 30 件，避免撑爆大模型
                .list();

        if (CollUtil.isEmpty(items)) {
            return "当前用户没有穿搭物品。";
        }

        // 拼接成简洁的字符串
        String result = items.stream()
                .map(item -> item.getItemName() + "：" + item.getSummary())
                .collect(Collectors.joining("；"));
        log.info("✅执行 OutfitTool → queryOutfit() 工具，返回穿搭描述：{}", result);

        return result;
    }
}
