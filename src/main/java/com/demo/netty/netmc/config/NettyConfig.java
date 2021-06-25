package com.demo.netty.netmc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * create by shen_xi on 2021/04/20
 */
@Component
@ConfigurationProperties(prefix = "netty.equipment")
public class NettyConfig {
    public Boolean enable;
    /**
     * 服务端口
     */
    public int port;
    /**
     * 服务器心跳检测时间
     */
    public int idle;
}
