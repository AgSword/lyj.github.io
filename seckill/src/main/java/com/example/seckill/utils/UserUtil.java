package com.example.seckill.utils;



import com.example.seckill.bean.User;
import com.example.seckill.vo.RespBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 生成用户工具类
 * <p>
 * 乐字节：专注线上IT培训
 * 答疑老师微信：lezijie
 *
 * @author zhoubin
 * @since 1.0.0
 */
@Component
public class UserUtil {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    private void createUser(int count) throws Exception {
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
        System.out.println("create user");
        // // //插入数据库
//        Connection conn = getConn();
//        String sql = "insert into t_user(login_count, nickname, register_date, salt, password, id)values(?,?,?,?,?,?)";
//        PreparedStatement pstmt = conn.prepareStatement(sql);
//        for (int i = 0; i < users.size(); i++) {
//            User user = users.get(i);
//            pstmt.setInt(1, user.getLoginCount());
//            pstmt.setString(2, user.getNickname());
//            pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
//            pstmt.setString(4, user.getSalt());
//            pstmt.setString(5, user.getPassword());
//            pstmt.setLong(6, user.getId());
//            pstmt.addBatch();
//        }
//        pstmt.executeBatch();
//        pstmt.close();
//        conn.close();
//        System.out.println("insert to db");
        //登录，生成userTicket
        String urlString = "http://localhost:8080/login/doLogin";
        File file = new File("C:\\Users\\li140\\Desktop\\config.txt");
        if(file.exists()){
            file.delete();
        }
        RandomAccessFile raf= new RandomAccessFile(file,"rw");
        raf.seek(0);
        for (User user: users){
//            //http 登录url
//            URL url= new URL(urlString);
//            HttpURLConnection co = (HttpURLConnection)url.openConnection();
//            co.setRequestMethod("POST");
//            co.setDoOutput(true);
//
//            //http输出流
//            OutputStream out=co.getOutputStream();
//            String params="mobile="+user.getId()+"&password="+MD5Util.inputPassToFromPass("123456");
//            out.write(params.getBytes());
//            out.flush();
//            //http响应输入流 转为字节流
//            InputStream inputStream = co.getInputStream();
//            ByteArrayOutputStream bout = new ByteArrayOutputStream();
//            byte[] buff=new byte[1024];
//            int len=0;
//            while ((len=inputStream.read(buff))>=0){
//                bout.write(buff,0,len);
//            }
//            inputStream.close();
//            bout.close();
//            String response = new String(bout.toByteArray());
//
//            //响应字节流转 RespBean，获取obj中的userTicket
//            ObjectMapper mapper = new ObjectMapper();
//            System.out.println("response :"+response);
//            RespBean respBean=mapper.readValue(response,RespBean.class);
//            String userTicket = (String) respBean.getObj();
//            System.out.println("create userTicket :"+user.getId());
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

    private static Connection getConn() throws Exception {
        String url = "jdbc:mysql://localhost:3306/seckill?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "liyinjian320623,";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }


}

