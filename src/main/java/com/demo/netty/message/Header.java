package com.demo.netty.message;

import java.io.Serializable;

/**
 * create by shen_xi on 2021/04/22
 */
public interface Header extends Serializable {

    /**
     * 客户端唯一标识
     */
    String getClientId();

    /**
     * 消息类型
     */
    Message.Type getType();

    /**
     * 消息流水号
     */
    int getSerialNo();

    default void setSerialNo(int serialNo) {

    }
}
