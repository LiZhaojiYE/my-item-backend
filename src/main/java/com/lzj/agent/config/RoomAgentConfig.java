package com.lzj.agent.config;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.skills.SkillsAgentHook;
import com.alibaba.cloud.ai.graph.skills.registry.SkillRegistry;
import com.lzj.agent.hook.MyLogHook;
import com.lzj.agent.tool.RoomTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/** @author 周航宇 */
@Slf4j
@Configuration
public class RoomAgentConfig {

    private static final String NAME = "roomAgent";
    private final String SYSTEM_PROMPT = """
            # 角色定位
            你是房间管理专员，负责查询房间相关信息。
            # 输入规则
            - 你必须分析用户意图，然后调用 skill 中定义的对应工具，禁止直接回答用户问题；
            - userId必须从用户输入中直接提取，格式为【userId=userId】，不存在则直接文字提示用户补充，禁止调用工具；
            # 输出规则
            - 必须直接返回工具调用的结果，不做任何处理，结果中的 <br/> 标记必须保留；
            """;

    @Bean(NAME)
    public ReactAgent skillAgent(ChatModel chatModel,
                                 SkillRegistry skillRegistry,
                                 RoomTool roomTool,
                                 MyLogHook myLogHook) {
        log.info("✅初始化智能体: {}", NAME);
        
        // 加载 RoomTool 工具
        List<ToolCallback> roomToolCallbacks = List.of(MethodToolCallbackProvider.builder()
                .toolObjects(roomTool)
                .build()
                .getToolCallbacks());
        roomToolCallbacks.forEach(callback -> log.info("✅加载工具：{}", callback.getToolDefinition().name()));
        
        // 创建钩子
        SkillsAgentHook skillsAgentHook = SkillsAgentHook.builder()
                .skillRegistry(skillRegistry)
                .groupedTools(Map.of("room-skills", roomToolCallbacks))
                .build();
        
        // 创建智能体
        return ReactAgent.builder()
                .name(NAME)
                .systemPrompt(SYSTEM_PROMPT)
                .model(chatModel)
                .hooks(skillsAgentHook, myLogHook) // 添加钩子
                .toolExecutionTimeout(Duration.ofMinutes(5)) // 增加超时时间
                .build();
    }
}
