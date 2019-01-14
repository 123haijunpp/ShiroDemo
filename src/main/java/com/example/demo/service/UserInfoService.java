package com.example.demo.service;

import com.example.demo.model.UserInfo;

/**
 * program: ShiroDemo
 * Date: 2019/1/12/012 18:19
 *
 * @author Mr.Qiu
 * Description:
 */
public interface UserInfoService {

    /**
     * 查询所有用户名
     *
     * @param username 用户名
     * @return ...
     */
    UserInfo findByUsername(String username);
}
