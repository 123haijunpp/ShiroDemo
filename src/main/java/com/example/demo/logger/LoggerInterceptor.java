package com.example.demo.logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.demo.model.LoggerModel;
import com.example.demo.service.LoggerService;
import com.example.demo.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;

/**
 * program: ShiroDemo
 *
 * @author Qiu
 * Description: 日志拦截器
 * @date 2019/1/14/014 22:31
 */
@Slf4j
public class LoggerInterceptor implements HandlerInterceptor {

    // 请求开始时间标识
    private static final String LOGGER_SEND_TIME = "_send_time";
    private static final String LOGGER_ENTITY = "_logger_entity";

    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    private LoggerService loggerService;

    public LoggerInterceptor() {
        log.info(" 日志：LoggerInterceptor ");
    }

    /**
     * 格式化时间戳
     *
     * @param returnTime
     * @return
     */
    private String format(Long returnTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT);
        return dateFormat.format(returnTime);
    }

    /**
     * 在拦截器中的构造函数中注入bean，然后在@Configuration中设置
     *
     * @param loggerService 服务
     */
    LoggerInterceptor(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    /**
     * 进入SpringMVC的Controller之前开始记录日志实体
     *
     * @param request  请求对象
     * @param response 相应对象
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("LoggerInterceptor.preHandle");
        // 创建日志实体
        LoggerModel loggerModel = new LoggerModel();
        /*
        1.未禁用cookies ：
            request.getRequestedSessionId()方法获取的值是Cookie中的值，即使URL中有jsessionid的值。
        2.禁用cookies：
            request.getRequestedSessionId()方法获取的是值就是URL中的jessionid的值，如果没有，则为NULL
         */
        String requestedSessionId = request.getRequestedSessionId();
        // 请求路径
        String requestURI = request.getRequestURI();
        // 获取请求参数信息
        String paramDate = JSON.toJSONString(request.getParameterMap(), SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue);
        // 设置客户端ip
        loggerModel.setClientIp(LoggerUtils.getCliectIp(request));
        // 设置请求的类型 (json | 普通类型)
        loggerModel.setType(LoggerUtils.getRequestType(request));
        // 设置请求参数内容json字符串
        loggerModel.setParamData(paramDate);
        // 设置请求的地址
        loggerModel.setUri(requestURI);
        // 请求的SessionId
        loggerModel.setSessionId(requestedSessionId);
        // 设置请求的方法
        loggerModel.setMethod(request.getMethod());
        // 设置请求的开始时间
        request.setAttribute(LOGGER_SEND_TIME, System.currentTimeMillis());
        // 设置请求实体到request内，方便afterCompletion方法调用
        request.setAttribute(LOGGER_ENTITY, loggerModel);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 在最后视图渲染即将返回前台的时候调用
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("LoggerInterceptor.afterCompletion");
        // 请求的错误代码
        int status = response.getStatus();
        // 当前时间
        long currentTime = System.currentTimeMillis();
        // 请求开始的时间
        Long time = Long.valueOf(request.getAttribute(LOGGER_SEND_TIME).toString());
        // 获取本次请求日志实体
        LoggerModel loggerModel = (LoggerModel) request.getAttribute(LOGGER_ENTITY);
        // 设置请求时间差
        loggerModel.setTimeConsuming(Integer.valueOf((currentTime - time) + ""));
        //设置返回时间
        loggerModel.setReturnTime(format(currentTime));
        // 设置返回错误代码
        loggerModel.setHttpStatusCode(status + "");
        // 设置返回值
        loggerModel.setReturnData(JSON.toJSONString(request.getAttribute(LoggerUtils.LOGGER_RETURN), SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue));
        // 执行将日志写入数据库
//        loggerService.save(loggerModel);
    }
}
