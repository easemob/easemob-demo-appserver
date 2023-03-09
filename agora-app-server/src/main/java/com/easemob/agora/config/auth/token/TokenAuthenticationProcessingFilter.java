package com.easemob.agora.config.auth.token;

import com.easemob.agora.model.UserAuth;
import com.easemob.agora.utils.ServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class TokenAuthenticationProcessingFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        //获取手机号和验证码
        String token = obtainToken(request);

        String username = obtainUsername(request);

        if (!StringUtils.isEmpty(token) && !StringUtils.isEmpty(username)) {
            //创建令牌
            String appKey = obtainAppKey(request);

            String orgName = ServiceUtil.getOrg(appKey);
            String appName = ServiceUtil.getApp(appKey);

            UserAuth userAuth = new UserAuth(orgName, appName, token);

            AbstractAuthenticationToken authenticationToken =
                    new AuthenticationToken(username, userAuth);
            // 允许子类设置“details”属性
            setDetails(request, authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }

    private String obtainUsername(HttpServletRequest request) {
        return request.getParameter("userAccount");
    }

    private String obtainAppKey(HttpServletRequest request) {
        return request.getParameter("appkey");
    }

    private String obtainToken(HttpServletRequest request) {
        String authzHeader = request.getHeader(AUTHORIZATION);

        if (StringUtils.isEmpty(authzHeader)) {
            return "";
        }
        return authzHeader.startsWith("Bearer ") ? authzHeader.split(" ")[1] : authzHeader;
    }

    private void setDetails(HttpServletRequest request,
            AbstractAuthenticationToken authRequest) {
        authRequest.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    }
}
