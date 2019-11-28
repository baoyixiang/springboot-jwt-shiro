package com.bao.shirojwt;

import com.bao.shirojwt.dao.UserDao;
import com.bao.shirojwt.entity.User;
import com.bao.shirojwt.utils.JwtUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    private static final String encryptSalt = "F12839WhsnnEV$#23b";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserDao userDao;

    /**
     * 每次登录，生成一个新的token，
     * 生成的token中
     * @param username
     * @return
     */
    public String generateAndStoreJwtToken(String username) {
        // 这里的salt是用来加密token的salt，与加密password的salt不是一回事。
        String lastLoginSalt = JwtUtils.generateSalt();
        String jwtString = JwtUtils.sign(username, lastLoginSalt, 3600);
        userDao.updateLoginSalt(username, lastLoginSalt, jwtString);
        return jwtString;
    }

    /**
     * 获取用户的数据，主要要获取加密后的密码，用于比对
     * @param username
     * @return
     */
    public User getUserInfo(String username) {
        User user = userDao.findUserByUsername(username);
        // 这里从数据库中查
        return user;
    }

    /**
     * 获取此用户最后一次登录的 salt 和 用户信息
     * 通过salt 和 username就可以得到 最后一次登录的token，然后用来与最新一次请求的token做对比
     * @return
     */
    public User getJwtTokenInfo(String username) {
        User user = getUserInfo(username);
        return user;
    }

    /**
     * 获取用户角色列表，这里最好从缓存中获取
     * @param userId
     * @return
     */
    public List<String> getUserRoles(Integer userId) {
        return Arrays.asList("admin");
    }

    /**
     * 用户注册， 要处理的逻辑有
     * 1. 加密密码
     * 2. 将部分逻辑同步到redis缓存中，暂时没有必要
     */
    public Boolean userRegister(String username, String password) {
        // 先生成salt， 用SecureRandomNumberGenerator来生成
        String salt = new SecureRandomNumberGenerator().nextBytes().toHex();

        // 用 simpleHash 类来加密
        SimpleHash hash = new SimpleHash(Sha256Hash.ALGORITHM_NAME, password, ByteSource.Util.bytes(salt), 1);
        String encodedPassword = hash.toHex();

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setEncryptSalt(salt);
        try {
            if (userDao.insertUser(user) != 1) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

}
