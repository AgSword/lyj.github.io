package com.example.seckill.utils;

import com.example.seckill.bean.User;
import com.example.seckill.vo.RespBean;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class UserAddUtil {

    public  void addUser(int count) throws Exception {
        ArrayList<User> users = new ArrayList<>(count);
        //生成user
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
        //将user加入到数据库中
//         Connection conn = getConn();
//         String sql = "insert into t_user(login_count, nickname, register_date, salt, password, id)values(?,?,?,?,?,?)";
//         PreparedStatement pstmt = conn.prepareStatement(sql);
//         for (int i = 0; i < users.size(); i++) {
//         	User user = users.get(i);
//         	pstmt.setInt(1, user.getLoginCount());
//         	pstmt.setString(2, user.getNickname());
//         	pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
//         	pstmt.setString(4, user.getSalt());
//         	pstmt.setString(5, user.getPassword());
//         	pstmt.setLong(6, user.getId());
//         	pstmt.addBatch();
//         }
//         pstmt.executeBatch();
//         pstmt.close();
//         conn.close();
//         System.out.println("insert to db");
        //登录，生成UserTicket
        String urlString="http://localhost:8080/login/toLogin";
        File file=new File("C:\\Users\\li140\\Desktop\\config.txt");
        if(file.exists()){
            file.delete();
        }
        RandomAccessFile raf=new RandomAccessFile(file,"rw");
        raf.seek(0);
        for (int i=0;i<users.size();i++){
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection connection  = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            String params= "mobile="+user.getId()+"&password="+MD5Util.inputPassToFromPass("123456");
            outputStream.write(params.getBytes());
            outputStream.flush();
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len=0;
            while ((len=inputStream.read(buff))>=0){
                bout.write(buff,0,len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = (String) respBean.getObj();
            String row= user.getId()+","+userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());

        }
        raf.close();

//        Connection connection=getconn();
    }

    public static void main(String[] args) throws Exception {
        UserAddUtil userAddUtil = new UserAddUtil();
        userAddUtil.addUser(5000);
    }

    private static Connection getConn() throws ClassNotFoundException, SQLException {
        String url="jdbc:mysql://localhost/seckill?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=true";
        String user="root";
        String password="liyinjian320623,";
        String driver="com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url,user,password);
    }
}
