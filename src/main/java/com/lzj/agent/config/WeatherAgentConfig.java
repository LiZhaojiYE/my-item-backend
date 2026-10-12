package com.lzj.agent.config;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/** @author 周航宇 */
@Slf4j
@Configuration
public class WeatherAgentConfig {

    private static final String NAME = "weatherAgent";
    private static final String SYSTEM_PROMPT = """
            # 角色定位
            你是天气查询专用智能体，仅负责调用高德地图MCP工具获取实时天气数据即可。
            # 输入规范
            - 如果提取不到城市名 city，直接回复“请提供您所在的城市”，禁止调用工具；
            # 输出规范
            - 获取到天气数据后，整理润色成专业播报的一段文本字符串，便于后续穿搭智能体读取；
            - 禁止输出JSON格式的字符串，代码块等，也不要展示推理和思考的过程；
            - 回复尽量简洁，尽量不超过200个字，给出天气信息即可；
            """;
    private final String OUTPUT_KEY = "WEATHER_AGENT_OUTPUT";
    
    @Bean(NAME)
    public ReactAgent weatherAgent(ChatModel chatModel, ToolCallbackProvider toolCallbackProvider) {
        log.info("✅初始化智能体: {}", NAME);
        return ReactAgent.builder()
                .model(chatModel)
                .name(NAME)
                .systemPrompt(SYSTEM_PROMPT)
                .outputKey(OUTPUT_KEY)
                .toolExecutionTimeout(Duration.ofMinutes(5))
                .toolCallbackProviders(toolCallbackProvider)
                .build();
    }
}
