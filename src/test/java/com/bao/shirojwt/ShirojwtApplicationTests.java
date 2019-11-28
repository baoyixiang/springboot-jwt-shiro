package com.bao.shirojwt;

import com.bao.shirojwt.dao.UserDao;
import com.bao.shirojwt.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShirojwtApplicationTests {

    @Autowired
    private UserDao userDao;

    @Test
    void contextLoads() {
        userDao.listUser().stream().forEach((user) -> System.out.println(user.getName()));
    }

}
