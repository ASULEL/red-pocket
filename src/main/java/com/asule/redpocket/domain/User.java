package com.asule.redpocket.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Integer id;

    /**
     * 用户账号（手机号）
     */
    private String phone;

    /**
     * 用户密码
     */
    private String password;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}