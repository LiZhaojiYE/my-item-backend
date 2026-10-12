package com.lzj.agent.hook;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.hook.AgentHook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/** @author 周航宇 */
@Scope("prototype") // 让每个 Agent 都有一个独立的钩子实例，否则会共享状态
@Slf4j
@Component
public class MyLogHook extends AgentHook {

    /**
     * 在 Agent 开始前调用，记录开始时间并初始化状态变量
     *
     * @param state  全局状态，包含用户输入、模型输出等信息
     * @param config 运行配置，包含模型选择、参数设置等
     * @return 包含自定义数据的 CompletableFuture，用于后续处理
     */
    @Override
    public CompletableFuture<Map<String, Object>> beforeAgent(OverAllState state, RunnableConfig config) {
        String agentName = this.getAgentName();
        log.info("✅【{}】开始执行", agentName);
        return CompletableFuture.completedFuture(Map.of("agentName", agentName));
    }

    /**
     * 在 Agent 结束后调用
     *
     * @param state  全局状态，包含用户输入、模型输出等信息
     * @param config 运行配置，包含模型选择、参数设置等
     * @return 包含自定义数据的 CompletableFuture，用于后续处理
     */
    @Override
    public CompletableFuture<Map<String, Object>> afterAgent(OverAllState state, RunnableConfig config) {
        log.info("✅【{}】执行结束", this.getAgentName());
        // 返回空 CompletableFuture，表示没有后续处理需求
        return CompletableFuture.completedFuture(Map.of());
    }

    @Override
    public String getName() {
        return "MyLogHook";
    }
}
