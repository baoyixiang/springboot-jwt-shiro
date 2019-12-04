package com.bao.shirojwt.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bao.shirojwt.domain.user.LoginInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@Component
public class TokenSchedulerTask {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisTemplate redisTemplate;

    // 每个月清除一次不活跃用户
    // 不活跃用户的判定 还未实现
    @Scheduled(cron="*/6 * * * * ?")
    private void process(){
        Map<Object, Object> tokenInfos = redisTemplate.opsForHash().entries("user_token_info");
        // 遍历用户登录信息，筛选不活跃用户，即一个月未登录的用户，然后删掉其tokeninfo，下次他再来就要重新登录。
        ArrayList<Object> deleteKeys = new ArrayList<>(100); // 分配初始capacity，避免过多的扩容，影响性能。
        for (Object key : tokenInfos.keySet()) {
            LoginInfo curInfo = JSON.parseObject(JSONObject.toJSONString(tokenInfos.get(key), true), LoginInfo.class);
            Date judgeDate = new Date(curInfo.getLastLoginTime().getTime() + 3600*24*30*1000) ;
            if ( judgeDate.before(new Date()) ) {
                // 说明这个用户的 token 信息已经一个月没有更新过了
                deleteKeys.add(key);
            }
        }

        if (deleteKeys.size() != 0) {
            Long effectedNum = redisTemplate.opsForHash().delete("user_token_info", deleteKeys.toArray());

            logger.info("清除了redis缓存中" + effectedNum + "条不活跃用户的登录信息");
        }
    }
}
