package com.jit.sensor.global.socket;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/websocket")
@Component
public class MyWebsocket {
    private static Session session;

    public static void sendMessage(String message) throws IOException {
        System.out.println(session == null);
        System.out.println(session.getBasicRemote());
        session.getBasicRemote().sendText(message);

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        System.out.println("有一连接关闭");
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("连接成功");
        MyWebsocket.session = session;
        System.out.println("Myebsocket+21\t" + session == null);
        MessageTransfer.setSession(session);
    }


}