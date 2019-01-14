package com.example.demo.web;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * program: ShiroDemo
 *
 * @author Qiu
 * Description:
 * @date 2019/1/13/013 16:43
 */
@Controller
@RequestMapping("/userInfo")
public class UserInfoController {

    /**
     * 用户查询.
     */
    @RequestMapping("/userList")
    @RequiresPermissions("userInfo:view")
    @ExceptionHandler(UnauthorizedException.class)
    public String userInfo() {
        return "userInfo";
    }

    /**
     * 用户添加;
     */
    @RequestMapping("/userAdd")
    @RequiresPermissions("userInfo:add")
    public String userInfoAdd() {
        return "unauthorizedUrl";
    }

    @RequestMapping("/userDel")
    @RequiresPermissions("userInfo:del")
    public String userInfoDel() {
        return "userDel";
    }
}
