package com.dh.framework.webmvc.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 字符编码过滤器
 * 统一设置请求和响应的字符编码为UTF-8
 */
public class CharacterEncodingFilter implements Filter {

    private String encoding = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String configEncoding = filterConfig.getInitParameter("encoding");
        if (configEncoding != null && !configEncoding.trim().isEmpty()) {
            this.encoding = configEncoding;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        // 设置请求编码
        req.setCharacterEncoding(encoding);
        // 设置响应编码和默认Content-Type
        resp.setCharacterEncoding(encoding);
        resp.setContentType("text/plain;charset=" + encoding);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

} 