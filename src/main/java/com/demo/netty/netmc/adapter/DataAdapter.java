package com.demo.netty.netmc.adapter;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * create by shen_xi on 2021/04/20
 * TODO 数据适配器
 */
@Slf4j
public class DataAdapter extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.debug("{}", msg);
        ctx.flush();
    }
}
