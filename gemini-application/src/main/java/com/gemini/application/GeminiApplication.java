package com.gemini.application;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.gemini")
@MapperScan("com.gemini.**.mapper")
public class GeminiApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(GeminiApplication.class, args);
    }
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 注意这里要指向原先用main方法执行的Application启动类
        return builder.sources(GeminiApplication.class);
    }

}

//@SpringBootApplication
//@ComponentScan("com.gemini")
//@MapperScan("com.gemini.**.mapper")
//public class GeminiApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(GeminiApplication.class, args);
//    }
//
//}
