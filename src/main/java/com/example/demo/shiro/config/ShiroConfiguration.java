package com.example.demo.shiro.config;

import com.example.demo.filter.KaptchaFilter;
import com.example.demo.shiro.listener.BDSessionListener;
import com.example.demo.shiro.realm.MyShiroRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.Filter;
import java.util.*;

/**
 * program: ShiroDemo
 *
 * @author Mr.Qiu
 * description 这里要配置的是ShiroConfig类，Apache Shiro 核心通过 Filter 来实现，就好像SpringMvc 通过DispachServlet 来主控制一样。 既然是使用 Filter 一般也就能猜到，是通过URL规则来进行过滤和权限校验，所以我们需要定义一系列关于URL的规则和访问权限。
 * @date 2019/1/12/012 18:48
 */
@Configuration
@Slf4j
public class ShiroConfiguration {

    @Value("${server.servlet.session.timeout}")
    private String tomcatTimeout;

    /**
     * shiro生命周期处理器
     *
     * @return LifecycleBeanPostProcessor
     */
    @Bean
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("securityManager") SecurityManager securityManager) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 验证码过滤器
        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        KaptchaFilter kaptchaFilter = new KaptchaFilter();
        filters.put("kaptchaFilter", kaptchaFilter);
        shiroFilterFactoryBean.setFilters(filters);

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 图片验证码(kaptcha框架)
        filterChainDefinitionMap.put("/kaptcha.jpg", "anon");
        filterChainDefinitionMap.put("/login", "kaptchaFilter");
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/fonts/**", "anon");
        filterChainDefinitionMap.put("/img/**", "anon");
        filterChainDefinitionMap.put("/docs/**", "anon");
        filterChainDefinitionMap.put("/druid/**", "anon");
        filterChainDefinitionMap.put("/upload/**", "anon");
        filterChainDefinitionMap.put("/files/**", "anon");
        filterChainDefinitionMap.put("/500", "anon");
        filterChainDefinitionMap.put("/404", "anon");
        filterChainDefinitionMap.put("/unauthorizedUrl", "anon");
        // 退出登录
        filterChainDefinitionMap.put("/logout", "logout");
        // 显示在地址栏上表示这个网站的图标。
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        // user过滤器,添加user过滤器的资源在记住我或认证之后就可以直接访问了。
        filterChainDefinitionMap.put("/index", "user");
        filterChainDefinitionMap.put("/", "user");
        filterChainDefinitionMap.put("/**", "authc");
        // authc表示需要验证身份才能访问，还有一些比如anon表示不需要验证身份就能访问等。
        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setSuccessUrl("/index");
        // 这里设置403并不会起作用，参考http://www.jianshu.com/p/e03f5b54838c   //未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorizedUrl");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * SecurityManager 是 Shiro 架构的核心，通过它来链接Realm和用户(文档中称之为Subject.)
     */
    @Bean("securityManager")
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //将Realm注入到SecurityManager中
        securityManager.setRealm(myShiroRealm());
        // 自定义缓存实现，注入缓存
//        securityManager.setCacheManager(cacheManager());
        //配置redis缓存
        securityManager.setCacheManager(cacheManager());
        //配置自定义session管理，使用redis
        securityManager.setSessionManager(sessionManager());
        // 注入rememberMeManager记住我
        securityManager.setRememberMeManager(cookieRememberMeManager());
        return securityManager;
    }

    /**
     * 自定义身份认证的 realm
     *
     * @return myShiroRealm
     */
    @Bean
    public MyShiroRealm myShiroRealm() {
        MyShiroRealm myShiroRealm = new MyShiroRealm();
        //设置解密规则
        myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return myShiroRealm;
    }

    /**
     * 因为我们的密码是加过密的，所以，如果要Shiro验证用户身份的话，需要告诉它我们用的是md5加密的，并且是加密了两次。同时我们在自己的Realm中也通过SimpleAuthenticationInfo返回了加密时使用的盐。这样Shiro就能顺利的解密密码并验证用户名和密码是否正确了。
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        //散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        //散列的次数，比如散列两次，相当于 md5(md5(""));
        hashedCredentialsMatcher.setHashIterations(2);
        return hashedCredentialsMatcher;
    }

    /**
     * 开启shiro aop注解支持.
     * 开启shiro 注解模式
     * 可以在controller中的方法前加上注解
     * 如 @RequiresPermissions("userInfo:add")
     * 使用代理方式;所以需要开启代码支持;
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    /**
     * 记住Cookie对象
     *
     * @return SimpleCookie
     */
    @Bean
    public SimpleCookie rememberMeCookie() {
        log.info("ShiroConfiguration.rememberMeCookie");
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //设为true后，只能通过http访问，javascript无法访问
        //防止xss读取cookie
        simpleCookie.setHttpOnly(true);
        simpleCookie.setPath("/");
        //<!-- 记住我cookie生效时间30天 ,单位秒;-->
        simpleCookie.setMaxAge(2592000);
        return simpleCookie;
    }

    /**
     * cookie管理对象
     *
     * @return CookieRememberMeManager
     */
    @Bean
    public CookieRememberMeManager cookieRememberMeManager() {
        log.info("ShiroConfiguration.cookieRememberMeManager");
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        return cookieRememberMeManager;
    }

    /**
     * 解决： 无权限页面不跳转 shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized") 无效
     * shiro的源代码ShiroFilterFactoryBean.Java定义的filter必须满足filter instanceof AuthorizationFilter，
     * 只有perms，roles，ssl，rest，port才是属于AuthorizationFilter，而anon，authcBasic，auchc，user是AuthenticationFilter，
     * 所以unauthorizedUrl设置后页面不跳转 Shiro注解模式下，登录失败与没有权限都是通过抛出异常。
     * 并且默认并没有去处理或者捕获这些异常。在SpringMVC下需要配置捕获相应异常来通知用户信息
     */
    @Bean
    public SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver simpleMappingExceptionResolver = new SimpleMappingExceptionResolver();
        Properties properties = new Properties();
        //这里的 /unauthorized 是页面，不是访问的路径
        properties.setProperty("org.apache.shiro.authz.UnauthorizedException", "/unauthorizedUrl");
        properties.setProperty("org.apache.shiro.authz.UnauthenticatedException", "/unauthorizedUrl");
        simpleMappingExceptionResolver.setExceptionMappings(properties);
        return simpleMappingExceptionResolver;
    }

    /**
     * FormAuthenticationFilter 过滤器 过滤记住我
     *
     * @return formAuthenticationFilter
     */
    @Bean
    public FormAuthenticationFilter formAuthenticationFilter() {
        FormAuthenticationFilter formAuthenticationFilter = new FormAuthenticationFilter();
        //对应前端的checkbox的name = rememberMe
        formAuthenticationFilter.setRememberMeParam("rememberMe");
        return formAuthenticationFilter;
    }

    /**
     * 解决spring-boot Whitelabel Error Page
     *
     * @return
     */
    @Bean
    public WebServerFactoryCustomizer containerCustomizer() {
        return new WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>() {
            @Override
            public void customize(ConfigurableServletWebServerFactory factory) {
                ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/unauthorizedUrl");
                ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404");
                ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500");
                Set<ErrorPage> errorPages = new HashSet<>();
                errorPages.add(error401Page);
                errorPages.add(error404Page);
                errorPages.add(error500Page);
                factory.setErrorPages(errorPages);
            }
        };
    }

    /**
     * shiro缓存管理器;
     * 需要添加到securityManager中
     *
     * @return cacheManager
     */
    @Bean
    public org.crazycake.shiro.RedisCacheManager cacheManager() {
        org.crazycake.shiro.RedisCacheManager redisCacheManager = new org.crazycake.shiro.RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        //redis中针对不同用户缓存
        redisCacheManager.setPrincipalIdFieldName("username");
        //用户权限信息缓存时间
        redisCacheManager.setExpire(200000);
        return redisCacheManager;
    }

    /**
     * 让某个实例的某个方法的返回值注入为Bean的实例
     * Spring静态注入
     *
     * @return getMethodInvokingFactoryBean
     */
    @Bean
    public MethodInvokingFactoryBean getMethodInvokingFactoryBean() {
        MethodInvokingFactoryBean factoryBean = new MethodInvokingFactoryBean();
        factoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        factoryBean.setArguments(new Object[]{securityManager()});
        return factoryBean;
    }

    /**
     * 配置session监听
     *
     * @return sessionListener
     */
    @Bean("sessionListener")
    public org.apache.shiro.session.SessionListener sessionListener() {
        return new BDSessionListener();
    }

    /**
     * 配置会话ID生成器
     *
     * @return JavaUuidSessionIdGenerator
     */
    @Bean
    public SessionIdGenerator sessionIdGenerator() {
        return new JavaUuidSessionIdGenerator();
    }

    @Bean
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost("192.168.0.104");
        redisManager.setPort(6379);
        redisManager.setPassword("admin");
        return redisManager;
    }

    /**
     * SessionDAO的作用是为Session提供CRUD并进行持久化的一个shiro组件
     * MemorySessionDAO 直接在内存中进行会话维护
     * EnterpriseCacheSessionDAO  提供了缓存功能的会话维护，默认情况下使用MapCache实现，内部使用ConcurrentHashMap保存缓存的会话。
     *
     * @return sessionDAO
     */
    @Bean
    public SessionDAO sessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        // session在redis中的保存时间,最好大于session会话超时时间
        redisSessionDAO.setExpire(12000);
        return redisSessionDAO;
    }

    /**
     * 配置保存sessionId的cookie
     * 注意：这里的cookie 不是上面的记住我 cookie 记住我需要一个cookie session管理 也需要自己的cookie
     * 默认为: JSESSIONID 问题: 与SERVLET容器名冲突,重新定义为sid
     *
     * @return sessionIdCookie
     */
    @Bean
    public SimpleCookie sessionIdCookie() {
        //这个参数是cookie的名称
        SimpleCookie simpleCookie = new SimpleCookie("sid");
        //setcookie的httponly属性如果设为true的话，会增加对xss防护的安全系数。它有以下特点：

        //setcookie()的第七个参数
        //设为true后，只能通过http访问，javascript无法访问
        //防止xss读取cookie
        simpleCookie.setHttpOnly(true);
        simpleCookie.setPath("/");
        //maxAge=-1表示浏览器关闭时失效此Cookie
        simpleCookie.setMaxAge(-1);
        return simpleCookie;
    }

    /**
     * 配置会话管理器，设定会话超时及保存
     *
     * @return sessionManager
     */
    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        Collection<SessionListener> listeners = new ArrayList<>();
        //配置监听
        listeners.add(sessionListener());
        sessionManager.setSessionListeners(listeners);
        sessionManager.setSessionIdCookie(sessionIdCookie());
        sessionManager.setSessionDAO(sessionDAO());
        sessionManager.setCacheManager(cacheManager());

        //全局会话超时时间（单位毫秒），默认30分钟  暂时设置为10秒钟 用来测试
        sessionManager.setGlobalSessionTimeout(1800000);
        //是否开启删除无效的session对象  默认为true
        sessionManager.setDeleteInvalidSessions(true);
        //是否开启定时调度器进行检测过期session 默认为true
        sessionManager.setSessionValidationSchedulerEnabled(true);
        //设置session失效的扫描时间, 清理用户直接关闭浏览器造成的孤立会话 默认为 1个小时
        //设置该属性 就不需要设置 ExecutorServiceSessionValidationScheduler 底层也是默认自动调用ExecutorServiceSessionValidationScheduler
        //暂时设置为 5秒 用来测试
        sessionManager.setSessionValidationInterval(3600000);
        //取消url 后面的 JSESSIONID
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;

    }

}
