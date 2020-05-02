package com.ihrm.system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author liuhao
 * @Desc Jwt配置类
 * @DATE 2019/12/24 0:25
 */
@Configuration
public class SystemConfig extends WebMvcConfigurationSupport{
   /* @Autowired
    private JwtInterceptor jwtInterceptor;

    *//**
     * 添加拦截器的配置
     *//*
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/system/login","/system/test");
    }*/
}
