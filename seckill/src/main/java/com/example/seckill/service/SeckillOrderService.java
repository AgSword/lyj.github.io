package com.example.seckill.service;

import com.example.seckill.bean.SeckillOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckill.bean.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lyj
 * @since 2022-03-16
 */
public interface SeckillOrderService extends IService<SeckillOrder> {

    Long getResult(User user, Long goodsId);
}
