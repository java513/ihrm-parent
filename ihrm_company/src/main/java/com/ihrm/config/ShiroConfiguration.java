package com.ihrm.config;

import com.ihrm.common.shiro.realm.IhrmRealm;
import com.ihrm.common.shiro.session.IhrmWebSessionManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author liuhao
 * @Desc shiro配置类
 * @DATE 2019/12/27 23:58
 */
@Configuration
public class ShiroConfiguration {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private Integer port;
    //1.创建realm
    @Bean
    public IhrmRealm getRealm(){
        return new IhrmRealm();
    }
    //2.创建安全管理器
    @Bean
    public SecurityManager getSecurityManager(IhrmRealm ihrmRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(ihrmRealm);
        //将自定义的会话管理器注册到安全管理器中
        securityManager.setSessionManager(defaultSessionManager());
        //将自定义的redis缓存管理器注册到安全管理器中
        securityManager.setCacheManager(redisCacheManager());
        return securityManager;
    }
    //3.配置shiro的过滤器工厂
    /**
     * 再web程序中，shiro进行权限控制全部是通过一组过滤器集合进行控制
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager){
        //1.创建过滤器工厂
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        //2.设置安全管理器
        factoryBean.setSecurityManager(securityManager);
        //3.通用配置（跳转登录页面，未授权跳转的页面）
        factoryBean.setLoginUrl("/authError?code=1");
        factoryBean.setUnauthorizedUrl("/authError?code=2");
        //4.设置过滤器集合
        Map<String,String> filterMap = new LinkedHashMap<>();
        //anon -- 匿名访问
        filterMap.put("/system/login","anon");
        filterMap.put("/authError","anon");
        //注册
        //authc -- 认证之后访问（登录）
        filterMap.put("/**","authc");
        //perms -- 具有某中权限 (使用注解配置授权)
        factoryBean.setFilterChainDefinitionMap(filterMap);
        return factoryBean;
    }

    /**
     *  1.redis的控制器，操作redis
     */
    public RedisManager redisManager(){
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        return redisManager;
    }

    /**
     * 2.sessionDao
     */
    public RedisSessionDAO redisSessionDAO(){
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }

    /**
     * 3.会话管理器
     */
    public DefaultSessionManager defaultSessionManager(){
        IhrmWebSessionManager sessionManager = new IhrmWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        //禁用cookie
        //sessionManager.setSessionIdCookieEnabled(false);
        //禁用url重写   url;jsessionid=id
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    /**
     * 4.缓存管理器
     */
    public RedisCacheManager redisCacheManager(){
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }

    //开启对shior注解的支持
    @Bean
    public AuthorizationAttributeSourceAdvisor advisor(SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

}
