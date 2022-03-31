package com.example.seckill.mapper;

import com.example.seckill.bean.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lyj
 * @since 2022-03-16
 */
@Component
public interface GoodsMapper extends BaseMapper<Goods> {

    @Select("SELECT g.id,g.goods_name,g.goods_title,g.goods_img,g.goods_detail,g.goods_price,g.goods_stock," +
            "sg.seckill_price,sg.stock_count,sg.start_date,sg.end_date FROM t_goods g INNER JOIN t_seckill_goods AS sg ON g.id=sg.goods_id")
    List<GoodsVo> findGoodsVo();

    @Select("SELECT g.id,g.goods_name,g.goods_title,g.goods_img,g.goods_detail,g.goods_price,g.goods_stock," +
            "sg.seckill_price,sg.stock_count,sg.start_date,sg.end_date FROM t_goods g INNER JOIN t_seckill_goods AS sg ON g.id=sg.goods_id where g.id=#{id}")
    GoodsVo findGoodsVoById(Long id);
}
