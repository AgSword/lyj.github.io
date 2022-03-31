package com.example.seckill.controller;

import com.example.seckill.bean.User;
import com.example.seckill.rabbitmq.MQSender;
import com.example.seckill.utils.CookieUtil;
import com.example.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lyj
 * @since 2022-03-13
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Autowired
    MQSender mqSender;


//    @ResponseBody
//    @RequestMapping("/info")
//    public RespBean userInfo(HttpServletRequest request, HttpServletResponse response){
//        String ticket = CookieUtil.getCookieValue(request, "userTicket");
//        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
//        return RespBean.success(user);
//    }

    @ResponseBody
    @RequestMapping("/info")
    public RespBean userInfo(User user){
        return RespBean.success(user);
    }

    @RequestMapping("/mq")
    @ResponseBody
    public void mq(){
        mqSender.sendSeckillMessage("hello");
    }

}
