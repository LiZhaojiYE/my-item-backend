package com.lzj.agent.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.stringtemplate.v4.ST;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** @author 周航宇 */
@Slf4j
public class StUtil {

    /** 模板分隔符开始字符 */
    private static final char DELIMITER_START_CHAR = '$';
    /** 模板分隔符结束字符 */
    private static final char DELIMITER_STOP_CHAR = '$';
    /** 资源加载器 */
    private static final DefaultResourceLoader RESOURCE_LOADER = new DefaultResourceLoader();

    /**
     * 通过加载 .st 文件模板（使用 $ 分隔符）来构建 ST 实例
     *
     * @param location 资源路径 prompt/xxx.st
     * @return ST 实例
     */
    public static ST load(String location) {
        try {
            // 加载资源，支持 classpath 下的文件
            Resource resource = RESOURCE_LOADER.getResource(location);
            // 获取文件路径
            Path path = resource.getFile().toPath();
            // 读取文件内容
            String content = Files.readString(path, StandardCharsets.UTF_8);
            // 构建 ST 对象，使用 $ 和 $ 作为分隔符
            return new ST(content, DELIMITER_START_CHAR, DELIMITER_STOP_CHAR);
        } catch (Exception e) {
            throw new RuntimeException("加载 classpath 下 .st 文件模板失败", e);
        }
    }

    /**
     * 构建内联字符串模板（使用 < 和 > 分隔符）
     *
     * @param templateText 模板文本
     * @return ST 实例
     */
    public static ST build(String templateText) {
        // 构建 ST 对象，使用 < 和 > 作为分隔符（默认值）
        return new ST(templateText);
    }
}
