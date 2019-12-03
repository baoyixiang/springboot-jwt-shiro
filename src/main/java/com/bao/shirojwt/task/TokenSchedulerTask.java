package com.bao.shirojwt.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenSchedulerTask {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 每个月清除一次不活跃用户
    // 不活跃用户的判定 还未实现
    @Scheduled(cron="*/6 * * * * ?")
    private void process(){
        Object[] keys = stringRedisTemplate.opsForHash().entries("user_token_salt").keySet().toArray();
        if (keys.length != 0) {
            Long effectedNum = stringRedisTemplate.opsForHash().delete("user_token_salt", keys);
            System.out.println(effectedNum);
        }
    }
}
