package com.demo.netty.utils;

import com.demo.netty.message.CommonAttribute;
import com.demo.netty.message.CommonMessage;
import io.netty.buffer.ByteBufUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * create by shen_xi on 2021/05/11
 */
@Slf4j
public class HexUtil {

    static char[] hex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static byte[] dumpHex(String hex) {
        int len;
        int num;
        if (hex.length() >= 2) {
            len = (hex.length() / 2);
            num = (hex.length() % 2);
            if (num == 1) {
                hex = "0" + hex;
                len = len + 1;
            }
        } else {
            hex = "0" + hex;
            len = 1;
        }
        byte[] result = new byte[len];
        char[] chars = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(chars[pos]) << 4 | toByte(chars[pos + 1]));
        }
        return result;
    }

    public static String hexDump(byte[] bytes) {
        return ByteBufUtil.hexDump(bytes);
    }

    public static String hexDump(byte b) {
        return "" + hex[(b & 0xff) >> 4] + hex[(b & 0x0f)];
    }

    private static int toByte(char c) {
        if (c >= 'a') {
            return (c - 'a' + 10) & 0x0f;
        }
        if (c >= 'A') {
            return (c - 'A' + 10) & 0x0f;
        }
        return (c - '0') & 0x0f;
    }

    public static int bytesFlip(short v) {
        int low = (v & 0xff00) >> 8;
        int high = (v & 0xff) << 8;
        return low | high;
    }

    public static boolean checkLength(CommonMessage message) {
        return message.getBodyLength() + 3 == message.getSource().length;
    }

    public static boolean verify(CommonMessage message) {
        long sum = 0L;
        sum += 0x68;
        for (byte b : message.getHeader().getSource()) {
            sum += (0xff) & b;
        }
        final byte[] source = message.getSource();
        byte verify = source[source.length - 1];
        sum += 0x68;
        for (int i = 0; i < source.length - 1; i++) {
            sum += (0xff) & source[i];
        }
        return (sum & 0xff) == (verify & 0xff);
    }

    public static CommonAttribute.Attribute[] analyseAttr(byte[] bytes) {
        // 0081 -> 0000 0000 1000 0001
        // __00 0000 0010
        // 设备编码
        int eqNumber = ((bytes[0] & 0xff) << 2) + ((bytes[1] & 0xff) >> 6);
        StringBuffer eqSbNum = new StringBuffer();
        // __00 0001
        // b1 设备序号
        byte eqSerial = (byte) (bytes[1] & 0x3f);
        // b2 设备参数组
        String attrGroup = hexDump(bytes[2]);
        // b3 设备参数位
        byte attrPos = bytes[3];

        eqSbNum.append(hex[eqNumber >> 8])
                .append(hex[(eqNumber & 0xf0) >> 4])
                .append(hex[eqNumber & 0x0f]);
        List<CommonAttribute.Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if (((attrPos >> i) & 0x01) == 1) {
                String attr = eqSbNum + attrGroup + hexDump((byte) (0x01 << i));
                log.debug(attr);
                // TODO 处理参数 转化信息
                attributes.add(new CommonAttribute.Attribute());
            }
        }
        CommonAttribute.Attribute[] result = new CommonAttribute.Attribute[attributes.size()];
        return attributes.toArray(result);
    }
}
