package com.bao.shirojwt.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.http.HttpStatus;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class AnyRolesAuthorizationFilter extends AuthorizationFilter {

    @Override
    protected void postHandle(ServletRequest request, ServletResponse response) throws Exception {
        request.setAttribute("anyRolesAuthFilter.FILTERED", true);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        Boolean afterFiltered = (Boolean) servletRequest.getAttribute("anyRolesAuthFilter.FILTERED");
        if (BooleanUtils.isTrue(afterFiltered))
            return true;

        Subject subject = getSubject(servletRequest, servletResponse);
        String[] rolesArray = (String[]) o;
        if (rolesArray == null || rolesArray.length == 0) {
            // 没有角色限制，又权限访问
            return true;
        }
        for (String role : rolesArray) {
            if (subject.hasRole(role)) {
                // 当前用户有访问权限
                return true;
            }
        }
        return false;
    }

    /**
     * 如果 isAccessAllowed返回为false，就会调用这个方法
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
        return false;
    }
}
