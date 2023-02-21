package com.asule.redpocket.controller;

import com.asule.redpocket.domain.CommonResult;
import com.asule.redpocket.domain.User;
import com.asule.redpocket.service.UserService;
import com.asule.redpocket.util.JwtUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/2/6 22:17
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/login")
    public CommonResult login(User user) {

        CommonResult commonResult = new CommonResult();

        try {
            //1.验证前端传过来的值User
            if (user == null || user.getPhone() == null || user.getPassword() == null){
                throw new RuntimeException("用户名或密码不能为空");
            }
            log.info("************************控制层面**********************");
            log.info("************************手机号:[{}]", user.getPhone());
            log.info("************************密码:[{}]", user.getPassword());
            Map<String, Object> map = new HashMap<>();

            //2.尝试登陆
            User userDB = userService.login(user);
            Map<String, String> payload = new HashMap<>();
            payload.put("phone", userDB.getPhone());
            //3.生成jwt令牌
            String token = JwtUtils.getToken(payload);
            map.put("token", token);
            commonResult.setCode(200)
                    .setMessage("登陆成功")
                    .setData(map);
        } catch (Exception e) {
            commonResult.setCode(400)
                    .setMessage("登陆失败")
                    .setData(e.getMessage());
        }
        return commonResult;
    }

    /**
     * 如果每个方法都这么去写，代码冗余度太高了
     * 解决方法
     *     web项目可以用拦截器
     *     分布式项目可以用网关
     * @param
     * @return
     */
    @PostMapping("/user/test")
    public CommonResult<Map> test(HttpServletRequest request) {
        //处理业务逻辑
        Map<String,Object> map = new HashMap<>();
        String token = request.getHeader("token");
        DecodedJWT verify = JwtUtils.verify(token);
        log.info("用户id：{}" , verify.getClaim("phone").asString());
        map.put("status",true);
        CommonResult<Map> commonResult = new CommonResult<>(200,map,"测试成功");
        return commonResult;
    }
}
