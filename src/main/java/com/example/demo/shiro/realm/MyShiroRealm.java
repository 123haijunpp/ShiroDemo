package com.example.demo.shiro.realm;

import com.example.demo.model.SysPermission;
import com.example.demo.model.SysRole;
import com.example.demo.model.UserInfo;
import com.example.demo.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;

/**
 * program: ShiroDemo
 * Date: 2019/1/12/012 18:18
 *
 * @author Mr.Qiu
 * Description: 重载以下两个方法来配置用户身份验证和权限验证。
 */
@Slf4j
public class MyShiroRealm extends AuthorizingRealm {

    /**
     * 设置realm的名称
     */
    @Override
    public void setName(String name) {
        super.setName("myShiroRealm");
    }

    public MyShiroRealm() {
        log.info("MyShiroRealm");
    }

    @Resource
    private UserInfoService userInfoService;

    /**
     * 当访问到页面的时候，链接配置了相应的权限或者shiro标签才会执行此方法否则不会执行
     * 授权，只有通过doGetAuthenticationInfo方法认证完之后才会执行
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.info("开始权限配置");
        SimpleAuthorizationInfo authenticationInfo = new SimpleAuthorizationInfo();
        UserInfo userInfo = (UserInfo) principalCollection.getPrimaryPrincipal();
        for (SysRole role : userInfo.getRoles()) {
            authenticationInfo.addRole(role.getRole());
            for (SysPermission permission : role.getPermissions()) {
                authenticationInfo.addStringPermission(permission.getPermission());
            }
        }
        return authenticationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("开始身份验证");
        //token中包含用户输入的用户名和密码，获取用户名，默认和login.html中的username对应。
        String userName = (String) authenticationToken.getPrincipal();
        UserInfo userInfo = userInfoService.findByUsername(userName);
        if (userInfo == null) {
            //没有返回登录用户名对应的SimpleAuthenticationInfo对象时,就会在LoginController中抛出UnknownAccountException异常
            return null;
        }
        //验证通过返回一个封装了用户信息的AuthenticationInfo实例即可。
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(
                //用户信息
                userInfo,
                //密码
                userInfo.getPassword(),
                //realm name
                this.getName()
        );
        //设置盐
        simpleAuthenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(userInfo.getCredentialsSalt()));
        return simpleAuthenticationInfo;
    }

    /**
     * 清除缓存
     */
    public void clearCached() {
        PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
        super.clearCache(principalCollection);
    }

    /**
     * 重写方法,清除当前用户的的 授权缓存
     *
     * @param principals
     */
    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    /**
     * 重写方法，清除当前用户的 认证缓存
     *
     * @param principals
     */
    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    /**
     * 自定义方法：清除所有 授权缓存
     */
     void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    /**
     * 自定义方法：清除所有 认证缓存
     */
     void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    /**
     * 自定义方法：清除所有的  认证缓存  和 授权缓存
     */
    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }
}
