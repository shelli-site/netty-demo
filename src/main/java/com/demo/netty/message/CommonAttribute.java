package com.demo.netty.message;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * create by shen_xi on 2021/05/12
 */
@Data
public class CommonAttribute {
    private byte[] source;

    @Data
    public static class Attribute {
        private int len;
        private AttributeType type;
        private byte eqSerial;
    }

    @AllArgsConstructor
    public enum AttributeType {
        /**
         * 有符号8位
         */
        S8(Integer.class, 2),
        /**
         * 有符号16位
         */
        S16(Integer.class, 4),
        /**
         * 无符号8位
         */
        U8(Integer.class, 2),
        /**
         * 无符号16位
         */
        U16(Integer.class, 4),
        /**
         * 8421BCD码
         */
        BCD(String.class, -1),
        /**
         * ASCII码
         */
        ASCII(String.class, -1),
        /**
         * 自定义类型
         */
        CUSTOMIZE(Object.class, -1);

        public Class rT;
        public Integer len;
    }
}
