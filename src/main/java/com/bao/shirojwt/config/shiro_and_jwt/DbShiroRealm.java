package com.bao.shirojwt.config.shiro_and_jwt;

import com.bao.shirojwt.service.UserService;
import com.bao.shirojwt.entity.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个 Realm是用户第一次登录时 采用的Realm
 */
public class DbShiroRealm extends AuthorizingRealm {
    private final Logger log = LoggerFactory.getLogger(DbShiroRealm.class);

    // 主要设置 matcher
    public DbShiroRealm() {
        this.setCredentialsMatcher(new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME));
    }

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    // 认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        String username = usernamePasswordToken.getUsername();
        User user = userService.getUserInfo(username);
        if (user == null) {
            throw new UnknownAccountException("用户不存在");
        }

        // 可以看到在这里没有对密码进行 比对判断，这个过程是shiro在matcher中帮我们做的。
        return new SimpleAuthenticationInfo(user, user.getPassword(), ByteSource.Util.bytes(user.getEncryptSalt()), "dbRealm");
    }
}
