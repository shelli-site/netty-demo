package com.demo.netty.netmc;

import com.demo.netty.netmc.adapter.DataAdapter;
import com.demo.netty.netmc.adapter.DecodeAdapter;
import com.demo.netty.netmc.adapter.EncodeAdapter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * create by shen_xi on 2021/04/20
 */
@Slf4j
@Configuration
@ConditionalOnProperty(value = "netty.equipment.enable", havingValue = "true")
public class NettyServer {

    @Value("${netty.equipment.port}")
    int PORT;
    @Value("${netty.equipment.idle}")
    int IDLE;

    private volatile boolean isRunning = false;

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();


    @EventListener
    public void startNettyServer(ContextRefreshedEvent event) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {

            @Override
            public void initChannel(NioSocketChannel channel) {
                channel.pipeline()
                        .addLast(new IdleStateHandler(IDLE, 0, 0, TimeUnit.SECONDS))
                        .addLast("frameDecoder", frameDecoder())
                        .addLast("decoder", new DecodeAdapter())
                        .addLast("encoder", new EncodeAdapter())
                        .addLast("adapter", new DataAdapter());
//                        .addLast(new DataAdapter());
            }
        });

        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.bind(PORT).sync();
        } catch (InterruptedException e) {
            log.error("", e);
            System.exit(0);
        }
        isRunning = channelFuture.isSuccess();
        if (isRunning) {
            log.warn("==={}启动成功, port={}===", "netty", PORT);
        }
    }

    @PreDestroy
    public void stopNettyServer() {
        bossGroup.shutdownGracefully().syncUninterruptibly();
        workerGroup.shutdownGracefully().syncUninterruptibly();
        isRunning = false;
        log.info("关闭 Netty 成功");
    }

    /**
     * 帧处理器
     *
     * @return
     */
    public ByteToMessageDecoder frameDecoder() {
        ByteBuf delimiter0 = Unpooled.copiedBuffer(new byte[]{0x68});
        ByteBuf delimiter1 = Unpooled.copiedBuffer(new byte[]{0x16});
        return new DelimiterBasedFrameDecoder(4096, true, delimiter0, delimiter1);
    }
}
