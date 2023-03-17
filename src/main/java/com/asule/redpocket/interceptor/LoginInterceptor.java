package com.asule.redpocket.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/2/7 11:24
 * @Version: 1.0
 * @Description: 拦截器....
 * <p>
 * 拦截器是最先执行的，还未初始化bean，所以要在拦截器执行前将注入bean
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate redisTemplate;

    private final String prefix = "user:token:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //UTF-8
        response.setCharacterEncoding("UTF-8");
        //响应json格式数据
        response.setContentType("application/json");

        //从Header中获取token
        String token = prefix + request.getHeader("token");
        if (StrUtil.isEmpty(token)) {
            response.getWriter().write(this.errorResponse("未登录"));
            return false;
        }

        //从redis中通过token查询phone
        try {
            String phone = (String) redisTemplate.opsForValue().get(token);
            if (StrUtil.isEmpty(phone)) {
                response.getWriter().write(this.errorResponse("已经过期，请重新登陆"));
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.getWriter().write(this.errorResponse("发生异常"));
            return false;
        }
        return true;
    }

    /**
     * 私有方法，返回json字符串格式的错误信息
     */
    private String errorResponse(String msg) {
        Map<String, Object> map = new HashMap<String, Object>(2) {
            {
                put("code", 400);
                put("msg", msg);
            }
        };
        return JSONObject.toJSONString(map);
    }
}
