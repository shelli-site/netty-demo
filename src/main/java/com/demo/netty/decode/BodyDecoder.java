package com.demo.netty.decode;

import com.demo.netty.message.*;
import com.demo.netty.utils.HexUtil;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.demo.netty.utils.HexUtil.hexDump;
import static com.demo.netty.utils.HexUtil.verify;

/**
 * create by shen_xi on 2021/05/12
 */
@Slf4j
public class BodyDecoder {

    public Message decode(ByteBuf buf, Header cacheHeader) {
        CommonMessage message = new CommonMessage((CommonHeader) cacheHeader);
        byte[] source = new byte[buf.readableBytes()];
        buf.copy().readBytes(source);
        message.setSource(source);

        if (!verify(message)) {
            message.setVerified(false);
            log.warn("68{}68{}16", hexDump(message.getHeader().getSource()), hexDump(message.getSource()));
        } else {
            message.setVerified(true);
        }

        int length = HexUtil.bytesFlip(buf.readShort());
        message.setBodyLength(length);

        if (buf.readableBytes() != length + 1) { // 一个校验位
            log.warn("长度校验失败 {} {}", buf.readableBytes(), length);
        }

        byte control = (byte) HexUtil.bytesFlip(buf.readShort());
        message.getHeader().setType(Message.Type.getType(control));

        byte deviceCounts = buf.readByte();
        message.setDeviceCounts(deviceCounts & 0xff);

        List data = new ArrayList();

        while (true) {
            if (buf.readableBytes() > 2 && buf.readableBytes() < 6) {
                for (int i = 0; i < buf.readableBytes() - 2; i++) {
                    buf.readByte();
                }
                break;
            }
            if (buf.readableBytes() == 2) {
                break;
            }
            byte[] attr = new byte[4];
            buf.readBytes(attr, 0, 4);
            CommonAttribute.Attribute[] attributes = HexUtil.analyseAttr(attr);
            for (CommonAttribute.Attribute attribute : attributes) {
                int len = attribute.getType().len != -1 ? attribute.getType().len : attribute.getLen();
                byte[] value = new byte[len];
                buf.readBytes(value, 0, len);
                //
                data.add(value);
            }
        }
        byte serial = buf.readByte();
        message.getHeader().setSerialNo(0xff & serial);
        byte verify = buf.readByte();
        message.setVerify(verify);
        buf.release();
        return message;
    }

    /**
     * 68
     * ea
     * 03330000130000
     * 68
     * 0f00
     * c200
     * 01
     * 0081 -> 00 0000 0010 00 0001
     * 0001
     * 20210408150311
     * 65
     * 32
     * 16
     */
}
