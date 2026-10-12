package com.lzj.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** @author 李兆基 */
@Configuration
public class SpringDocConfig {

    @Value("${myitem.springdoc.author}")
    private String author;
    @Value("${myitem.springdoc.url}")
    private String url;
    @Value("${myitem.springdoc.title}")
    private String title;
    @Value("${myitem.springdoc.description}")
    private String description;
    @Value("${myitem.springdoc.version}")
    private String version;

    /** 通用信息 Bean */
    @Bean
    public OpenAPI commonInfo() {
        return new OpenAPI().info(new Info()
                .title(title)
                .description(description)
                .version(version)
                .contact(new Contact().name(author).url(url)));
    }
}
