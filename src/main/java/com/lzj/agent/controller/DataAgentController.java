package com.lzj.agent.controller;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.flow.agent.LlmRoutingAgent;
import com.alibaba.cloud.ai.graph.streaming.OutputType;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.UUID;

/** @author 周航宇 */
@RestController
@RequestMapping("/api/v1/dataAgent")
public class DataAgentController {

    private final LlmRoutingAgent routingAgent;

    public DataAgentController(@Qualifier("routingAgent") LlmRoutingAgent routingAgent) {
        this.routingAgent = routingAgent;
    }

    @SneakyThrows
    @GetMapping("/chat")
    public Flux<String> chat(@RequestParam("input") String input, HttpServletRequest request) {
        
        // 取出用户的 ID
        String userId = (String) request.getAttribute("userId");
        
        // 增加用户 ID 到输入中
        input = "【userId=" + userId + "】 用户输入: " + input;
        
        // 执行路由 Agent，使用随机的 threadId 进行隔离
        return routingAgent.stream(input, RunnableConfig.builder().threadId(UUID.randomUUID().toString()).build())
                .ofType(StreamingOutput.class)
                .filter(output -> output.getOutputType() == OutputType.AGENT_MODEL_STREAMING)
                .map(StreamingOutput::message)
                .ofType(AssistantMessage.class)
                .mapNotNull(AssistantMessage::getText)
                .filter(Objects::nonNull)
                .concatWith(Flux.just("[over]"));
    }
}
