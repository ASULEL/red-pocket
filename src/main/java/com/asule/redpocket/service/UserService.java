package com.asule.redpocket.service;

import com.asule.redpocket.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 12707
* @description 针对表【user】的数据库操作Service
* @createDate 2023-02-06 22:17:35
*/
public interface UserService extends IService<User> {

    /**
     * 用户登陆
     * @param user
     * @return
     */
    User login(User user);
}
