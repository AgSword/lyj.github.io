package com.example.seckill.vo;

import com.example.seckill.bean.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Array;
import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DetailVo {
    private User user;
    private GoodsVo goodsVo;
    private int seckillStatus;
    private int remainSeconds;

}
