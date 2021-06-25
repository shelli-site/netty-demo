package com.demo.netty.netmc.adapter;

import com.demo.netty.decode.BodyDecoder;
import com.demo.netty.decode.HeadDecoder;
import com.demo.netty.message.Header;
import com.demo.netty.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * create by shen_xi on 2021/04/20
 * TODO 解码适配器
 */
@Slf4j
public class DecodeAdapter extends ChannelHandlerAdapter {

    static Map<Channel, Header> headCache = new ConcurrentHashMap<>();
    static Map<Channel, byte[]> subPackCache = new ConcurrentHashMap<>();

    private HeadDecoder headDecoder = new HeadDecoder();
    private BodyDecoder bodyDecoder = new BodyDecoder();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        final Channel channel = ctx.channel();
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            if (buf.readableBytes() <= 0) {
                return;
            }
            boolean isHead = isHead(buf);
            if (isHead) {
                headCache.put(channel, decodeHeader(buf));
                return;
            }
            Message message = decodeBody(buf, headCache.get(channel));
            buf.release();
            ctx.fireChannelRead(message);
        } else {
            log.error("{}", msg);
        }
    }

    private Message decodeBody(ByteBuf buf, Header cacheHeader) {
        return bodyDecoder.decode(buf, cacheHeader);
    }

    private Header decodeHeader(ByteBuf buf) {
        return headDecoder.decode(buf);
    }

    private boolean isHead(ByteBuf buf) {
        return headDecoder.isHead(buf);
    }
}
