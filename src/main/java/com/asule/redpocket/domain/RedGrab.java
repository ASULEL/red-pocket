package com.asule.redpocket.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName red_grab
 */
@TableName(value ="red_grab")
@Data
@Accessors(chain = true)
public class RedGrab implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户账号
     */
    private String phone;

    /**
     * 红包标识
     */
    private Integer recordId;

    /**
     * 红包金额（单位为元）
     */
    private BigDecimal amount;

    /**
     * 时间
     */
    private Date createTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}