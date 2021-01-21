package com.heroke.bookstore;

import com.heroke.bookstore.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class SpringSocialApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringSocialApplication.class, args);
    }
}
