package demo.spring.boot.demospringboot.config.wesocket.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用来管理WebSocket的会话
 */
public class MyWebSocketHandlerDecorator extends WebSocketHandlerDecorator {

    private Logger log = LoggerFactory.getLogger(getClass());
    /**
     * 当前用户
     */
    /*每个浏览器连接都会有一个新的会话对象*/
    private WebSocketSession session;

    /**
     * !!!!!这个处理器存在的意义就是管理Session
     * 用来存储每个会话的session,静态的不会被实例化
     */
    private static List<WebSocketSession> webSocketSets = Collections.synchronizedList(new ArrayList<>());

    public static List<WebSocketSession> getWebSocketSets() {
        return webSocketSets;
    }

    public MyWebSocketHandlerDecorator(WebSocketHandler delegate) {
        super(delegate);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session = session;
        webSocketSets.add(session);
        log.info("建立连接");
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//        log.info("#消息#:{}", session);

        super.handleMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        webSocketSets.remove(session);
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        session.close(closeStatus);
        webSocketSets.remove(session);
        super.afterConnectionClosed(session, closeStatus);
    }
}
