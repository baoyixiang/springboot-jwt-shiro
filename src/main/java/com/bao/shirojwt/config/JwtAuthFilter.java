package com.bao.shirojwt.config;

import com.bao.shirojwt.UserService;
import com.bao.shirojwt.entity.User;
import com.bao.shirojwt.utils.JwtUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JwtAuthFilter extends AuthenticatingFilter {
    private final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private static final int TOKEN_REFRESH_INTERVAL = 300;

    @Autowired
    private UserService userService;

    public JwtAuthFilter() {
        this.setLoginUrl("/login");
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            return false;
        }

        return super.preHandle(request, response);
    }

    // 跨域处理
    @Override
    protected void postHandle(ServletRequest request, ServletResponse response) throws Exception {
        this.fillCorsHeader(WebUtils.toHttp(request), WebUtils.toHttp(response));
        request.setAttribute("jwtShiroFilter.FILTERED", true);
    }

    /**
     * 父类会在亲求进入拦截器后调用该方法，返回true则继续，返回false则调用onAccessDenied()方法，不通过时还会调用isPermissive
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (this.isLoginRequest(request, response)) {
            return true;
        }
        Boolean afterFiltered = (Boolean)(request.getAttribute("jwtShiroFilter.FILTERED"));
        if (BooleanUtils.isTrue(afterFiltered)) {
            return true;
        }

        boolean allowed = false;
        try {
            // 相当于每次请求都做了一次login？
            allowed = executeLogin(request, response);
        } catch (IllegalStateException e) {
            // 没有找到token
            logger.error("Not found any token");
        } catch (Exception e) {
            logger.error("Error occurs when login", e);
        }
        return allowed || super.isPermissive(mappedValue);
    }

    /**
     * 这里重写了父类的方法，使用我们自己定义的Token类，提交给shiro，这个方法返回null的话会直接抛出异常，进入isAccessAllowed()的异常处理逻辑
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        String jwtToken = getAuthcHeader(servletRequest);
        if (StringUtils.isNoneBlank(jwtToken) && !JwtUtils.isTokenExpired(jwtToken)) {
            return new JwtToken(jwtToken);
        }

        // 也就是说当token不存在或者token已经过期了，就返回null，此时当作异常处理。
        return null;
    }

    /**
     * 如果isAccessAllowed() 返回为false，就会调用这个方法，代表用户被拦截了，在这里我们直接返回错误的response
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(servletResponse);
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.setStatus(HttpStatus.SC_UNAUTHORIZED); // 状态码203，意思为没有授权信息
        fillCorsHeader(WebUtils.toHttp(servletRequest), httpServletResponse);
        return false;
    }

    /**
     * 用户登录成功会调用的方法，在此方法中还判断了token是否需要刷新
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        String newToken = null;
        if (token instanceof JwtToken) {
            JwtToken jwtToken = (JwtToken) token;
            User user = (User) subject.getPrincipal();
            boolean shouldRefresh = shouldTokenRefresh(JwtUtils.getIssuedAt(jwtToken.getToken()));
            if (shouldRefresh) {
                newToken = userService.generateAndStoreJwtToken(user.getUsername());
            }
        }
        // 如果token刷新了，就把新的token放到response头中返回给前端
        if (StringUtils.isNotBlank(newToken)) {
            httpServletResponse.setHeader("x-auth-token", newToken);
        }

        return true;
    }

    /**
     * 登录失败的方法，在这个方法中我们什么都不做，逻辑放到了onAccessDenied方法中
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        logger.error("Validate token fail, token: {}, error: {}", token.toString(), e.getMessage());
        return false;
    }

    protected String getAuthcHeader(ServletRequest request) {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        String header = httpServletRequest.getHeader("x-auth-token");
        return StringUtils.removeStart(header, "Bearer ");
    }

    protected boolean shouldTokenRefresh(Date issueAt) {
        LocalDateTime issueTime = LocalDateTime.ofInstant(issueAt.toInstant(), ZoneId.systemDefault());
        return LocalDateTime.now().minusSeconds(TOKEN_REFRESH_INTERVAL).isAfter(issueTime);
    }

    protected void fillCorsHeader(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,HEAD");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
    }
}
