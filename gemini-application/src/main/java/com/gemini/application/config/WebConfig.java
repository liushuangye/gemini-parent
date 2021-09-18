package com.gemini.application.config;

import com.gemini.toolkit.common.utils.CommonConsts;
import com.gemini.toolkit.config.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.FormContentFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport  {
    @Autowired
    private TokenInterceptor tokenInterceptor;
    @Autowired
    private MessageSource messageSource;

    //静态文件路径跳转
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射static路径的请求到static目录下
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        // 解决swagger无法访问
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        // 解决swagger的js文件无法访问
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        super.addResourceHandlers(registry);
    }
    //hiddenMethod请求处理
    @Bean
    public FilterRegistrationBean formContentFilter() {

        // 过滤器注册器
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();

        // PUT/DELETE等hiddenMethod的过滤器
        FormContentFilter formContentFilter = new FormContentFilter();
        registrationBean.setFilter(formContentFilter);

        // 过滤路径
        List<String> urls = new ArrayList<>();
        urls.add("/*");
        registrationBean.setUrlPatterns(urls);

        return registrationBean;

    }
    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludePath = new ArrayList<>();
//        excludePath.add("/user_register"); //注册
        excludePath.add("/index"); // 首页
        excludePath.add("/login/check"); // 登录
        excludePath.add("/login/forgetPassword"); // 忘记密码
        excludePath.add("/logout"); // 登出
        excludePath.add("/static/**"); // 静态资源
        excludePath.add("/assets/**"); // 静态资源
        excludePath.add("/webjars/**"); // swagger-ui静态资源
        registry.addInterceptor(tokenInterceptor).addPathPatterns("/**").excludePathPatterns(excludePath);

    }
    @Override
    public Validator getValidator() {
        return validator();
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource);
        return validator;
    }
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.setAllowCredentials(Boolean.TRUE);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.addExposedHeader(CommonConsts.AUTHORIZATION);
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);
        return new CorsFilter(configSource);
    }
}

