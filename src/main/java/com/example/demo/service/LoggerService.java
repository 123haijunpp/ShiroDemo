package com.example.demo.service;

import com.example.demo.model.LoggerModel;

/**
 * program: ShiroDemo
 *
 * @author Qiu
 * Description:
 * @date 2019/1/14/014 22:20
 */
public interface LoggerService {

    /**
     * 保存日志
     *
     * @param loggerModel 日志对象
     */
    void save(LoggerModel loggerModel);
}
