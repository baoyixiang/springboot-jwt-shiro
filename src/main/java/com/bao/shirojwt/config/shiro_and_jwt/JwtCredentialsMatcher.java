package com.bao.shirojwt.config.shiro_and_jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.bao.shirojwt.entity.User;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtCredentialsMatcher implements CredentialsMatcher {

    private final Logger logger = LoggerFactory.getLogger(JwtCredentialsMatcher.class);

    // 当用户进行请求时要进行认证匹配
    // 跟第一次login时不一样，shiro没有实现jwt的matcher， 需要我们自定义实现
    // Matcher中直接调用工具包中的verify方法即可
    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        String token = (String) authenticationToken.getCredentials(); // 这里是前端传过来的加密的需要认证的token
        Object stored = authenticationInfo.getCredentials();  // 这是保存的salt
        String salt = stored.toString();

        String username = authenticationInfo.getPrincipals().getPrimaryPrincipal().toString();
        try {
            Algorithm algorithm = Algorithm.HMAC256(salt);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", username)
                    .build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            logger.error("Token Error: {}", e.getMessage());
        }

        return false;
    }
}
