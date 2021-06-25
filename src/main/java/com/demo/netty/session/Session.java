package com.demo.netty.session;

import com.demo.netty.message.Header;
import com.demo.netty.message.Message;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * create by shen_xi on 2021/04/22
 */
@Slf4j
@Getter
@Setter
public class Session<S, T> {
    public static int MAX_SERIAL = 0xff;
    public static final AttributeKey<Session> KEY = AttributeKey.newInstance(Session.class.getName());

    protected final Channel channel;

    private AtomicInteger serialNo = new AtomicInteger(0);
    private boolean registered = false;
    private String clientId;

    private final long creationTime;
    private volatile long lastAccessedTime;
    private Map<String, Object> attributes;
    private S subject;
    private T snapshot;

    private SessionManager sessionManager;

    protected Session(Channel channel, SessionManager sessionManager) {
        this.channel = channel;
        this.sessionManager = sessionManager;
        this.creationTime = System.currentTimeMillis();
        this.lastAccessedTime = creationTime;
        this.attributes = new TreeMap<>();
    }

    public int nextSerialNo() {
        int current;
        int next;
        do {
            current = serialNo.get();
            next = current > MAX_SERIAL ? 0 : current;
        } while (!serialNo.compareAndSet(current, next + 1));
        return next;
    }

    public void register(String clientId, S subject) {
        this.clientId = clientId;
        this.registered = true;
        this.subject = subject;
        sessionManager.put(clientId, this);
    }

    public long access() {
        lastAccessedTime = System.currentTimeMillis();
        return lastAccessedTime;
    }

    public Collection<String> getAttributeNames() {
        return attributes.keySet();
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public Object removeAttribute(String name) {
        return attributes.remove(name);
    }

    public void invalidate() {
        channel.close();
        sessionManager.callSessionDestroyedListener(this);
    }

    public int getId() {
        return channel.id().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Session that = (Session) o;
        return this.getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    public void writeObject(Message<? extends Header> message) {
        log.info("<<<<<<<<<<消息下发{},{}", this, message);
        channel.writeAndFlush(message);
    }
}
