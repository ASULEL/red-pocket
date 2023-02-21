package com.asule.redpocket.service.impl;

import com.asule.redpocket.domain.CommonResult;
import com.asule.redpocket.domain.RedDetail;
import com.asule.redpocket.mapper.RedDetailMapper;
import com.asule.redpocket.mapper.RedRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.asule.redpocket.domain.RedGrab;
import com.asule.redpocket.service.RedGrabService;
import com.asule.redpocket.mapper.RedGrabMapper;
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
    implements RedGrabService{

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
        log.info("********************获取数据库中随机获取redPocketId:{}的一条记录",redPocketId);
        RedDetail redDetail = redGrabMapper.randRedDetail(redPocketId);
        log.info("随机生成redDetail：" + redDetail);
        if (redDetail == null){
            throw new RuntimeException("没有该红包~~~");
        }

        try {
            //2.随机选取一个集合中的数据进行抢（将改条RedDetail中的isActive改为0）
            redDetail.setIsActive(0);
            QueryWrapper<RedDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id",redDetail.getId());
            redDetailMapper.update(redDetail,queryWrapper);

            //3.抢红包成功记录添加入数据库
            RedGrab redGrab = new RedGrab();
            redGrab.setPhone(phone)
            	.setAmount(redDetail.getAmount())
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
    public boolean isExists(String phone,Integer redPocketId) {
        //1.用户是否已经获取过红包
        log.info("********************红包详情业务层");
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("phone",phone);
        queryWrapper.eq("red_pocket",redPocketId);
        return redGrabMapper.selectOne(queryWrapper) != null ? true : false;
    }
}




