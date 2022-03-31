package com.example.seckill.vo;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
public enum RespBeanEnum {
    SUCCESS(200,"成功"),
    ERROR(500,"服务端异常"),
    //登录模块
    Login_ERROR(500210,"用户名或密码错误"),
    MOBILE_ERROR(500211,"手机号码格式不正确"),
    BIND_ERROR(500213,"参数校验异常"),
    SESSION_ERROR(500214,"用户不存在"),
    //秒杀模块
    EMPTY_STOCK(500500,"库存不足"),
    REPEATE_ERROR(500501,"每人限购一件"),
    REQUESR_ILLEGAL(500502,"请求非法,请重新尝试"),
    ERROR_CAPTCHA(500503,"验证码错误，请重新输入"),
    ACCESS_LIMIT_REAHCED(500504,"访问过于频繁"),
    //订单模块
    ORDER_NOT_EXIST(500300,"订单信息不存在"),
    ;


    private final Integer code;
    private final String message;

}
