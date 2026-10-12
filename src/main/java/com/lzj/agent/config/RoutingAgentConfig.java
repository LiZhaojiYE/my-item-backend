package com.lzj.agent.config;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.flow.agent.LlmRoutingAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/** @author 周航宇 */
@Slf4j
@Configuration
public class RoutingAgentConfig {

    private final String NAME = "routingAgent";
    private final String SYSTEM_PROMT = """
            # 角色定位
            你是Agent调度专员，负责严格匹配问题并分配对应的Agent专员；
            # 输出规则
            - 你只能输出匹配的Agent专员的名字，不能输出其他内容；
            - 物品相关的问题，如查询物品详情，查询物品位置，查询临期物品等 → itemAgent；
            - 房间相关的问题，如查询房间详情，查询房间内的全部位置，查询房间数量等 → roomAgent；
            """;

    @Bean(NAME)
    public LlmRoutingAgent routingAgent(ChatModel chatModel,
                                        @Qualifier("itemAgent") ReactAgent itemAgent,
                                        @Qualifier("roomAgent") ReactAgent roomAgent) {
        log.info("✅初始化智能体: {}", NAME);
        return LlmRoutingAgent.builder()
                .name(NAME)
                .model(chatModel)
                .systemPrompt(SYSTEM_PROMT)
                .subAgents(List.of(itemAgent, roomAgent)) // 子路由
                .build();
    }
}
