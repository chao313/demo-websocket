package demo.spring.boot.demospringboot.config.wesocket.javax;

import org.apache.catalina.session.StandardSessionFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;


/**
 * 主要的配置类
 * 本类必须要继承Configurator,因为@ServerEndpoint注解中的config属性只接收这个类型
 *
 * @author whc
 */
@Configuration
public class WebsocketConfig extends ServerEndpointConfig.Configurator {

    public static final String HTTP_SESSION = "httpSession";

    /**
     * 修改握手,就是在握手协议建立之前修改其中携带的内容
     * ->建立连接之前去放入登陆消息
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        /*如果没有监听器,那么这里获取到的HttpSession是null*/
        StandardSessionFacade ssf = (StandardSessionFacade) request.getHttpSession();
        if (ssf != null) {
            sec.getUserProperties().put(WebsocketConfig.HTTP_SESSION, request.getHttpSession());
        }
        super.modifyHandshake(sec, request, response);
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
