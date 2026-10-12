package com.lzj.agent.config;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.lzj.agent.tool.OutfitTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/** @author 周航宇 */
@Slf4j  
@Configuration  
public class OutfitAgentConfig {  
  
    private static final String NAME = "outfitAgent";  
    private static final String SYSTEM_PROMPT = """  
            # 角色定位  
            你是穿搭查询专用智能体，仅负责查询用户的所有穿搭数据。  
            # 输入规范  
            - userId必须从用户输入中直接提取，格式为【userId=userId】，不存在则直接文字提示用户补充，禁止调用工具；  
            # 输出规范  
            - 必须调用本地工具 queryOutfit 来获取最终的查询结果，禁止使用模型内置知识编造穿搭数据；  
            - 直接返回工具调用结果，无需额外修饰；  
            """;  
    private final String OUTPUT_KEY = "OUTFIT_AGENT_OUTPUT";  
  
    @Bean(NAME)  
    public ReactAgent outfitAgent(ChatModel chatModel, OutfitTool outfitTool) {  
        log.info("✅初始化智能体: {}", NAME);  
        // 创建智能体  
        return ReactAgent.builder()  
                .model(chatModel)  
                .name(NAME)  
                .systemPrompt(SYSTEM_PROMPT)  
                .outputKey(OUTPUT_KEY)  
                .toolExecutionTimeout(Duration.ofMinutes(5))  
                .tools(ToolCallbacks.from(outfitTool)) // 引入本地工具  
                .build();  
    }  
}
