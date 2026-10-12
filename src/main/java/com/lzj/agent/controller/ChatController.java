package com.lzj.agent.controller;
import com.lzj.agent.advisor.RedisAdvisor;
import com.lzj.agent.util.StUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stringtemplate.v4.ST;
import reactor.core.publisher.Flux;

import java.util.List;

/** @author 周航宇 */
@RestController
@Tag(name = "AI智能聊天专员")
@RequestMapping("/api/v1/client")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatClientBuilder, RedisAdvisor redisAdvisor) {

        // 创建系统提示词模板
        ST st = StUtil.load("classpath:prompt/ChatPrompt.st");
        st.add("role", "你是一个聊天助手，负责用最精炼的语言和用户进行沟通");
        st.add("input", List.of("必须基于用户的输入来生成回复"));
        st.add("output", List.of("只能输出纯中文文本，严禁输出JSON、代码块或其他格式"));

        // 构建模型配置（运行时配置）
        ChatOptions chatOptions = ChatOptions.builder()
                .model("qwen-max") // 设置模型名称
                .maxTokens(100) // 设置最大令牌数（100 个字符）
                .temperature(0.7) // 设置温度（0.7 = 中等风险）
                .topK(50) // 设置 Top-K 滤波（50 个候选）
                .topP(0.9) // 设置 Top-P 滤波（0.9 = 90% 概率）
                .build(); // 构建模型配置

        // 创建日志顾问器（自定义需要打印的请求日志内容，响应日志内容和优先级）
        SimpleLoggerAdvisor loggerAdvisor = new SimpleLoggerAdvisor(
                req -> "✅【LogAdvisor】提示词：" + req.prompt().getContents(), // 拦截请求，但只打印提示词文本
                res -> "✅【LogAdvisor】AI回复：" + res.getResult().getOutput().getText(), // 拦截响应，但只打印 AI 回复的内容
                0 // 执行优先级：数字越小越先执行（0 = 最高优先级）
        );

        // 创建对话客户端
        this.chatClient = chatClientBuilder
                .defaultSystem(st.render()) // 设置默认系统提示
                .defaultOptions(chatOptions) // 设置默认模型配置
                .defaultAdvisors(loggerAdvisor, redisAdvisor) // 附加顾问器
                .build();
    }

    @Operation(summary = "AI对话")
    @GetMapping("chat")
    public Flux<String> chat(@RequestParam("input") String input, HttpServletRequest request) {

        // 取出用户的 ID
        long userId = Long.parseLong(request.getAttribute("userId").toString());

        // 流式发送消息（将响应结果以流的形式推送给前端）
        return chatClient.prompt()
                .user(input) // 设置用户消息
                .advisors(e -> e.param("conversationId", userId)) // 设置对话 ID：相同 ID 共享同一套记忆
                .stream() // 流式发送消息（将响应结果以流的形式推送给前端）
                .content() // 获取响应内容
                .concatWith(Flux.just("[over]")); // 约定结束标记，方便前端灵活终止 SSE 推送
    }
}
