package com.bao.shirojwt;

import com.bao.shirojwt.dao.UserDao;
import com.bao.shirojwt.domain.user.LoginInfo;
import com.bao.shirojwt.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;

@SpringBootTest
class ShirojwtApplicationTests {

    public static class Origin{
        private String id;
        private String name;
        private String des;

        public Origin(String id, String name, String des) {
            this.id = id;
            this.name = name;
            this.des = des;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }
    }
    public static class Destination{
        private String id;
        private String name;

        public Destination() {
        }

        public Destination(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    @Test
    void contextLoads() {
        Origin origin = new Origin("1", "bao", "hahahahahaha");
        Destination destination = new Destination();
        BeanUtils.copyProperties(origin, destination);
        System.out.println(destination.name);
    }

}
