package com.lzj.agent.config;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.flow.agent.SupervisorAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

/** @author 周航宇 */
@Slf4j
@Configuration
public class SupervisorAgentConfig {
    private final String SUPERVISOR_NAME = "supervisorAgent";
    private final String MAIN_NAME = "mainAgent";
    private final String MAIN_SYSTEM_PROMPT = """
            # 角色定位
            你是调度主管，你的唯一任务就是输出下一步要调用的子Agent名称的JSON数组。
            # 执行规则
            - 请严格按照下面的顺序，只输出JSON数组，不要添加任何解释、空格、换行或其他文字：
                - 第一步输出：["weatherAgent"]
                - 第二步输出：["outfitAgent"]
                - 最后输出：["FINISH"]
            """;
    
    @Bean(SUPERVISOR_NAME)
    public SupervisorAgent supervisorAgent(ChatModel chatModel,
                                           @Qualifier("weatherAgent") ReactAgent weatherAgent,
                                           @Qualifier("outfitAgent") ReactAgent outfitAgent) {
        // 创建主智能体（相当于调度智能体的大脑）
        ReactAgent mainAgent = ReactAgent.builder()
                .model(chatModel)
                .name(MAIN_NAME)
                .toolExecutionTimeout(Duration.ofMinutes(3))
                .systemPrompt(MAIN_SYSTEM_PROMPT)
                .build();
        log.info("✅初始化主智能体 {}", MAIN_NAME);

        // 创建调度智能体
        SupervisorAgent supervisor = SupervisorAgent.builder()
                .name(SUPERVISOR_NAME)
                .mainAgent(mainAgent)
                .subAgents(List.of(weatherAgent, outfitAgent))
                .build();
        log.info("✅初始化调度智能体 {}, 包含 2 个子智能体", SUPERVISOR_NAME);
        return supervisor;
    }
}
