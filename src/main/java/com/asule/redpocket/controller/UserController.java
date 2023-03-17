package com.asule.redpocket.controller;

import com.asule.redpocket.domain.CommonResult;
import com.asule.redpocket.domain.User;
import com.asule.redpocket.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;


    @GetMapping("/user/login")
    public CommonResult login(User user, HttpServletRequest request) {
        log.info("user" + user);
        CommonResult commonResult = new CommonResult();

        try {
            //1.验证前端传过来的值User
            if (user == null || user.getPhone() == null || user.getPassword() == null) {
                throw new RuntimeException("用户名或密码不能为空");
            }
            log.info("************************控制层面**********************");
            log.info("************************手机号:[{}]", user.getPhone());
            log.info("************************密码:[{}]", user.getPassword());

            //2.尝试登陆
            User userDB = userService.login(user);

            //3.登陆成功，生成token存储到客户端中
            String token = "user:token:" + UUID.randomUUID().toString()
                    .replace("-", "");
            redisTemplate.opsForValue().set(token, userDB.getPhone(),30, TimeUnit.MINUTES);
            request.setAttribute("token",token);

            //4.客户端在后续的请求中将 token 作为请求头发送给服务器端，服务器端根据 token 在 Redis 中查找对应的用户信息。
            commonResult.setCode(200)
                    .setMessage("登陆成功")
                    .setData(token);
        } catch (Exception e) {
            commonResult.setCode(400)
                    .setMessage("登陆失败")
                    .setData(e.getMessage());
        }
        return commonResult;
    }
}
