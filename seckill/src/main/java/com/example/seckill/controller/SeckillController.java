package com.example.seckill.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.example.seckill.bean.Order;
import com.example.seckill.bean.SeckillOrder;
import com.example.seckill.bean.User;
import com.example.seckill.config.AccessLimit;
import com.example.seckill.exception.GlobalException;
import com.example.seckill.rabbitmq.MQSender;
import com.example.seckill.service.GoodsService;
import com.example.seckill.service.OrderService;
import com.example.seckill.service.SeckillOrderService;
import com.example.seckill.service.SeckillService;
import com.example.seckill.utils.CookieUtil;
import com.example.seckill.utils.JsonUtil;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.vo.RespBean;
import com.example.seckill.vo.RespBeanEnum;
import com.example.seckill.vo.SeckillMessage;
import com.rabbitmq.tools.json.JSONUtil;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Autowired
    SeckillOrderService seckillOrderService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    MQSender mqSender;

    @Autowired
    DefaultRedisScript script;

    @Autowired
    OrderService orderService;

    private Map<Long,Boolean> emptyStockMap=new HashMap<>();
//    @Transactional
//    @PostMapping ("/do_seckill")
//    public String doSeckill(Model model, HttpServletRequest request, HttpServletResponse response,
//                            @RequestParam(value = "goodsId") Long id,User user){
////        String ticket = CookieUtil.getCookieValue(request, "userTicket");
////        User user = (User) redisTemplate.opsForValue().get("user:" +ticket);
//        if(user==null){
//            return "redirect:/login/toLogin";
//        }
//        GoodsVo goodsVo = goodsService.findGoodsVoById(id);
//        System.out.println(goodsVo.getStockCount());
//        if(goodsVo.getStockCount()<1){
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
//            return "miaosha_fail";
//        }
//        SeckillOrder one = iSeckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", id));
//        if(one!=null){
//            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
//            return "miaosha_fail";
//        }
//        Order order=seckillService.seckill(user,goodsVo);
//        model.addAttribute("orderInfo",order);
//        model.addAttribute("goods",goodsVo);
//        return "order_detail";
//    }


    @PostMapping ("/{path}/do_seckill")
    @ResponseBody
    public RespBean doSeckill(@PathVariable("path") String path ,@RequestParam(value = "goodsId") Long id, User user){
        if(user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Boolean check = orderService.checkPath(user,id,path);
        if(!check){
            return RespBean.error(RespBeanEnum.REQUESR_ILLEGAL);
        }

        GoodsVo goodsVo = goodsService.findGoodsVoById(id);
        //判断是否重复抢购
        SeckillOrder seckillOrder  = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + id);
        if(seckillOrder!=null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        if(emptyStockMap.get(id)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //预减库存
        Long stock = redisTemplate.opsForValue().decrement("seckillGoods:" + id);
//        Long stock = (Long) redisTemplate.execute(script, Collections.emptyList());
        if(stock<0){
            emptyStockMap.put(id,true);
            redisTemplate.opsForValue().increment("seckillGoods:" + id);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage message = new SeckillMessage(user, id);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(message));
        return RespBean.success(0);


        /*
        GoodsVo goodsVo = goodsService.findGoodsVoById(id);
        System.out.println(goodsVo.getStockCount());
        //判断库存
        if(goodsVo.getStockCount()<1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillOrder one = iSeckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", id));
        //判断是否重复抢购
//        if(one!=null){
//            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
//            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
//        }
        SeckillOrder seckillOrder  = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsVo.getId());
        Order order=seckillService.seckill(user,goodsVo);
        model.addAttribute("orderInfo",order);
        model.addAttribute("goods",goodsVo);
        return RespBean.success(order);
        */

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVo = goodsService.findGoodsVo();
        goodsVo.forEach(goodsVo1 -> {redisTemplate.opsForValue().set("seckillGoods:"+goodsVo1.getId(),goodsVo1.getStockCount());
            emptyStockMap.put(goodsVo1.getId(),false);

        });
    }

    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user,Long goodsId){
        if (user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId=seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }

    @AccessLimit(second=5,maxCount=5,needLogin=true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user,Long goodsId,@RequestParam(value = "verifyCode") String captcha){
        if(user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        boolean check = orderService.checkCaptcha(user,goodsId,captcha);
        if (!check){
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }
        String str= orderService.createPath(user,goodsId);
        System.out.println(str);
        return RespBean.success(str);
    }

    @RequestMapping(value = "/captcha",method = RequestMethod.GET)
    public void verifyCode(User user,Long goodsId,HttpServletResponse response){
        if (user==null||goodsId<0){
            throw new GlobalException(RespBeanEnum.REQUESR_ILLEGAL);
        }
        //设置请求头为输出图片的类型
        response.setContentType("image/jpg");
        response.setHeader("Pargam","No-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires",0L);
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId,captcha.text(),300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败",e.getMessage());
        }
    }
}
