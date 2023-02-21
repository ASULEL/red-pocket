package com.asule.redpocket.service.impl;

import com.asule.redpocket.domain.User;
import com.asule.redpocket.mapper.UserMapper;
import com.asule.redpocket.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
* @author 12707
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-02-06 22:17:35
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public User login(User user) {
        log.info("************************服务层面**********************");

        //1.判断用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone",user.getPhone());
        queryWrapper.eq("password",user.getPassword());
        User userDB = userMapper.selectOne(queryWrapper);
        log.info("************************数据库查询到的user:[{}]",userDB);


        //2.存在则返回用户,不存在则抛出异常
        if (userDB == null){
            throw new RuntimeException("用户名或密码错误~ ┭┮﹏┭┮");
        }
        return userDB;
    }
}




