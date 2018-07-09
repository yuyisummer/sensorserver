package com.jit.sensor.base.socket;

import javax.websocket.Session;
import java.io.IOException;

public class MessageTransfer {
    private static Session session;

    public static void setSession(Session s) {
        session = s;
    }

    public static void sendMessage(String str) {
        if (session != null) {
            try {
                session.getBasicRemote().sendText(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("为空");
        }
    }
}
