package com.asule.redpocket.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/1/31 9:48
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true )
public class CommonResult<T> {

    private Integer code;
    private T data;
    private String message;

    public CommonResult(Integer code, String message) {
        this(code, null, message);
    }
}
