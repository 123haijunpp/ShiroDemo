package com.example.demo.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * program: ShiroDemo
 * Date: 2019/1/12/012 17:50
 *
 * @author Mr.Qiu
 * Description:
 */
@Entity
//@Data
@Setter
@Getter
public class UserInfo implements Serializable {

    /**
     * 用户id
     */
    @Id
    @GeneratedValue
    private long uid;

    /**
     * 账号
     */
    @Column(unique = true)
    private String username;
    /**
     * 名称（昵称或者真实姓名，不同系统不同定义）
     */
    private String name;
    /**
     * 密码;
     */
    private String password;
    /**
     * 加密密码的盐
     */
    private String salt;
    /**
     * 用户状态,0:创建未认证（比如没有激活，没有输入验证码等等）--等待验证的用户 , 1:正常状态,2：用户被锁定.
     */
    private byte state;

    /**
     * 一个用户对应多个角色
     * FetchType.EAGER 立即加载
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "SysUserRole", joinColumns = {@JoinColumn(name = "uid")}, inverseJoinColumns = {@JoinColumn(name = "roleId")})
    private List<SysRole> roles;

    /**
     * 密码盐
     *
     * @return str
     */
    public String getCredentialsSalt() {
        return this.username + this.salt;
    }

}
