package com.example.demo.service.impl;

import com.example.demo.mapper.UserInfoRepository;
import com.example.demo.model.UserInfo;
import com.example.demo.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * program: ShiroDemo
 * Date: 2019/1/12/012 18:19
 *
 * @author: Mr.Qiu
 * Description:
 */
@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserInfoRepository userInfoRepository;

    @Override
    public UserInfo findByUsername(String username) {
        log.info("UserInfoServiceImpl.findByUsername");
        return userInfoRepository.findByUsername(username);
    }
}