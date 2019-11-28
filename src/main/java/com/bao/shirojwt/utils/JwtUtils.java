package com.bao.shirojwt.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

// jwt相关的工具类
public class JwtUtils {

    /**
     * 获得token中的信息无需secret解密也能获得，此方法是获得token中的签发时间。
     * @param token
     * @return token中的签发时间
     */
    public static Date getIssuedAt(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getIssuedAt();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * @param token
     * @return token中的username
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 生成签名，expireTime后过期
     * @param username 用户名
     * @param time 过期时间
     * @return 加密后的token
     */
    public static String sign(String username, String salt, long time) {
        Date date = new Date(System.currentTimeMillis() + time*1000);
        Algorithm algorithm = Algorithm.HMAC256(salt);
        // 附带username信息
        return JWT.create()
                .withClaim("username", username)
                .withExpiresAt(date)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    /**
     * 判断token是否过期
     * @param token
     * @return
     */
    public static boolean isTokenExpired(String token) {
        Date now = Calendar.getInstance().getTime();
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().before(now);
    }

    /**
     * @return 随机salt
     */
    public static String generateSalt() {
        SecureRandomNumberGenerator secureRandom = new SecureRandomNumberGenerator();
        String hex = secureRandom.nextBytes(16).toHex();
        return hex;
    }
}
