package com.panda.sport.rcs.config;

import com.panda.sport.rcs.CommonUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import java.io.IOException;

/**
 * 接口traceId 设置
 *
 * @description:
 * @author: magic
 * @create: 2022-06-05 10:15
 **/
@Slf4j
public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            CommonUtils.mdcPut();
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            CommonUtils.mdcRemove();
        }
    }
}