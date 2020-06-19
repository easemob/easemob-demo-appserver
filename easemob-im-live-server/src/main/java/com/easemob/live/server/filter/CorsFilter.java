package com.easemob.live.server.filter;

import com.easemob.live.server.utils.CORSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author shenchong@easemob.com 2020/3/25
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        // 根据 request headers 处理 response headers, 避免跨域。
        CORSUtils.allowAllOrigins(request, response);

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("doFilter | Failed to forward request due to exception", e);
            throw e;
        }
    }
}
