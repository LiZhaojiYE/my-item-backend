package com.lzj.agent.controller;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.flow.agent.SupervisorAgent;
import com.lzj.component.MyRedis;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/** @author 周航宇 */
@Slf4j
@RestController
@RequestMapping("/api/v1/recommend")
public class RecommendController {

    private final SupervisorAgent supervisorAgent;
    private final ChatClient chatClient;
    private final String SYSTEM_PROMPT = """
            # 角色定位
            你是一个穿搭推荐助手，根据以下信息推荐穿衣搭配：
            - 今日天气信息：%s
            - 用户目前拥有的穿搭物品信息：%s
            # 输出规则
            - 只能根据用户目前拥有的服装进行搭配推荐；
            - 回复尽量简洁，尽量不超过200个字，给出一套推荐方案和原因即可；
            - 如果用户目前没有合适的穿搭物品，直接文字提示用户即可；
            """;
    private final MyRedis myRedis;

    public RecommendController(@Qualifier("supervisorAgent") SupervisorAgent supervisorAgent, ChatModel chatModel, MyRedis myRedis) {
        this.supervisorAgent = supervisorAgent;
        this.chatClient = ChatClient.builder(chatModel).build();
        this.myRedis = myRedis;
    }

    @SneakyThrows
    @GetMapping("/exe")
    public Map<String, Object> exe(@RequestParam("city") String city, HttpServletRequest request) {

        // 取出用户的 ID
        String userId = request.getAttribute("userId").toString();

        // 组装用户输入
        String input = "根据%s今日的天气信息获取今日推荐，userId=%s".formatted(city, userId);

        // 尝试从缓存中获取推荐信息
        String recommendWeatherKey = "myitem:recommend-weather:%s:%s:%s".formatted(userId, city, LocalDate.now().toString());
        String recommendOutfitKey = "myitem:recommend-outfit:%s:%s:%s".formatted(userId, city, LocalDate.now().toString());
        if (myRedis.exists(recommendWeatherKey) && myRedis.exists(recommendOutfitKey)) {
            String recommendWeatherFromRedis = myRedis.get(recommendWeatherKey);
            String recommendOutfitFromRedis = myRedis.get(recommendOutfitKey);
            log.info("✅从缓存中获取到推荐信息：{}", recommendWeatherFromRedis);
            log.info("✅从缓存中获取到推荐信息：{}", recommendOutfitFromRedis);
            return Map.of("weather", recommendWeatherFromRedis, "outfit", recommendOutfitFromRedis);
        }

        try {
            // 调用Agent
            OverAllState state = supervisorAgent.invoke(input, RunnableConfig.builder().threadId(UUID.randomUUID().toString()).build())
                    .orElseThrow();

            // 提取两个子 Agent 的输出
            String weatherAgentOutPut = this.getText(state, "WEATHER_AGENT_OUTPUT");
            String outfitAgentOutPut = this.getText(state, "OUTFIT_AGENT_OUTPUT");
            log.info("✅获取到子 Agent 的输出，天气信息：{}，用户穿搭列表：{}", weatherAgentOutPut, outfitAgentOutPut);

            // 穿搭推荐
            String outfit = chatClient.prompt()
                    .system(SYSTEM_PROMPT.formatted(weatherAgentOutPut, outfitAgentOutPut))
                    .call()
                    .content();
            if (StrUtil.isBlank(outfit)) {
                outfit = "用户目前没有合适的穿搭物品，无法推荐穿衣搭配。";
            }
            log.info("✅推荐穿衣搭配：{}", outfit);

            // 缓存信息
            myRedis.setEx(recommendWeatherKey, weatherAgentOutPut, 24, TimeUnit.HOURS);
            myRedis.setEx(recommendOutfitKey, outfit, 24, TimeUnit.HOURS);
            log.info("✅将推荐信息缓存到Redis");
            return Map.of("weather", weatherAgentOutPut, "outfit", outfit);
        } catch (Exception e) {
            log.error("❌推荐穿衣搭配失败：{}", e.getMessage());
            return new HashMap<>();
        }
    }

    private String getText(OverAllState state, String key) {
        return state.value(key, AssistantMessage.class).orElseThrow().getText();
    }
}
