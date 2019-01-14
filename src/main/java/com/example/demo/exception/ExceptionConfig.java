package com.example.demo.exception;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.Properties;

/**
 * program: ShiroDemo
 *
 * @author Qiu
 * Description: unauthorizedUrl设置后不起作用,处理为授权的动作,自己来处理UnauthorizedException异常：
 * @date 2019/1/13/013 17:29
 */
@Configuration
public class ExceptionConfig {

    @Bean
    public SimpleMappingExceptionResolver resolver() {
        SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
        Properties properties = new Properties();
        properties.setProperty("org.apache.shiro.authz.UnauthorizedException", "/403");
        resolver.setExceptionMappings(properties);
        return resolver;
    }

}
