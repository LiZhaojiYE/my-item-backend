package com.lzj.agent.config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

/** @author 周航宇 */
@Slf4j
@Configuration
public class RedisVectorStoreConfig {

    @Value("${myitem.redis-stack.host}")
    private String REDIS_HOST;
    @Value("${myitem.redis-stack.port}")
    private int REDIS_PORT;
    @Value("${myitem.redis-stack.password}")
    private String REDIS_PASSWORD;
    @Value("${myitem.redis-stack.prefix}")
    private String REDIS_PREFIX;

    @Bean("redisVectorStore")
    public VectorStore redisVectorStore(EmbeddingModel embeddingModel) {

        // 创建独立 Redis Stack 连接
        JedisPooled jedisPooled = new JedisPooled(REDIS_HOST, REDIS_PORT, null, REDIS_PASSWORD);

        // 创建 RedisVectorStore 实例
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .prefix(REDIS_PREFIX)
                .initializeSchema(true) // 自动创建索引
                .build();
    }
}
