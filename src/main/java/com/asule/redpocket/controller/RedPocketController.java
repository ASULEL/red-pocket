package com.asule.redpocket.controller;

import cn.hutool.http.HttpStatus;
import com.asule.redpocket.domain.CommonResult;
import com.asule.redpocket.domain.RedDetail;
import com.asule.redpocket.domain.RedRecord;
import com.asule.redpocket.service.RedDetailService;
import com.asule.redpocket.service.RedGrabService;
import com.asule.redpocket.service.RedRecordService;
import com.asule.redpocket.util.RedEnvelopeUtils;
import com.asule.redpocket.util.ShortKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/1/31 9:47
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@RestController
@Slf4j
public class RedPocketController {

    @Autowired
    private RedRecordService redRecordService;

    @Resource
    private RedDetailService redDetailService;

    @Resource
    private RedGrabService redGrabService;

    @Resource
    private RedisTemplate redisTemplate;

    private final String prefix = "redPocket:";


    /**
     * 发红包
     *
     * @param redRecord
     * @return
     */
    @PostMapping("/red/pocket/send")
    public CommonResult sendRedPocket(RedRecord redRecord) {

        List<RedDetail> redDetailList;

        try {
            //1.判断数据合法性
            if (redRecord.getMoneyTotal() <= 0 || redRecord.getTotal() <= 0 ||
                    redRecord.getMoneyTotal() == null || redRecord.getTotal() == null) {
                throw new RuntimeException("红包金额或人数不能小于0");
            }

            //2.设置创建红包的时间和账户
            redRecord.setCreateTime(new Date());

            //3.生成随机红包
            //使用二倍均值算法生成随机金额红包
            List<Integer> list = RedEnvelopeUtils.divideRedPackage(redRecord.getMoneyTotal() * 100, redRecord.getTotal());

            //4.生成redPacket唯一标识
            UUID uuid = UUID.randomUUID();
            String encode = ShortKeyGenerator.encode(uuid);
            while (redisTemplate.hasKey(encode)) {
                uuid = UUID.randomUUID();
                encode = ShortKeyGenerator.encode(uuid);
            }
            String redId = new StringBuffer()
                    .append("redPocket:")
                    .append(encode).toString();

            log.info("****************list:" + list);
            redisTemplate.opsForList().leftPushAll(redId, list);
            //5.将红包的总数记录redis
            redisTemplate.opsForValue().set(redId + ":total", redRecord.getTotal());
            redRecord.setId(encode);
            redRecordService.sendRedPocket(redRecord, list);
            log.info("***************发送红包成功");
            return new CommonResult(HttpStatus.HTTP_OK, encode, "发送红包成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("***************发送红包失败");
            return new CommonResult(HttpStatus.HTTP_NOT_ACCEPTABLE, e.getMessage(), "发送红包失败");
        }
    }

    @PostMapping("/red/pocket/grab")
    public CommonResult grabRedPocket(String encode, String phone) {
        CommonResult commonResult = new CommonResult();
        Double money = null;
        try {
            log.info("********************抢红包");
            if ((money = (Double) redisTemplate.opsForValue().get(phone + ":" + encode + ":grab")) != null){
                return commonResult.setCode(HttpStatus.HTTP_OK)
                        .setMessage("已经抢过红包")
                        .setData(money);
            }
            BigDecimal result = redGrabService.grabRedPocket(phone, encode);
            if (Objects.isNull(result))
                return commonResult.setCode(HttpStatus.HTTP_NO_CONTENT)
                        .setMessage("红包被抢光了")
                        .setData(null);

            return commonResult.setCode(HttpStatus.HTTP_OK)
                    .setMessage("抢红包成功")
                    .setData(result);
        } catch (Exception e) {
            log.error("抢红包失败，redPocketId={}, error={}", encode, e.getMessage(), e);
            return commonResult.setCode(HttpStatus.HTTP_NOT_ACCEPTABLE)
                    .setMessage("抢红包失败")
                    .setData(e.getMessage());
        }
    }
}
