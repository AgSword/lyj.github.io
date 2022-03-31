package com.example.seckill.controller;

import com.example.seckill.bean.User;
import com.example.seckill.service.GoodsService;
import com.example.seckill.service.UserService;
import com.example.seckill.utils.CookieUtil;
import com.example.seckill.vo.DetailVo;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller

public class GoodsController {

    @Autowired
    UserService iUserService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    //页面缓存

    @RequestMapping(value = "/goods/to_list",produces = "text/html;charset=utf-8")
    @ResponseBody()
    public String toGood(Model model, HttpServletRequest request, HttpServletResponse response, User user) {
        //在使用在程序中提前集中校验是否登录的的方式后，下面
//        if(!StringUtils.hasLength(cookie)){
//            return "login";
//        }
//        //将用户信息存入redis而不是session后，下面一行被注释
////        User user = (User)session.getAttribute(cookie);
//        User user = iUserService.getUserByTicket(cookie, request, response);
//        if (user==null){
//            return "login";
//        }
        ValueOperations opsForValue = redisTemplate.opsForValue();
        String html = (String) opsForValue.get("goods_list");
        if(StringUtils.hasLength(html)){
            return html;
        }
//        String ticket = CookieUtil.getCookieValue(request, "userTicket");
//        User user = iUserService.getUserByTicket(ticket, request, response);
        List<GoodsVo> goodsVo = goodsService.findGoodsVo();
//        goodsVo.forEach(goodsVo1 -> System.out.println());
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsVo);
        //如果没有对应的缓存，手动渲染，并放入缓存中
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", context);
        if (StringUtils.hasLength(html)){
            opsForValue.set("goods_list",html,10, TimeUnit.SECONDS);
        }
        return html;
    }

//    @RequestMapping(value = "/toDetail/{id}",produces = "text/html;charset=utf-8")
//    @ResponseBody
//    public String toDetail(Model model, @PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response,User user) {
//        ValueOperations opsForValue = redisTemplate.opsForValue();
//        String html =(String) opsForValue.get("goods_detail" + id);
//        if(StringUtils.hasLength(html)){
//            return html;
//        }
////        String ticket = CookieUtil.getCookieValue(request, "userTicket");
////        User user = iUserService.getUserByTicket(ticket, request, response);
////        System.out.println(id);
//        GoodsVo goodsVo = goodsService.findGoodsVoById(id);
////        System.out.println(goodsVo);
//        Date startDate = goodsVo.getStartDate();
//        Date endDate = goodsVo.getEndDate();
//        Date nowdate = new Date();
////        System.out.println("startDate:" + startDate);
////        System.out.println("endDate:" + endDate);
////        System.out.println("nowdate:" + nowdate);
//        int seckillStatus = 0;
//        int remainSeconds = 0;
//        int seckillSeconds = (int) ((endDate.getTime() - startDate.getTime()) / 1000);
//        if (nowdate.before(startDate)) {
//            remainSeconds = ((int) (startDate.getTime() - nowdate.getTime()) / 1000);
//        } else if (nowdate.after(endDate)) {
//            seckillStatus = 2;
//            remainSeconds = -1;
//            seckillSeconds=0;
//        } else {
//            seckillStatus = 1;
//            remainSeconds = 0;
//            seckillSeconds=(int) ((endDate.getTime() - nowdate.getTime()) / 1000);
//        }
////        System.out.println("seckillStatus:" + seckillStatus);
//        model.addAttribute("seckillSeconds", seckillSeconds);
//        model.addAttribute("seckillStatus", seckillStatus);
//        model.addAttribute("remainSeconds", remainSeconds);
//        model.addAttribute("user", user);
//        model.addAttribute("goods", goodsVo);
//        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
//        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", context);
//        opsForValue.set("goods_detail"+id,html,10,TimeUnit.SECONDS);
//        return html;
//    }

    //页面静态化，前后端分离
    @RequestMapping("/goods/toDetail/{id}")
    @ResponseBody
    public RespBean toDetail(Model model, @PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response, User user) {


        GoodsVo goodsVo = goodsService.findGoodsVoById(id);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowdate = new Date();
        int seckillStatus = 0;
        int remainSeconds = 0;
        int seckillSeconds = (int) ((endDate.getTime() - startDate.getTime()) / 1000);
        if (nowdate.before(startDate)) {
            remainSeconds = ((int) (startDate.getTime() - nowdate.getTime()) / 1000);
        } else if (nowdate.after(endDate)) {
            seckillStatus = 2;
            remainSeconds = -1;
            seckillSeconds=0;
        } else {
            seckillStatus = 1;
            remainSeconds = 0;
            seckillSeconds=(int) ((endDate.getTime() - nowdate.getTime()) / 1000);
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSeckillStatus(seckillSeconds);
        detailVo.setRemainSeconds(remainSeconds);

        return RespBean.success(detailVo);
    }
}
