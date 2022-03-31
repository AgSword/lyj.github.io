package com.example.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.seckill.bean.User;
import com.example.seckill.exception.GlobalException;
import com.example.seckill.mapper.UserMapper;
import com.example.seckill.service.UserService;
import com.example.seckill.utils.CookieUtil;
import com.example.seckill.utils.MD5Util;
import com.example.seckill.utils.UUIDUtil;
import com.example.seckill.vo.LoginVo;
import com.example.seckill.vo.RespBean;
import com.example.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lyj
 * @since 2022-03-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Override
    public RespBean toLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //因为使用自定义注解参数校验，下面的代码可以注释掉
//        if(!StringUtils.hasLength(mobile)||!StringUtils.hasLength(password)){
//            return RespBean.error(RespBeanEnum.Login_ERROR);
//        }
//        if(!ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }
        //根据前端输入的mobile查询数据中的用户
        User user = userMapper.selectById(mobile);
        if(user==null){
            //采用Exception类，抛出异常的方式后，下面一行就可以注释掉了
//            return RespBean.error(RespBeanEnum.Login_ERROR);
            throw new GlobalException(RespBeanEnum.Login_ERROR);
        }
        //校验密码
        if(!MD5Util.fromPassToDbPass(password,user.getSalt()).equals(user.getPassword())){
            //采用抛出异常的方式
//            return RespBean.error(RespBeanEnum.Login_ERROR);
            throw new GlobalException(RespBeanEnum.Login_ERROR);
        }
        //生成ticket
        String ticket = UUIDUtil.uuid();
        //将对象与ticket信息放到redis后，而不是session后这一行就可以注释掉
//        request.getSession().setAttribute(ticket,user);
        redisTemplate.opsForValue().set("user:"+ticket,user);
        CookieUtil.setCookie(request,response,"userTicket",ticket);
        return RespBean.success();
    }

    @Override
    public User getUserByTicket(String ticket, HttpServletRequest request, HttpServletResponse response) {
        if(!StringUtils.hasLength(ticket)){
            return null;
        }
        User user = (User)redisTemplate.opsForValue().get("user:" + ticket);
        if(user!=null){
            CookieUtil.setCookie(request,response,"userTicket",ticket);
        }
        return user;
    }
}
