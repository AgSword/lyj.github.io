package com.example.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class MD5Util {
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt="1a2b3c4d";

    public static String inputPassToFromPass(String inputPass){
        String s=""+salt.charAt(0)+salt.charAt(1)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(s);
    }

    public static String fromPassToDbPass(String fromPass,String salt){
        String s=""+salt.charAt(0)+salt.charAt(1)+fromPass+salt.charAt(5)+salt.charAt(4);
        return md5(s);
    }

    public static String inputPassToDbPass(String input,String salt){
        return  fromPassToDbPass(inputPassToFromPass(input),salt);
    }

    public static void main(String[] args) {
        String str="123456";
        System.out.println(inputPassToFromPass(str));  // c21bff23915a157e38fb8bd1a3f127d5
        System.out.println(inputPassToDbPass(str,"1a2b3c4d"));  // 8b52a637743c423ebdfa9f1213db8596
    }
}
