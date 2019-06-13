package demo.spring.boot.demospringboot.config.wesocket.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * 配置 websocket
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        /**
         * 设置订阅地址
         */
        config.enableSimpleBroker("/topic");
        config.setUserDestinationPrefix("/user");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /**
         * 注册链接点 由前端 var socket = new SockJS('/my-websocket'); 链接
         */

        registry.addEndpoint("/my-websocket").addInterceptors(new MyHandlerShareInterceptor()).setHandshakeHandler(new DefaultHandshakeHandler() {
            /**
             * !!! 重要，设置认证的用户
             * @param request
             * @param wsHandler
             * @param attributes
             * @return
             */
            @Override
            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                //设置认证用户
                return (Principal) attributes.get("user");
            }
        }).setAllowedOrigins("*") //允许跨域
                .withSockJS();  //指定使用SockJS协议

    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(webSocketHandler -> new MyWebSocketHandlerDecorator(webSocketHandler));
        super.configureWebSocketTransport(registration);
    }


}