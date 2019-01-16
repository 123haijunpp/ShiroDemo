package com.example.demo.logger;

import com.example.demo.annotation.Log;
import com.example.demo.mapper.LoggerRepository;
import com.example.demo.model.LoggerModel;
import com.example.demo.model.UserInfo;
import com.example.demo.util.HttpContextUtils;
import com.example.demo.util.IPUtils;
import com.example.demo.util.JSONUtils;
import com.example.demo.util.ShiroUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    private LoggerRepository loggerRepository;

    @Pointcut("@annotation(com.example.demo.annotation.Log)")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        // 执行方法
        Object result = point.proceed();
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //异步保存日志
        saveLog(point, time);
        return result;
    }


    void saveLog(ProceedingJoinPoint joinPoint, long time) throws InterruptedException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        LoggerModel loggerModel = new LoggerModel();
        Log log = method.getAnnotation(Log.class);
        if (log != null) {
            // 注解上的描述
            loggerModel.setOperation(log.value());
        }
        // 请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        loggerModel.setMethod(className + "." + methodName + "()");
        // 请求的参数
        Object[] args = joinPoint.getArgs();
        try {
            String params = JSONUtils.beanToJson(args[0]).substring(0, 4999);
            loggerModel.setParamData(params);
        } catch (Exception e) {

        }
        // 获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        // 设置IP地址
        loggerModel.setClientIp(IPUtils.getIpAddr(request));
        // 用户名
        UserInfo currUser = ShiroUtils.getUser();
        if (null == currUser) {
            if (null != loggerModel.getParamData()) {
                loggerModel.setId(-1L);
                loggerModel.setUsername(loggerModel.getParamData());
            } else {
                loggerModel.setId(-1L);
                loggerModel.setUsername("获取用户信息为空");
            }
        } else {
            loggerModel.setId(ShiroUtils.getUserId());
            loggerModel.setUsername(ShiroUtils.getUser().getUsername());
        }
        loggerModel.setTime((int) time);
        // 系统当前时间
        Date date = new Date();
        loggerModel.setGmtCreate(date);
        // 保存系统日志
        loggerRepository.save(loggerModel);
    }
}
