package com.example.demo.exception;

import org.apache.shiro.authc.AuthenticationException;

/**
 * program: ShiroDemo
 *
 * @author Qiu
 * Description: 验证码校验
 * @date 2019/1/20/020 18:47
 */
public class IncorrectCaptchaException extends AuthenticationException {

    public IncorrectCaptchaException() {
        super();
    }

    public IncorrectCaptchaException(String message) {
        super(message);
    }

    public IncorrectCaptchaException(Throwable cause) {
        super(cause);
    }

    public IncorrectCaptchaException(String message, Throwable cause) {
        super(message, cause);
    }
}
