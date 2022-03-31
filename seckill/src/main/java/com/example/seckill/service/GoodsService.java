package com.example.seckill.service;

import com.example.seckill.bean.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lyj
 * @since 2022-03-16
 */
public interface GoodsService extends IService<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoById(Long id);
}
