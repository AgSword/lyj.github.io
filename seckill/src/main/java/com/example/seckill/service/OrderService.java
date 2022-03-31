package com.example.seckill.service;

import com.example.seckill.bean.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckill.bean.User;
import com.example.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lyj
 * @since 2022-03-16
 */
public interface OrderService extends IService<Order> {


    OrderDetailVo detail(Long orderId);

    String createPath(User user, Long goodsId);

    Boolean checkPath(User user, Long id, String path);

    boolean checkCaptcha(User user, Long goodsId, String captcha);

}
