package com.example.demo.web;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * program: ShiroDemo
 *
 * @author Mr.Qiu
 * Description:
 * @date 2019/1/12/012 18:08
 */
@Controller
@Slf4j
public class LoginController {

    @RequestMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @RequestMapping("/login")
    ModelAndView login(HttpServletRequest request, ModelAndView modelAndView) throws Exception {
        log.info("LoginController.login");
        JSONObject obj = new JSONObject();
        // 登录失败从request中获取shiro处理的异常信息。
        // shiroLoginFailure:就是shiro异常类的全类名.
        Object exception = request.getAttribute("shiroLoginFailure");
        String msg = "";
        if (exception != null) {
            if (UnknownAccountException.class.getName().equals(exception)) {
                msg = "账户不存在或密码不正确";
            } else if (IncorrectCredentialsException.class.getName().equals(exception)) {
                msg = "账户不存在或密码不正确";
            } else {
                msg = "其他异常";
            }
        }
        obj.put("msg", msg);
        modelAndView.addObject("msg", msg);
        modelAndView.setViewName("login");
        // 将返回值写入到请求对象中
        request.setAttribute(LoggerUtils.LOGGER_RETURN, obj);
        // 此方法不处理登录成功,由shiro进行处理.
        return modelAndView;
    }
}
