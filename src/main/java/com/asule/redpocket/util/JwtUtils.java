package com.asule.redpocket.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/2/3 18:41
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
public class JwtUtils {

    private static final String Signature = "CZL_TOKEN_2000/04/28!@**,Gods determine what you are going to be";

    /**
     * 生成token header payload Signature
     */
    public static String getToken(Map<String,String> map){


        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE,7);  //默认7天过期

        //创建jwt builder
        JWTCreator.Builder builder = JWT.create();

        //payload
        map.forEach((k,v)->{
            builder.withClaim(k, v);
        });

        //指定令牌过期时间
        String token = builder.withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(Signature));


        return token;
    }

    /**
     * 验证token合法性
     */
    public static DecodedJWT verify(String token){
        //1.创建验证对象
       return JWT.require(Algorithm.HMAC256(Signature)).build().verify(token);
    }

}
