package com.asule.redpocket.service.impl;

import com.asule.redpocket.domain.RedGrab;
import com.asule.redpocket.mapper.RedDetailMapper;
import com.asule.redpocket.mapper.RedGrabMapper;
import com.asule.redpocket.mapper.RedRecordMapper;
import com.asule.redpocket.service.RedGrabService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author 12707
 * @description 针对表【red_grab】的数据库操作Service实现
 * @createDate 2023-01-31 15:54:33
 */
@Service
@Slf4j
public class RedGrabServiceImpl extends ServiceImpl<RedGrabMapper, RedGrab>
        implements RedGrabService {

    @Autowired
    private RedRecordMapper redRecordMapper;

    @Resource
    private RedDetailMapper redDetailMapper;

    @Resource
    private RedGrabMapper redGrabMapper;

    @Resource
    private RedisTemplate redisTemplate;

    private final String prefix = "redPocket:";

    /**
     * 抢红包核心业务，需要在缓存中查找红包并且进行记录，青岛红包后需要调用redDetailMapper写入数据库
     *
     * @param phone
     * @param encode
     * @return
     */
    @Override
    public BigDecimal grabRedPocket(String phone, String encode) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //1.判断当前用户是否已经抢过红包，若抢过直接返回金额
        Object obj = valueOperations.get(phone + ":" + encode + ":grab");
        if (obj != null)
            return new BigDecimal(obj.toString());
        //2.判断缓存中是否有剩余红包
        boolean flag = isExists(encode);
        //3.有红包就进入拆红包业务逻辑
        if (flag) {
            //setIfAbsent方法可以在键不存在时设置键值，如果键已经存在，方法会返回false，表示获取锁失败，否则返回true，表示获取锁成功。
            final String lockKey = phone + ":" + encode + "-lock";
            boolean lock = valueOperations.setIfAbsent(lockKey, encode);
            redisTemplate.expire(lockKey, 24L, TimeUnit.HOURS);

            try {
                if (lock) {
                    //3.1 从小红包列表中拆一个红包
                    Integer value = (Integer) redisTemplate.opsForList().rightPop(prefix + encode);
                    if (value != null) {
                        String pocketTotal = prefix + encode + ":total";
                        Integer currentTotal = valueOperations.get(pocketTotal) != null ? (Integer) valueOperations.get(pocketTotal) : 0;
                        valueOperations.increment(pocketTotal, -1);
                        BigDecimal grabMoney = new BigDecimal(value).divide(new BigDecimal(100));
                        //抢到红包的信息记录进入数据库
                        recordRobedPacket(phone, encode, new BigDecimal(value.toString()));
                        //当前用户抢过红包了，使用key进行标识，设置过期时间为一天
                        valueOperations.set(phone + ":" + encode + ":grab", grabMoney, 24L, TimeUnit.HOURS);
                        log.info("当前用户抢到红包了：phone={} redId={} 金额={}", phone, encode, grabMoney);
                        return grabMoney;
                    }
                }
            } catch (Exception e) {
                log.error("加分布式锁失败");
                e.printStackTrace();
            }
        }
        //当前用户没有抢到红包
        return null;
    }

    @Override
    @Async
    public void recordRobedPacket(String phone, String encode, BigDecimal amount) {
        RedGrab redGrab = new RedGrab();
        redGrab.setPhone(phone);
        redGrab.setRecordId(encode);
        redGrab.setAmount(amount.divide(new BigDecimal(100)));
        redGrab.setCreateTime(new Date());
        //插入数据库
        redGrabMapper.insert(redGrab);
    }

    /**
     * 判断缓存中是否还有红包
     *
     * @param encode
     * @return
     */
    private boolean isExists(String encode) {

        Integer amount = (Integer) redisTemplate.opsForValue().get(prefix + encode + ":total");
        if (amount != null && amount > 0)
            return true;
        return false;
    }

}




