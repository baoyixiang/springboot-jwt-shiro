package com.bao.shirojwt.controller;

import com.bao.shirojwt.service.UserService;
import com.bao.shirojwt.domain.ResponseVO;
import com.bao.shirojwt.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @PostMapping("register")
    public ResponseVO register(@RequestParam String username, @RequestParam String password) {
        if (username == null || password == null) {
            return new ResponseVO(false, "用户名或者密码不能为空");
        }

        if (userService.userRegister(username, password)) {
            return new ResponseVO(true, null);
        } else {
            return new ResponseVO(false, "注册失败");
        }

    }

    @PostMapping("login")
    public ResponseVO login(@RequestBody User userVO, HttpServletRequest request, HttpServletResponse response) {
        Subject subject = SecurityUtils.getSubject();
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(userVO.getUsername(), userVO.getPassword());
            subject.login(token);  // 登录认证和获取访问权限交给shiro去做

            // 如果登录成功，生成jwt的token返回给前端
            User user = (User) subject.getPrincipal();
            String newToken = userService.generateAndStoreJwtToken(user.getUsername());
            response.setHeader("x-auth-token", newToken);

            return new ResponseVO(true, "登录成功");
        } catch (UnknownAccountException e) {
            return new ResponseVO(false, "用户不存在，请注册");
        } catch (AuthenticationException e) {
            logger.error("User {} login fail, Reason: {}", userVO.getName(), e.getMessage());
            return new ResponseVO(false, "用户名或密码错误");
        } catch (Exception e) {
            return new ResponseVO(false, "服务器出问题啦，请联系管理员");
        }
    }

    @GetMapping("logout")
    public ResponseEntity<Void> logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.getPrincipals() != null) {
            User user = (User) subject.getPrincipals().getPrimaryPrincipal();
            // 这里要删除用户的登录信息
        }
        SecurityUtils.getSubject().logout();
        return ResponseEntity.ok().build();
    }
}
