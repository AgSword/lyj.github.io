package com.example.seckill.service;

import com.example.seckill.bean.Order;
import com.example.seckill.bean.User;
import com.example.seckill.vo.GoodsVo;

public interface SeckillService {
    Order seckill(User user, GoodsVo goodsVo);
}
