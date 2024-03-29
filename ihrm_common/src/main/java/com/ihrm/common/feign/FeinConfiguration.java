package com.ihrm.common.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/28 11:09
 */
@Configuration
public class FeinConfiguration {
    //配置feign拦截器，解决请求头问题
    @Bean
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            //获取所有浏览器发送的请求头属性，请求头赋值到feign
            @Override
            public void apply(RequestTemplate requestTemplate) {
                //请求属性
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(attributes!=null){
                   HttpServletRequest request = attributes.getRequest();
                   //获取浏览器发起的请求头
                    Enumeration<String> headerNames = request.getHeaderNames();
                    if(headerNames!=null){
                        while (headerNames.hasMoreElements()){
                            String name = headerNames.nextElement();//请求头名称
                            String value = request.getHeader(name);//请求头数据
                            requestTemplate.header(name,value);
                        }
                    }
                }
            }
        };
    }
}
