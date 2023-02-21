package com.asule.redpocket.service;

import com.asule.redpocket.domain.RedGrab;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 12707
* @description 针对表【red_grab】的数据库操作Service
* @createDate 2023-01-31 15:54:33
*/
public interface RedGrabService extends IService<RedGrab> {

    RedGrab grabRedPocket(String phone, Integer redPocketId);

    boolean isExists(String phone,Integer redPocketId);
}
