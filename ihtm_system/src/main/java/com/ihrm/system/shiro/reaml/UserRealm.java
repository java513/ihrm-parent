package com.ihrm.system.shiro.reaml;

import com.ihrm.common.entity.UserLevel;
import com.ihrm.common.shiro.realm.IhrmRealm;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.ProfileResult;
import com.ihrm.system.service.PermissionService;
import com.ihrm.system.service.UserService;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/27 23:35
 */
public class UserRealm extends IhrmRealm{

    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;

    /**
     * 认证方法
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //1.获取用户的手机号和密码
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String mobile = upToken.getUsername();
        String password = new String(upToken.getPassword());
        //2.根据手机号查询用户
        User user = userService.findByMobile(mobile);
        //3.判断用户是否存在，用户密码是否和输入密码一致
        if(user!=null&&user.getPassword().equals(password)){
        //4.构造安全数据并返回（安全数据：用户基本数据，权限信息 profileResult）
            ProfileResult result = null;
            if(UserLevel.USER.equals(user.getLevel())){
                result = new ProfileResult(user);
            }else {
                Map map = new HashMap();
                if(UserLevel.COADMIN.equals(user.getLevel())){
                    map.put("enVisible","1");
                }
                List<Permission> permissions =permissionService.findAll(map);
               result =  new ProfileResult(user,permissions);
            }
        //构造方法：安全数据，密码，realm域名
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(result,user.getPassword(),this.getName());
            return info;
        }
        //返回null，会抛出异常，标识用户名和密码不匹配
        return null;
    }
}
