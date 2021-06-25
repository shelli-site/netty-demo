package com.demo.netty.message;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * create by shen_xi on 2021/04/22
 */
public interface Message<T extends Header> {

    T getHeader();

    default Type getMessageType() {
        return getHeader().getType();
    }

    @AllArgsConstructor
    enum Type {
        登陆帧((byte) 0x03),
        登陆应答帧((byte) 0x04),
        心跳帧((byte) 0x06),
        心跳应答帧((byte) 0x07),
        数据帧((byte) 0xc1),
        数据应答帧((byte) 0xc2),
        参数设置((byte) 0xd1),
        参数设置应答((byte) 0xd2),
        参数查询((byte) 0xd7),
        参数查询应答((byte) 0xd8);

        private byte control;

        static Map<Byte, Type> allTypeMap = new HashMap<>();

        static {
            for (Type type : Type.values()) {
                allTypeMap.put(type.control, type);
            }
        }

        public static Type getType(byte control) {
            return allTypeMap.get(control);
        }
    }
}
