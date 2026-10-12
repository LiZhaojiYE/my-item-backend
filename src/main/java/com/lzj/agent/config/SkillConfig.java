package com.lzj.agent.config;

import cn.hutool.core.io.FileUtil;
import com.alibaba.cloud.ai.graph.skills.registry.SkillRegistry;
import com.alibaba.cloud.ai.graph.skills.registry.classpath.ClasspathSkillRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** @author 周航宇 */
@Configuration  
@Slf4j  
public class SkillConfig {  
    private static final String CACHE_PATH = "D:/Idea-Project/my-item-backend/cache";
  
    /**  
     * 配置 Skill 注册器，该注册器用于扫描，加载和管理指定目录下的 SKILL.md  
     * 让 Agent 知道他有哪些技能包可以用  
     *  
     * @return Skill 注册器，全局可用，推荐单独配 bean，加入 Spring 管理，而非局部创建  
     */  
    @Bean  
    public SkillRegistry skillRegistry() {  
        // 清理缓存  
        log.info("✅清理缓存：{}", FileUtil.del(CACHE_PATH));  
        // 注册 SKILL.md 文件  
        return ClasspathSkillRegistry.builder()  
                // 指定扫描路径，默认 resources/skills/ 中的全部 SKILL.MD 文件  
                .classpathPath("skills")  
                // 指定缓存目录，默认生成 D:/tmp 目录并使用  
                .basePath(CACHE_PATH)  
                .build();  
    }  
}
