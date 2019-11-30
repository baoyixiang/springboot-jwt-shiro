package com.bao.shirojwt.dao;

import com.bao.shirojwt.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserDao {

    @Select("select * from user")
    List<User> listUser();

    @Insert("insert into user(username,password,name,last_login_salt,roles,encrypt_salt,last_login_token) " +
            "values(#{username},#{password},#{name},#{lastLoginSalt},#{roles},#{encryptSalt},#{lastLoginToken})")
    Integer insertUser(User user);

    @Select("select * from user where username=#{username}")
    User findUserByUsername(String username);

    // 根据username更新 lastlogintoken
    @Update("update user set last_login_salt=#{lastLoginSalt}, last_login_token=#{lastLoginToken}")
    Integer updateLoginSalt(String username, String lastLoginSalt, String lastLoginToken);
}
