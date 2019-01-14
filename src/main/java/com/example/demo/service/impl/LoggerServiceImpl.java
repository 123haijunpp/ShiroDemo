package com.example.demo.service.impl;

import com.example.demo.base.BaseService;
import com.example.demo.mapper.LoggerRepository;
import com.example.demo.model.LoggerModel;
import com.example.demo.service.LoggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * program: ShiroDemo
 *
 * @author Qiu
 * Description:
 * @date 2019/1/14/014 22:22
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class LoggerServiceImpl extends BaseService implements LoggerService {

    @Resource
    private LoggerRepository loggerRepository;

    @Override
    public void save(LoggerModel loggerModel) {
        loggerRepository.save(loggerModel);
    }
}
