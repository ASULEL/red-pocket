package com.asule.redpocket.controller;

import com.asule.redpocket.domain.CommonResult;
import com.asule.redpocket.domain.RedDetail;
import com.asule.redpocket.domain.RedGrab;
import com.asule.redpocket.domain.RedRecord;
import com.asule.redpocket.service.RedDetailService;
import com.asule.redpocket.service.RedGrabService;
import com.asule.redpocket.service.RedRecordService;
import com.asule.redpocket.util.JwtUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

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

    @Autowired
    private RedDetailService redDetailService;

    @Autowired
    private RedGrabService redGrabService;

    private final String prefix = "redPocket:";


    /**
     * 发红包
     *
     * @param redRecord
     * @return
     */
    @PostMapping("/red/pocket/send")
    public CommonResult sendRedPocket(RedRecord redRecord, HttpServletRequest request) {

        List<RedDetail> redDetailList;

        try {
            //1.判断数据合法性
            if (redRecord.getMoneyTotal() < 0 || redRecord.getAmount() < 0 ||
                    redRecord.getMoneyTotal() == null || redRecord.getAmount() == null) {
                throw new RuntimeException("红包金额或人数不能小于0");
            }

            //2.设置创建红包的时间和账户
            log.info("********************设置创建红包的时间和账户");
            String token = request.getHeader("token");
            DecodedJWT verify = JwtUtils.verify(token);
            String phone = verify.getClaim("phone").asString();
            log.info("********************phone: {}", phone);
            redRecord.setPhone(phone);
            redRecord.setCreateTime(new Date());

            //3.雪花算法生成id
            //log.info("********************雪花算法生成id");
            //long id = new IdGeneratorSnowFlake().snowflakeId();
            //log.info("********************所得id:" + id);
            //String redPocketFlag = prefix + redRecord.getPhone() + ":" + id;
            //log.info("********************所得红包唯一标识:" + id);
            //redRecord.setRedPocket(redPocketFlag);

            //4.生成随机红包
            redDetailList = redRecordService.sendRedPocket(redRecord);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("***************发送红包失败");
            return new CommonResult(444, e.getMessage(), "发送红包失败");
        }
        log.info("***************发送红包成功");
        return new CommonResult(200,redDetailList ,"发送红包成功");
    }

    @PostMapping("/red/pocket/grab")
    public CommonResult grabRedPocket(Integer redPocketId,HttpServletRequest request) {
        CommonResult commonResult =new CommonResult();

        try {
            //1.判断当前用户是否已经抢过红包
            log.info("********************判断当前用户是否已经抢过红包");
            String token = request.getHeader("token");
            DecodedJWT verify = JwtUtils.verify(token);
            String phone = verify.getClaim("phone").asString();
            if (redGrabService.isExists(phone,redPocketId)){
                commonResult.setCode(406)
                        .setMessage("当前用户已经抢过红包");
            }

            //2.抢红包
            log.info("********************抢红包");
            RedGrab redGrab = redGrabService.grabRedPocket(phone,redPocketId);
            commonResult.setCode(200)
                    .setMessage("抢红包成功")
                    .setData(redGrab);
        } catch (Exception e) {
            e.printStackTrace();
            commonResult.setCode(406)
                    .setMessage("抢红包失败")
                    .setData(e.getMessage());
        }
        return commonResult;
    }
}
