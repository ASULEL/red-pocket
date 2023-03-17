package com.asule.redpocket;

import com.asule.redpocket.util.RedEnvelopeUtils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
@Slf4j
class RedPocketApplicationTests {

    @Test
    void redPocket(){
        //金额总数 以分为单位
        Integer amount = 1000;
        //红包个数
        Integer total = 10;

        List<Integer> lists = RedEnvelopeUtils.divideRedPackage(amount, total);
        log.info("总金额={}分，总个数={}个", amount, total);

        //生成金额之和
        Integer sum = 0;
        for (Integer i : lists) {
            log.info("随机金额为：{}分，即{}元", i, new BigDecimal(i).divide(new BigDecimal(100)));
            sum += i;
        }
        log.info("所有随机金额叠加之和={}分", sum);
    }

    @Test
    void test(){
        try {
            int i = 10 /0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("111");
    }

    /**
     * 获取令牌
     */
    @Test
    void jwt(){
        //头可以不做改动
        HashMap<String,Object> map = new HashMap<>();

        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND,100);

        String token = JWT.create()
                //.withHeader(map) //header可以不写
                .withClaim("userId", 21)
                .withClaim("username", "xiaochen") //payload
                .withExpiresAt(instance.getTime()) //指定令牌的过期时间
                .sign(Algorithm.HMAC256("!@#@#"));//签名,密钥可以随便写，不能给其他人

        System.out.println(token);

    }

    @Test
    public void jwtYz(){
        //1.创建验证对象
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("!@#@#")).build();
        //2.验证token
        DecodedJWT verify = jwtVerifier.verify
                ("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzU2OTU5MzYsInVzZXJJZCI6MjEsInVzZXJuYW1lIjoieGlhb2NoZW4ifQ.XDpvyXBTWxPhDZE7sKMtbyLdDwb8Htl1ahFltS-Y07Q");
        //3.获取信息
        System.out.println(verify.getClaim("userId").asInt());
        System.out.println(verify.getClaim("username").asString());
        System.out.println("过期时间" + verify.getExpiresAt());
    }

}
