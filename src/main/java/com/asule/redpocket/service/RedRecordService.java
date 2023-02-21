package com.asule.redpocket.service;

import com.asule.redpocket.domain.RedDetail;
import com.asule.redpocket.domain.RedRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 12707
* @description 针对表【red_record】的数据库操作Service
* @createDate 2023-01-31 09:19:45
*/
public interface RedRecordService extends IService<RedRecord> {

    List<RedDetail> sendRedPocket(RedRecord redRecord);
}
