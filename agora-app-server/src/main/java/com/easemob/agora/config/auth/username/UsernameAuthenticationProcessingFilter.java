package com.easemob.agora.config.auth.username;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easemob.agora.model.UserAuth;
import com.easemob.agora.utils.ServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;

@Slf4j
public class UsernameAuthenticationProcessingFilter extends OncePerRequestFilter {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String APPKEY = "appkey";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        RequestWrapper requestWrapper = new RequestWrapper(request);
        // 获取手机号和验证码
        Map<String, Object> body = getBody(requestWrapper);
        String username = obtainUsername(request, body);
        String password = obtainPassword(request, body);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (!StringUtils.isEmpty(username) && securityContext.getAuthentication() == null) {

            String appKey = obtainAppKey(request, body);
            String orgName = ServiceUtil.getOrg(appKey);
            String appName = ServiceUtil.getApp(appKey);
            UserAuth userAuth = new UserAuth(orgName, appName, password);
            //创建令牌
            AbstractAuthenticationToken authenticationToken =
                    new UsernameAuthenticationToken(username, userAuth);
            // 允许子类设置“details”属性
            setDetails(request, authenticationToken);

            securityContext.setAuthentication(authenticationToken);
        }

        filterChain.doFilter(requestWrapper, response);
    }

    private Map<String, Object> getBody(HttpServletRequest request) {
        try {
            BufferedReader br = request.getReader();
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }

            JSONObject result = JSON.parseObject(sb.toString());
            return result.getInnerMap();
        } catch (Exception e) {
            log.debug("username filter get body failed.");
        }
        return Collections.emptyMap();
    }

    private String obtainUsername(HttpServletRequest request, Map<String, Object> body) {
        String username = request.getParameter(USERNAME);
        if (StringUtils.isEmpty(username)) {
            username = body.containsKey(USERNAME) ? body.get(USERNAME).toString() : "";
            request.setAttribute(USERNAME, username);
        }
        return username;
    }

    private String obtainPassword(HttpServletRequest request, Map<String, Object> body) {
        String password = request.getParameter(PASSWORD);
        if (StringUtils.isEmpty(password)) {
            password = body.containsKey(PASSWORD) ? body.get(PASSWORD).toString() : "";
        }
        return password;
    }

    private String obtainAppKey(HttpServletRequest request, Map<String, Object> body) {
        String password = request.getParameter(APPKEY);
        if (StringUtils.isEmpty(password)) {
            password = body.containsKey(APPKEY) ? body.get(APPKEY).toString() : "";
        }
        return password;
    }

    private void setDetails(HttpServletRequest request,
            AbstractAuthenticationToken authRequest) {
        authRequest.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    }

    class RequestWrapper extends HttpServletRequestWrapper {

        private final byte[] body;

        /**
         * Constructs a request object wrapping the given request.
         *
         * @param request The request to wrap
         * @throws IllegalArgumentException if the request is null
         */
        public RequestWrapper(HttpServletRequest request) {
            super(request);
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader br = request.getReader();
                String str;
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }

            } catch (Exception e) {
                log.debug("username filter get body failed.");
            }
            body = sb.toString().getBytes();
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream bais = new ByteArrayInputStream(body);

            return new ServletInputStream() {
                @Override
                public int read() throws IOException {
                    return bais.read();
                }

                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override public void setReadListener(ReadListener listener) {
                    // nothing
                }
            };
        }
    }

}
