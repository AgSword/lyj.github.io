package com.example.seckill;

import com.example.seckill.bean.User;
import com.example.seckill.utils.MD5Util;
import com.example.seckill.utils.UUIDUtil;
import com.example.seckill.utils.UserAddUtil;
import com.example.seckill.utils.UserUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class SeckillApplicationTests {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Test
    void contextLoads() throws Exception {
        int count=5000;
        List<User> users = new ArrayList<>(count);
        //生成用户
        for(int i=0;i<count;i++){
            User user = new User();
            user.setId(16062700000L+i);
            user.setNickname("suer"+i);
            user.setSalt("1a2b3c4d");
            user.setPassword(MD5Util.inputPassToDbPass("123456",user.getSalt()));
            user.setHead("1");
            user.setRegisterDate(new Date());
            user.setLastLoginDate(new Date());
            user.setLoginCount(1);
            users.add(user);
        }
        File file = new File("C:\\Users\\li140\\Desktop\\config.txt");
        if(file.exists()){
            file.delete();
        }
        RandomAccessFile raf= new RandomAccessFile(file,"rw");
        raf.seek(0);

        for(User user:users){
            String ticket = UUIDUtil.uuid();
            redisTemplate.opsForValue().set("user:"+ticket,user);
//
//            //写入config.txt 一行
            String row=user.getId()+","+ticket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file :"+user.getId());
        }
        raf.close();
        System.out.println("over");
    }

}
