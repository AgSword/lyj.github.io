package com.example.seckill.config;

import com.example.seckill.bean.User;

public class UserContext {

    private static ThreadLocal<User> userHolder=new ThreadLocal<>();

    public static void setUser (User user){
        userHolder.set(user);
    }

    public static User getUser(){
        return userHolder.get();
    }
}