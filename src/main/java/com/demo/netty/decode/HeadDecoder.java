package com.demo.netty.decode;

import com.demo.netty.message.CommonHeader;
import com.demo.netty.message.Header;
import com.demo.netty.utils.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

/**
 * create by shen_xi on 2021/05/12
 */
public class HeadDecoder {
    public Header decode(ByteBuf buf) {
        byte[] source = new byte[8];
        buf.copy().readBytes(source);
        byte platform = buf.readByte();
        String device = ByteBufUtil.hexDump(buf);
        CommonHeader header = new CommonHeader();
        header.setSource(source);
        header.setPlatform(HexUtil.hexDump(platform));
        header.setDevice(device);
        return header;
    }

    public boolean isHead(ByteBuf buf) {
        if (buf.readableBytes() != 8) {
            return false;
        }
        return true;
    }
}
