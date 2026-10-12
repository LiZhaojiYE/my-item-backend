package com.lzj.agent.advisor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/** @author 周航宇 */
@Slf4j
@Component
@SuppressWarnings("all")
public class RedisAdvisor implements BaseAdvisor {

    private final RedissonClient redissonClient;

    public RedisAdvisor(RedissonClient redissonClient){
        this.redissonClient = redissonClient;
    }

    /** Redis Key 前缀 */
    @Value("${myitem.memory.key-prefix}")
    private String KEY_PREFIX;
    /** 对话 ID 键名 */
    @Value("${myitem.memory.conversation-id}")
    private String CONVERSATION_ID;
    /** 默认对话 ID */
    @Value("${myitem.memory.default-conversation-id}")
    private String DEFAULT_CONVERSATION_ID;
    /** 滑动窗口最大消息数（用户 + AI 各算 1 条） */
    @Value("${myitem.memory.max-messages}")
    private int MAX_MESSAGES;
    /** 记忆过期时间（分钟）：30 分钟无操作自动删除 */
    @Value("${myitem.memory.expire}")
    private long EXPIRE;

    /**
     * AI 调用前执行：
     * 1. 从 Redis 中读取历史消息
     * 2. 将本次用户消息加入到历史消息
     * 3. 重新构建提示词和请求（包含最新的历史消息）
     * 4. 返回新请求
     */
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {

        // 获取会话 ID
        String conversationId = this.getConversationId(chatClientRequest.context(), DEFAULT_CONVERSATION_ID);

        // 通过会话 ID 从 Redis 读取历史消息【M】，该列表用于存取记忆消息
        String key = KEY_PREFIX + conversationId;
        RList<Message> memoryMessages = redissonClient.getList(key);
        log.info("✅【RedisAdvisor】获取 Redis 历史消息数量：memoryMessagesSize={}", memoryMessages.size());

        // 创建消息列表【P】，内容和【M】一样，但功能不同，该列表仅用于更新提示词和请求
        List<Message> processedMessages = new ArrayList<>(memoryMessages);

        // 将本次的请求中的用户消息（列表类型）加入到消息列表【P】中
        List<Message> userMessages = chatClientRequest.prompt().getInstructions();
        processedMessages.addAll(userMessages);

        // 更新本次请求（ChatClientRequest 是只读对象，必须使用 mutate 构建新对象）
        ChatClientRequest processedChatClientRequest = chatClientRequest.mutate().prompt(
                // 更新本次请求中的提示词（Prompt 是只读对象，必须使用 mutate 构建新对象）
                chatClientRequest.prompt().mutate().messages(processedMessages).build()
        ).build();

        // 从更新后的请求中获取用户消息（单条消息）加入到消息列表【M】中
        UserMessage userMessage = processedChatClientRequest.prompt().getUserMessage();
        memoryMessages.add(userMessage);
        memoryMessages.expire(EXPIRE, TimeUnit.MINUTES);
        log.info("✅【RedisAdvisor】更新 Redis 历史消息数量：memoryMessagesSize={}", memoryMessages.size());

        // 返回处理后的本次请求对象
        return processedChatClientRequest;
    }

    /**
     * AI 调用后执行：
     * 1. 从 Redis 中读取历史消息
     * 2. 将本次 AI 消息加入到历史消息
     * 3. 滑动窗口裁剪，只保留最新 MAX_MESSAGES 条消息
     * 4. 刷新过期时间（每次请求自动续期）
     * 5. 返回响应
     */
    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {

        // 获取会话 ID
        String conversationId = this.getConversationId(chatClientResponse.context(), DEFAULT_CONVERSATION_ID);

        // 通过会话 ID 从 Redis 读取历史消息
        String key = KEY_PREFIX + conversationId;
        RList<Message> memoryMessages = redissonClient.getList(key);
        log.info("✅【RedisAdvisor】获取 Redis 历史消息数量：memoryMessagesSize={}", memoryMessages.size());

        // 获取 AI 回复消息
        AssistantMessage assistantMessage = chatClientResponse.chatResponse().getResult().getOutput();

        // 将 AI 回复的消息（单条消息）加入到历史消息中
        memoryMessages.add(assistantMessage);
        log.info("✅【RedisAdvisor】更新 Redis 历史消息数量：memoryMessagesSize={}", memoryMessages.size());

        // 滑动窗口：只保留最新 MAX_MESSAGES 条，防止上下文过长
        if (memoryMessages.size() > MAX_MESSAGES) {
            List<Message> latestMessages = memoryMessages.subList(memoryMessages.size() - MAX_MESSAGES, memoryMessages.size());
            memoryMessages.clear();
            memoryMessages.addAll(latestMessages);
            log.info("✅【RedisAdvisor】更新 Redis 历史消息：滑动窗口裁剪，保留最新 {} 条消息", MAX_MESSAGES);
        }
        
        // 刷新过期时间（每次请求自动续期）
		memoryMessages.expire(EXPIRE, TimeUnit.MINUTES);
		log.info("✅【RedisAdvisor】刷新 Redis 历史消息过期时间");

        // 返回响应
        return chatClientResponse;
    }

    /** Advisor 唯一名称 */
    @Override
    public String getName() {
        return "RedisAdvisor";
    }

    /** 执行顺序：数字越小，越先执行 */
    @Override
    public int getOrder() {
        return 0;
    }

    /** 获取会话 ID */
    private String getConversationId(Map<String, Object> context, String defaultConversationId) {
        Assert.notNull(context, "context cannot be null");
        Assert.noNullElements(context.keySet().toArray(), "context cannot contain null keys");
        Assert.hasText(defaultConversationId, "defaultConversationId cannot be null or empty");
        // 若请求中包含 key="conversationId"，则返回对应的 value
        // 若不包含，则返回默认的 "default"：参考 ChatMemory → DEFAULT_CONVERSATION_ID
        return context.containsKey(CONVERSATION_ID) ? context.get(CONVERSATION_ID).toString() : defaultConversationId;
    }
}
