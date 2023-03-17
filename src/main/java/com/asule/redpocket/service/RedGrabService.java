package com.asule.redpocket.service;

import com.asule.redpocket.domain.RedGrab;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
* @author 12707
* @description 针对表【red_grab】的数据库操作Service
* @createDate 2023-01-31 15:54:33
*/
public interface RedGrabService extends IService<RedGrab> {

    BigDecimal grabRedPocket(String phone, String encode);

    void recordRobedPacket(String phone, String encode, BigDecimal amount);
}
