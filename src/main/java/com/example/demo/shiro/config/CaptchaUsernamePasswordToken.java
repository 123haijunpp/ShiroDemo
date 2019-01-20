package com.example.demo.shiro.config;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * program: ShiroDemo
 *
 * @author Qiu
 * Description:
 * @date 2019/1/20/020 18:45
 */
public class CaptchaUsernamePasswordToken extends UsernamePasswordToken {

    /**
     * 验证码
     */
    private String captcha;

    public CaptchaUsernamePasswordToken(String username, char[] password, boolean rememberMe, String host, String captcha) {
        super(username, password, rememberMe, host);
        this.captcha = captcha;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
