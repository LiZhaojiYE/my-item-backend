package com.lzj.agent.controller;
import com.lzj.agent.util.StUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stringtemplate.v4.ST;
import reactor.core.publisher.Flux;

import java.util.List;

/** @author 周航宇 */
@Slf4j
@RestController
@Tag(name = "AI收纳技巧专员")
@RequestMapping("/api/v1/rag")
public class RagController {

    private final ChatClient chatClient;

    public RagController(ChatClient.Builder chatClientBuilder, @Qualifier("redisVectorStore") VectorStore redisVectorStore) {

        // 创建系统提示词模板
        ST st = StUtil.load("classpath:prompt/ChatPrompt.st");
        st.add("role", "你是一个聊天助手，负责根据 RAG 知识库内容用最精炼的语言和用户进行沟通");
        st.add("input", List.of("所有回答必须基于向量数据库中检索到相关知识进行分析，扩展和总结，并生成回复"));
        st.add("output", List.of(
                "只能输出纯中文文本，严禁输出JSON、代码块或其他格式",
                "若 RAG 知识库中不存在相关知识，直接返回 “我不清楚”"));

        // 创建日志顾问器（自定义需要打印的请求日志内容，响应日志内容和优先级）
        SimpleLoggerAdvisor loggerAdvisor = new SimpleLoggerAdvisor(
                req -> "✅【LogAdvisor】提示词：" + req.prompt().getContents(),
                res -> "✅【LogAdvisor】AI回复：" + res.getResult().getOutput().getText(),
                0
        );

        // 创建问答顾问器
        QuestionAnswerAdvisor questionAnswerAdvisor = QuestionAnswerAdvisor.builder(redisVectorStore)
                .searchRequest(SearchRequest.builder()
                        .topK(2)
                        .similarityThreshold(0.0)
                        .build())
                .build();

        // 创建对话客户端
        this.chatClient = chatClientBuilder
                .defaultSystem(st.render())
                .defaultAdvisors(loggerAdvisor, questionAnswerAdvisor)
                .build();
    }

    @Operation(summary = "AI对话")
    @GetMapping("chat")
    public Flux<String> chat(@RequestParam("input") String input) {
        // 流式发送消息（将响应结果以流的形式推送给前端）
        return chatClient.prompt()
                .user(input)
                .stream()
                .content()
                .concatWith(Flux.just("[over]"));
    }
}
