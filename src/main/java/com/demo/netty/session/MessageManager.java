package com.demo.netty.session;

import com.demo.netty.message.Header;
import com.demo.netty.message.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * create by shen_xi on 2021/04/22
 */
@Slf4j
public class MessageManager {

    private Map<String, SynchronousQueue> topicSubscribers = new ConcurrentHashMap<>();

    private SessionManager sessionManager;

    public MessageManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    /**
     * 发送通知类消息，不接收响应
     */
    public boolean notify(Message<? extends Header> message) {
        Header header = message.getHeader();
        String clientId = header.getClientId();
        Session session = sessionManager.get(clientId);
        if (session == null) {
            log.info("<<<<<<<<<<消息发送失败,未注册,{}", message);
            return false;
        }

        header.setSerialNo(session.nextSerialNo());
        session.writeObject(message);
        return true;
    }

    /**
     * 发送同步消息，接收响应
     * 默认超时时间20秒
     */
    public <T> T request(Message<? extends Header> request, Class<T> responseClass) {
        return request(request, responseClass, 20000);
    }

    public <T> T request(Message<? extends Header> request, Class<T> responseClass, long timeout) {
        Header header = request.getHeader();
        String clientId = header.getClientId();

        Session session = sessionManager.get(clientId);
        if (session == null) {
            log.info("<<<<<<<<<<消息发送失败,未注册,{}", request);
            throw new RuntimeException("NO_CONNECTION");
        }

        header.setSerialNo(session.nextSerialNo());

        String key = requestKey(header, responseClass);
        SynchronousQueue syncQueue = this.subscribe(key);
        if (syncQueue == null) {
            log.info("<<<<<<<<<<请勿重复发送,{}", request);
        }

        try {
            session.writeObject(request);
            return (T) syncQueue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.warn("<<<<<<<<<<等待响应超时" + session, e);
        } finally {
            this.unsubscribe(key);
        }
        return null;
    }

    /**
     * 消息响应
     */
    public boolean response(Message message) {
        SynchronousQueue queue = topicSubscribers.get(responseKey(message));
        if (queue != null) {
            return queue.offer(message);
        }
        return false;
    }

    private SynchronousQueue subscribe(String key) {
        SynchronousQueue queue = null;
        if (!topicSubscribers.containsKey(key)) {
            topicSubscribers.put(key, queue = new SynchronousQueue());
        }
        return queue;
    }

    private void unsubscribe(String key) {
        topicSubscribers.remove(key);
    }

    private static String requestKey(Header header, Class responseClass) {
        StringBuilder key = new StringBuilder();
        key.append(header.getClientId()).append('/').append(responseClass.getName());

        if (Response.class.isAssignableFrom(responseClass)) {
            key.append('/').append(header.getSerialNo());
        }
        return key.toString();
    }

    private static String responseKey(Message response) {
        Class<? extends Message> responseClass = response.getClass();
        Header header = response.getHeader();

        StringBuilder key = new StringBuilder();
        key.append(header.getClientId()).append('/').append(responseClass.getName());

        if (response instanceof Response) {
            key.append('/').append(((Response) response).getSerialNo());
        }
        return key.toString();
    }

    public interface Response {
        /**
         * 应答消息流水号
         */
        int getSerialNo();
    }
}
