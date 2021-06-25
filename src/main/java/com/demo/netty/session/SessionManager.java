package com.demo.netty.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * create by shen_xi on 2021/04/22
 */
public class SessionManager<S, T> {

    private Map<String, Session<S, T>> sessionMap;

    private ChannelFutureListener remover;

    private SessionListener sessionListener;

    public SessionManager() {
        this.sessionMap = new ConcurrentHashMap<>();
        this.remover = future -> {
            Session session = future.channel().attr(Session.KEY).get();
            if (session != null) {
                sessionMap.remove(session.getClientId(), session);
            }
        };
    }

    public SessionManager(SessionListener sessionListener) {
        this();
        this.sessionListener = sessionListener;
    }

    public Session<S, T> newSession(Channel channel) {
        Session session = new Session(channel, this);
        callSessionCreatedListener(session);
        return session;
    }

    protected void callSessionCreatedListener(Session<S, T> session) {
        if (sessionListener != null) {
            sessionListener.sessionCreated(session);
        }
    }

    protected void callSessionDestroyedListener(Session<S, T> session) {
        if (sessionListener != null) {
            sessionListener.sessionDestroyed(session);
        }
    }

    public Session<S, T> get(String clientId) {
        return sessionMap.get(clientId);
    }


    public Collection<Session<S, T>> all() {
        return sessionMap.values();
    }

    protected void put(String clientId, Session<S, T> newSession) {
        Session<S, T> oldSession = sessionMap.put(clientId, newSession);
        if (!newSession.equals(oldSession)) {
            newSession.channel.closeFuture().addListener(remover);
        }
    }

}
