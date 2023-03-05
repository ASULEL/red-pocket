package com.asule.redpocket.service.impl;

import com.asule.redpocket.domain.CommonResult;
import com.asule.redpocket.domain.RedDetail;
import com.asule.redpocket.domain.RedGrab;
import com.asule.redpocket.domain.RedRecord;
import com.asule.redpocket.mapper.RedDetailMapper;
import com.asule.redpocket.mapper.RedGrabMapper;
import com.asule.redpocket.mapper.RedRecordMapper;
import com.asule.redpocket.service.RedGrabService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    @Autowired
    private RedDetailMapper redDetailMapper;

    @Autowired
    private RedGrabMapper redGrabMapper;

    private Lock lock = new ReentrantLock();

    @Override
    public RedGrab grabRedPocket(String phone, Integer redPocketId) {
        lock.lock();
        CommonResult commonResult = new CommonResult();

        //1.获取数据库中随机获取redId的一条记录
        log.info("********************抢红包业务层");
        log.info("********************获取数据库中随机获取redPocketId:{}的一条记录", redPocketId);
        QueryWrapper<RedDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("record_id", redPocketId)
                .eq("is_active", 1)
                .last("ORDER BY RAND() LIMIT 1");
        RedDetail redDetail = redDetailMapper.selectOne(queryWrapper);
        log.info("随机生成redDetail：" + redDetail);
        if (redDetail == null) {
            throw new RuntimeException("没有该红包~~~");
        }

        try {
            //2.随机选取一个集合中的数据进行抢（将改条RedDetail中的isActive改为0）
            redDetail.setIsActive(0);
            UpdateWrapper<RedDetail> wrapper = new UpdateWrapper<>();
            wrapper.lambda()
                    .set(RedDetail::getIsActive,0)
                    .eq(RedDetail::getId,redDetail.getId());
            redDetailMapper.update(null,wrapper);

            //3.抢红包成功整体红包数减1
            RedRecord redRecord = redRecordMapper.selectById(redPocketId);
            UpdateWrapper<RedRecord> wrapper1 = new UpdateWrapper<>();
            wrapper1.lambda()
                    .set(RedRecord::getTotal,redRecord.getTotal() - 1)
                    .eq(RedRecord::getId,redPocketId);
            redRecordMapper.update(null,wrapper1);


            //4.抢红包成功记录添加入数据库
            RedGrab redGrab = new RedGrab();
            redGrab.setPhone(phone)
                    .setAmount(redDetail.getAmount())
                    .setRecordId(redPocketId)
                    .setCreateTime(new Date());
            redGrabMapper.insert(redGrab);
            return redGrab;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        throw new RuntimeException("抢红包异常  ┭┮﹏┭┮");
    }

    @Override
    public boolean isExists(String phone, Integer redPocketId) {
        //1.用户是否已经获取过红包
        log.info("********************红包详情业务层");
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("phone", phone);
        queryWrapper.eq("record_id", redPocketId);
        return redGrabMapper.selectOne(queryWrapper) != null;
    }
}




