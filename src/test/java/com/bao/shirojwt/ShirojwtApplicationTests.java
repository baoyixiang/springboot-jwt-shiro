package com.bao.shirojwt;

import com.bao.shirojwt.dao.UserDao;
import com.bao.shirojwt.domain.user.LoginInfo;
import com.bao.shirojwt.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;

@SpringBootTest
class ShirojwtApplicationTests {

    @Autowired
    private UserDao userDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        LoginInfo info = new LoginInfo();
        info.setLastLoginSalt("asasasasasasas");
        info.setLastLoginTime(new Date());
        redisTemplate.opsForHash().put("user_token_info", "bao", info);
    }

}
