package com.asule.redpocket.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.UUID;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/3/8 15:04
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@Slf4j
public class ShortKeyGenerator {

    public static String encode(UUID uuid) {
        byte[] bytes = new byte[16];
        //获取UUID最高有效位
        long msb = uuid.getMostSignificantBits();
        //获取UUID最低有效位
        long lsb = uuid.getLeastSignificantBits();
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (msb >>> 8 * (7 - i));
            bytes[8 + i] = (byte) (lsb >>> 8 * (7 - i));
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0,6);
    }

    public static UUID decode(String str) {
        byte[] bytes = Base64.getUrlDecoder().decode(str);
        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (bytes[i] & 0xff);
            lsb = (lsb << 8) | (bytes[8 + i] & 0xff);
        }
        return new UUID(msb, lsb);
    }
}
