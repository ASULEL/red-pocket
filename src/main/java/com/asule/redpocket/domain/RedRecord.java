package com.asule.redpocket.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName red_record
 */
@TableName(value ="red_record")
@Data
public class RedRecord implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户账号
     */
    private String phone;

    /**
     * 红包全局唯一标识串
     */
    private String redPocket;

    /**
     * 总金额（单位为分）
     */
    private Integer moneyTotal;

    /**
     * 红包总数
     */
    private Integer total;

    /**
     * 创建时间
     */
    private Date createTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}