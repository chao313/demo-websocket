package demo.spring.boot.demospringboot.config.wesocket.spring;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;


@Service
public class MyHandler implements WebSocketHandler {

    private WebSocketHandlerDecorator webSocketHandlerDecorator;

    private Logger log = LoggerFactory.getLogger(getClass());
    //在线用户列表
    private static final Map<String, WebSocketSession> webSocketUsers;

    static {
        webSocketUsers = new HashMap<>();
    }

    //新增socket
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String ID = session.getUri().toString().split("uid=")[1];
        if (ID != null) {
            webSocketUsers.put(ID, session);
            session.sendMessage(new TextMessage("成功建立socket连接"));
        }
        log.info("建立连接");
    }

    //接收socket信息
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        log.info("#消息#:{}", webSocketSession);
    }

    /**
     * 发送信息给指定用户
     *
     * @param clientId
     * @param message
     * @return
     */
    public boolean sendMessageToUser(String clientId, TextMessage message) {
        if (webSocketUsers.get(clientId) == null) {
            return false;
        }
        WebSocketSession session = webSocketUsers.get(clientId);
        if (!session.isOpen()) {
            return false;
        }
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 广播信息
     *
     * @param message
     * @return
     */
    public boolean sendMessageToAllUsers(TextMessage message) {
        boolean allSendSuccess = true;
        Set<String> clientIds = webSocketUsers.keySet();
        WebSocketSession session = null;
        for (String clientId : clientIds) {
            try {
                session = webSocketUsers.get(clientId);
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                allSendSuccess = false;
            }
        }

        return allSendSuccess;
    }

    /**
     * 关闭连接
     *
     * @param clientId
     * @return
     */
    public void afterConnectionClosed(String clientId) {
        try {
            if (webSocketUsers.get(clientId) != null) {
                WebSocketSession session = webSocketUsers.get(clientId);
                afterConnectionClosed(session, CloseStatus.NORMAL);
            }
            webSocketUsers.remove(clientId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String uid = session.getUri().toString().split("uid=")[1];
        webSocketUsers.remove(uid);
        if (session.isOpen()) {
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String uid = session.getUri().toString().split("uid=")[1];
        webSocketUsers.remove(uid);
        session.close(status);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}