package com.example.demo.logger;

import com.example.demo.service.LoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * program: ShiroDemo
 *
 * @author Qiu
 * Description: 在Configuration中Autowired注解需要扫描特定的包
 * @date 2019/1/14/014 22:00
 */
@Configuration
@ComponentScan({"com.example.demo.service"})
public class LoggerConfiguration extends WebMvcConfigurationSupport {

    private LoggerService loggerService;

    @Autowired
    public LoggerConfiguration(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggerInterceptor(loggerService)).addPathPatterns("/**");
    }
}
