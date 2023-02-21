package com.asule.redpocket.config;

import com.asule.redpocket.interceptor.JwtInterceptor;
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/**")             //其他接口token验证
                .excludePathPatterns("/user/login"); //所有用户都放
    }
}
