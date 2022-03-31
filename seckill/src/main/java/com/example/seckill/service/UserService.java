package com.example.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckill.bean.User;
import com.example.seckill.vo.LoginVo;
import com.example.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lyj
 * @since 2022-03-13
 */
public interface UserService extends IService<User> {

    RespBean toLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    User getUserByTicket(String ticket,HttpServletRequest request,HttpServletResponse response);

}
