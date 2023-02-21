package com.asule.redpocket.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/1/31 9:21
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@Component
@Slf4j
public class IdGeneratorSnowFlake {

    private long workerId = 0;
    private long datacenterId = 1;
    private Snowflake snowflake = IdUtil.getSnowflake(workerId, datacenterId);


    @PostConstruct
    public void init() {
        try {
            workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
            log.info("当前机器的workerId：{}", workerId);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("当前机器的workerId失败", workerId);
            workerId = NetUtil.getLocalhostStr().hashCode();
        }
    }

    public synchronized long snowflakeId(){
        return  snowflake.nextId();
    }

    public synchronized long snowflakeId(long workerId,long datacenterId){
        Snowflake snowflake = IdUtil.getSnowflake(workerId, datacenterId);
        return snowflake.nextId();
    }

}
