package com.example.seckill.config;

import com.example.seckill.bean.User;
import com.example.seckill.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        System.out.println("进入拦截器");
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
//        System.out.println(user);
        if (user == null) {
            System.out.println("拦截器生效："+ticket);
            response.sendRedirect("/login/toLogin");
            return false;
        }
        return true;
    }
}
