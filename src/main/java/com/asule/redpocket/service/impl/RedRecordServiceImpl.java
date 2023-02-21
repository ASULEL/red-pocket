package com.asule.redpocket.service.impl;

import com.asule.redpocket.domain.RedDetail;
import com.asule.redpocket.mapper.RedDetailMapper;
import com.asule.redpocket.util.RedEnvelopeUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.asule.redpocket.domain.RedRecord;
import com.asule.redpocket.service.RedRecordService;
import com.asule.redpocket.mapper.RedRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 12707
 * @description 针对表【red_record】的数据库操作Service实现
 * @createDate 2023-01-31 09:19:45
 */
@Service
@Slf4j
public class RedRecordServiceImpl extends ServiceImpl<RedRecordMapper, RedRecord>
        implements RedRecordService {

    @Autowired
    private RedRecordMapper redRecordMapper;

    @Autowired
    private RedDetailMapper redDetailMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<RedDetail> sendRedPocket(RedRecord redRecord) {

        try {
            Integer moneyTotal = redRecord.getMoneyTotal();
            Integer amount = redRecord.getAmount();

            //1.红包整体记录添加入数据库
            log.info("*****************红包整体记录添加入数据库");
            redRecordMapper.insert(redRecord);

            //2.使用二倍均值算法生成随机金额红包
            log.info("*****************使用二倍均值算法生成随机金额红包");
            List<Integer> list = RedEnvelopeUtils.divideRedPackage(moneyTotal * 100, amount);
            log.info("*****************二倍均值算法生成的红包" + list);

            //3.将随机生成的红包添加入数据库当中
            log.info("*****************将随机生成的红包添加入数据库当中");
            List<RedDetail> redDetailList = new ArrayList<>();
            RedDetail redDetail = new RedDetail();
            for (int i = 0; i < list.size(); i++) {
                log.info("随机金额为：{}分，即{}元", list.get(i), new BigDecimal(list.get(i)).divide(new BigDecimal(100)));
                redDetail.setRecordId(redRecord.getId());
                redDetail.setAmount(new BigDecimal(list.get(i)).divide(new BigDecimal(100)));
                redDetail.setIsActive(1);
                redDetail.setCreateTime(new Date());
                redDetailList.add(redDetail);
                redDetailMapper.insert(redDetail);
            }
            return redDetailList;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("*****************业务层发送红包失败");
        }
        throw new RuntimeException("业务层发送红包异常");
    }
}




