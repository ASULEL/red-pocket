package com.asule.redpocket;

import com.asule.redpocket.mapper.RedRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/1/31 9:35
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@SpringBootTest
@Slf4j
public class MybatisPlusText {

    @Autowired
    private RedRecordMapper redRecordMapper;


    @Test
    void select(){
        log.info("*********************查询id为1的红包" + redRecordMapper.selectById(1));
    }
}
