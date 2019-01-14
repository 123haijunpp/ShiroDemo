package com.example.demo.mapper;

import com.example.demo.model.UserInfo;
import org.springframework.data.repository.CrudRepository;

/**
 * program: ShiroDemo
 * Date: 2019/1/12/012 18:20
 *
 * @author: Mr.Qiu
 * Description:
 */
public interface UserInfoRepository extends CrudRepository<UserInfo, Long> {

    UserInfo findByUsername(String username);

    UserInfo save(UserInfo userInfo);
}
