package com.example.seckill.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    UserArgumentResolver userArgumentResolver;
//    @Autowired
//    LoginInterceptor loginInterceptor;
    @Autowired
    AccessLimitInterceptor accessLimitInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLimitInterceptor);
    }

    //    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        ArrayList<String> list = new ArrayList<>();
//        list.add("/bootstrap/**");
//        list.add("/css/**");
//        list.add("/fonts/**");
//        list.add("/img/**");
//        list.add("/jquery-validation/**");
//        list.add("/js/**");
//        list.add("/layer/**");
//        list.add("/login/**");
//        list.add("/templates/**");
//        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns(list);
//    }
}
