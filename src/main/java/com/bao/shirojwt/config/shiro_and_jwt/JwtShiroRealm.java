package com.bao.shirojwt.config.shiro_and_jwt;

import com.bao.shirojwt.service.UserService;
import com.bao.shirojwt.entity.User;
import com.bao.shirojwt.utils.JwtUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 这个Realm是 用户登录后发送请求时采用的Realm
 */
public class JwtShiroRealm extends AuthorizingRealm {
    private final Logger log = LoggerFactory.getLogger(JwtShiroRealm.class);

    @Autowired
    private UserService userService;


    // 设置matcher
    public JwtShiroRealm() {
        this.setCredentialsMatcher(new JwtCredentialsMatcher());
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * 用来验证用户名正确与否，错误抛出异常即可
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) authenticationToken;
        String token = jwtToken.getToken();

        // token 为这次请求所带的token，其中claim包含的有 username
        // 然后通过 username 可以获取这个用户存的 用来加密 token的salt
        String tokenUsername = JwtUtils.getUsername(token);
        String salt = userService.getJwtTokenSalt(tokenUsername);
        if (salt == null) {
            // 如果 user == null ，说明缓存了中已经删除这条消息
            throw new AuthenticationException("token过期，请重新登录");
        }

        // 这个会交给 jwtCredenMatcher 来做判断的逻辑
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(tokenUsername, salt, "jwtRealm");

        return authenticationInfo;
    }
}
