package demo.spring.boot.demospringboot.config.wesocket.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Map;

/**
 * websocket拦截器配置，读取session.
 * <p>
 * !!! 根据session，来生成Principal验证用户
 */
public class MyHandlerShareInterceptor extends HttpSessionHandshakeInterceptor {

    public static final String HTTP_SESSION = "httpSession";

    private static Logger logger = LoggerFactory.getLogger(MyHandlerShareInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest arg0, ServerHttpResponse arg1, WebSocketHandler arg2,
                                   Map<String, Object> arg3) throws Exception {

        if (arg0 instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) arg0;
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            if (session != null) {
                //使用userName区分WebSocketHandler，以便定向发送消息
                String httpSessionId = session.getId();
                logger.info(httpSessionId);
                /**
                 * ！！！这里存入的Session会放入WebSocket的属性中
                 */
                arg3.put(MyHandlerShareInterceptor.HTTP_SESSION, session);
                Principal user = () -> "xxx";
                arg3.put("user", user);
            } else {
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        // TODO Auto-generated method stub

        super.afterHandshake(request, response, wsHandler, ex);
    }


}