package com.demo.netty.message;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * create by shen_xi on 2021/05/12
 */
@Data
public class CommonMessage implements Message<CommonHeader> {
    CommonHeader header;
    private int bodyLength;
    private byte[] source;
    private int deviceCounts;
    private Map<String, CommonAttribute> attributes = new HashMap<>();

    private boolean isVerified;
    private byte verify;


    @Override
    public CommonHeader getHeader() {
        return header;
    }

    public CommonMessage(CommonHeader header) {
        this.header = header;
    }
}
