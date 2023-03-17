package com.asule.redpocket;

import com.asule.redpocket.domain.CommonResult;
import com.asule.redpocket.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/3/4 14:27
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class RedPocketText {

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/redpocket";
    static final String USER = "root";
    static final String PASSWORD = "123456";


    @Test
    public void addUser() {
        Connection conn = null;
        PreparedStatement ps = null;
        FileWriter fw1 = null;
        try {
            // 1.注册JDBC驱动
            Class.forName(JDBC_DRIVER);

            // 2.打开连接
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // 3.关闭自动提交
            conn.setAutoCommit(false);

            // 4.构建SQL语句
            String sql = "INSERT INTO user(phone, password) VALUES(?, ?)";

            // 5.创建 PreparedStatement 对象
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // 6.设置参数
            File file = new File("C:\\Users\\12707\\Desktop\\redPocketUser.txt");
            fw1 = new FileWriter(file);


            for (int i = 0; i < 100; i++) {
                ps.setString(1, "user" + i);
                ps.setString(2, "123456");

                // 7.添加到批量操作
                //输出插入的文本文件
                fw1.write("user" + i + ",");
                fw1.write("123456\n");
                ps.addBatch();
            }

            // 8.执行批量操作
            ps.executeBatch();

            // 9.获取自增键
            ResultSet rs = ps.getGeneratedKeys();
            while (rs.next()) {
                System.out.println("插入成功，自增键值为：" + rs.getInt(1));
            }

            // 10.提交事务
            conn.commit();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 11.关闭连接
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (fw1 != null)
                    fw1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private RestTemplate restTemplate = new RestTemplate();

    //2.用户登陆
    private static final String URL = "http://localhost:8888/user/login?phone={phone}&password={password}";


    @Test
    public void BFSTest() {
        FileWriter fw1 = null;
        FileWriter fw2 = null;
        try {

            File file = new File("C:\\Users\\12707\\Desktop\\毕设\\redPocketUser.txt");
            Scanner scanner = new Scanner(file);
            List<String> strList = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                strList.add(line);
            }
            String[] strArray = strList.stream().toArray(String[]::new);
            File file1 = new File("C:\\Users\\12707\\Desktop\\毕设\\result.txt");
            fw1 = new FileWriter(file1);
            File file2 = new File("C:\\Users\\12707\\Desktop\\毕设\\grabThread.txt");
            fw2 = new FileWriter(file2);
            //1.读取用户信息
            for (int i = 0; i < 100; i++) {
                String[] splitStr = strArray[i].split(",");
                User user = new User();
                user.setPhone(splitStr[0]);
                user.setPassword(splitStr[1]);
                ResponseEntity<CommonResult> responseEntity = restTemplate.getForEntity(URL, CommonResult.class, user.getPhone(), user.getPassword());
                //2.获取返回结果
                String str = responseEntity.getBody().getData().toString();
                String token = str.split(":")[2].toString();
                log.info(user.getPhone() + "," + user.getPassword() + "," + token);
                //3.写入登陆过的用户信息
                fw1.write(user.getPhone() + ",");
                fw1.write(user.getPassword() + ",");
                fw1.write(token + "\n");
                //4.写入将要并发抢红包的线程
                fw2.write(user.getPhone() + ",");
                fw2.write("9Lvaoe,");
                fw2.write(token + "\n");
            }
            log.info("***********************登陆成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw1 != null)
                    fw1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fw2 != null)
                    fw2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
