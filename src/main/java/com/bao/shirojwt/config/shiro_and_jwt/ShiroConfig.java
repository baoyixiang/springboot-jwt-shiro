package com.bao.shirojwt.config.shiro_and_jwt;

import com.bao.shirojwt.service.UserService;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSessionStorageEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.Map;

@Configuration
public class ShiroConfig {


    @Autowired
    private UserService userService;
    /**
     * 初始化Authenticator
     */
    @Bean
    public Authenticator authenticator() {
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        //这里设置两个realm, jwt用于token认证， db用于用户登录验证和访问权限的获取
        authenticator.setRealms(Arrays.asList(jwtShiroRealm(), dbShiroRealm()));
        // 多个realm的认证策略，即若一个成功就跳过其他的。
        authenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
        return authenticator;
    }

    /**
     * 禁用session， 不保存用户登录的状态，后端的无状态的，每次请求都要重新认证
     * 需要注意，如果用户代码里面调用 Subject.getSession()还是可以用session
     * 如果要完全禁用，要配合下面的 noSessionCreation的Filter来实现
     */
    @Bean
    public SessionStorageEvaluator sessionStorageEvaluator() {
        DefaultWebSessionStorageEvaluator sessionStorageEvaluator = new DefaultWebSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        return sessionStorageEvaluator;
    }

    /**
     * 设置过滤器, 加入了自定义的过滤器
     */
    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filterMap = factoryBean.getFilters();
        filterMap.put("authcToken", createAuthFilter());
        filterMap.put("anyRole", createRolesFilter());
        factoryBean.setFilters(filterMap);
        factoryBean.setFilterChainDefinitionMap(shiroFilterChainDefinition().getFilterChainMap());

        return factoryBean;
    }

    /**
     * 路由的过滤规则
     */
    @Bean
    protected ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/register", "anon");
        chainDefinition.addPathDefinition("/login", "noSessionCreation,anon");
        chainDefinition.addPathDefinition("/logout", "noSessionCreation,authcToken[permissive]");
        chainDefinition.addPathDefinition("/images/**", "anon");
        chainDefinition.addPathDefinition("/admin/**", "noSessionCreation,authcToken,anyRole[admin,manager]");
        chainDefinition.addPathDefinition("/**", "noSessionCreation, authcToken");
        return chainDefinition;
    }

    @Bean("dbRealm")
    public Realm dbShiroRealm() {
        DbShiroRealm realm = new DbShiroRealm();
        return realm;
    }

    @Bean("jwtRealm")
    public Realm jwtShiroRealm() {
        JwtShiroRealm realm = new JwtShiroRealm();
        return realm;
    }

    // 下面两个不能加 Bean注解，不然Spring自动会注入Filter，但是里面要用到自动注入咋办？ 当参数传进去
    // 自动注入是初始化时进行的，即在这些代码执行之前
    // 自动注入分两步， 1. 收集组件，  2. 进行关联（注入）
    // 如果在一个非组件中注入一个组件，第二步时找不到这个非组件，就没办法注入。
    protected JwtAuthFilter createAuthFilter() {
        return new JwtAuthFilter(userService);
    }

    protected AnyRolesAuthorizationFilter createRolesFilter() {
        return new AnyRolesAuthorizationFilter();
    }
}

