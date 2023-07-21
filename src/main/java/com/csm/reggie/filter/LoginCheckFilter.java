package com.csm.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.csm.reggie.common.BaseContext;
import com.csm.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {



        //向下转型
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();
        log.info("拦截到请求: {}", requestURI);
        //4.判断登陆状态，如果已登录，就直接放行
        if (request.getSession().getAttribute("employee") != null){
            Long empId = (Long)request.getSession().getAttribute("employee");
            log.info("用户已登录,用户id为: {}",empId);
            //输出检查一下当前线程的Id看看是否和登录是同一个线程
            log.info("当前线程的id为 {}", Thread.currentThread().getId());
            //将empId存入threadLocal封装类中保存
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }

        String[] urls =new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/frond/**",
                "/common/**"
        };

        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //3.如果不需要处理，直接放行
        if (check){
            log.info("本次请求 {} 不需要处理",requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        log.info("用户未登录");
        //5.如果没有登录则返回登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    /**
     * 路径匹配，检查本次请求是否放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
//                log.info("放行: {}",requestURI);
                return true;
            }else {
//                log.info("需要处理请求: {}",requestURI);
            }
        }
        return false;
    }



}
