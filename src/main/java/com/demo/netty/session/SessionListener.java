package com.demo.netty.session;

/**
 * create by shen_xi on 2021/04/22
 */
public interface SessionListener {

    void sessionCreated(Session session);

    void sessionDestroyed(Session session);
}
