package com.demo.netty.message;

import lombok.Data;

/**
 * create by shen_xi on 2021/05/12
 */
@Data
public class CommonHeader implements Header {

    /**
     * 设备类型
     */
    private String platform;
    /**
     * 设备编码(前4位为厂商代码)
     */
    private String device;
    private Message.Type type;
    private byte[] source;
    private int serialNo;

    @Override
    public String getClientId() {
        return device;
    }

    @Override
    public Message.Type getType() {
        return type;
    }

    @Override
    public int getSerialNo() {
        return serialNo;
    }

}
