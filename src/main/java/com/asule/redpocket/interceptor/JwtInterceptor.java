package com.asule.redpocket.interceptor;

import com.asule.redpocket.util.JwtUtils;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/2/7 11:24
 * @Version: 1.0
 * @Description: 拦截器....
 */
public class JwtInterceptor implements HandlerInterceptor {

    private HashMap<String, Object> map;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String,Object> map = new HashMap<>();
        //1.获取http请求头中的令牌
        String token = request.getHeader("token");
        try {
            JwtUtils.verify(token); //验证令牌
            return true;
        } catch (SignatureVerificationException e) {
            e.printStackTrace();
            map.put("msg","无效签名！");
        }catch (TokenExpiredException e){
            e.printStackTrace();
            map.put("msg","Token 过期~");
        }catch (AlgorithmMismatchException e){
            e.printStackTrace();
            map.put("msg","Token 算法不一致*");
        }catch (Exception e){
            e.printStackTrace();
            map.put("msg","没有传入token ┭┮﹏┭┮");
        }
        //设置状态
        map.put("state",false);
        //map以json格式response响应到前台
        String json = new ObjectMapper().writeValueAsString(map);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
        return false;
    }
}
