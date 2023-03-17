package com.asule.redpocket.config;

import com.asule.redpocket.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/2/7 12:06
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {


    @Bean
    public LoginInterceptor loginInterceptor(){
        return new LoginInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/**")             //其他接口token验证
                .excludePathPatterns("/user/login"); //所有用户都放
    }
}
