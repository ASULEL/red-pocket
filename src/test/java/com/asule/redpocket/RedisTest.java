package com.asule.redpocket;

import com.asule.redpocket.util.ShortKeyGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/3/6 16:52
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Async
    @Transactional(rollbackFor = Exception.class)
    public int redisTest(){
        Integer x = new Integer(3);
        ValueOperations value = redisTemplate.opsForValue();
        value.set("name","job");
        int a = x / 0;
        return a;
    }

    @Test
    public void test1(){
        UUID uuid = UUID.randomUUID();
        String redId = ShortKeyGenerator.encode(uuid);
        while (redisTemplate.hasKey(redId)){
            uuid = UUID.randomUUID();
            redId = ShortKeyGenerator.encode(uuid);
            System.out.println(uuid);
        }
    }

    @Test
    public void test2(){
        Integer value = (Integer) redisTemplate.opsForList().rightPop("redPocket:yYlAka");
        System.out.println(value);
    }

}
