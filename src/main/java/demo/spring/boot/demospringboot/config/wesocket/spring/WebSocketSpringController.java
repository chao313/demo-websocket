package demo.spring.boot.demospringboot.config.wesocket.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class WebSocketSpringController {

    @Autowired
    private HttpSession session;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/")
    public String index() {
        return "index";
    }


    /**
     * client 使用http向指定主题推送消息 + 使用 messagingTemplate 由 server 向客户端推送消息
     */
    @RequestMapping("/httpSendToServer")
    @ResponseBody
    public String httpSendToServer(@RequestParam(value = "msg") String msg) {
        SocketMessage message = new SocketMessage();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        message.date = msg + " : " + df.format(new Date());
        messagingTemplate.convertAndSend("/topic/serverSendToClient", message);
        return msg + "使用http发送消息成功";
    }

    /**
     * socketClient -> SendToServer
     * 方式1：利用 messagingTemplate 向指定主题推送消息
     *
     * @MessageMapping("/sendToServer") 和 stompClient.send（）对应
     * ->向地址为"/sendToServer"的服务器进行消息发送
     */
    @MessageMapping("/clientSendToServer2")
    public void clientSendToServer(SocketMessage message) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        message.date = df.format(new Date());
        messagingTemplate.convertAndSend("/topic/serverSendToClient", message);
    }


    /**
     * socketServer -> SendToClient
     * <p>
     * 方式2：使用 @SendTo 替代 messagingTemplate
     * <p>
     *
     * <p>
     * socket客户端发送消息到服务端
     * <p>
     *
     * @SendTo("/topic/sendToClient") 和 stompClient.subscribe（）对应
     * ->向订阅了为"/topic/serverSendToClient"的client进行消息发送
     */
    @MessageMapping("/clientSendToServer")
    @SendTo("/topic/serverSendToClient")
    public SocketMessage serverSendToClient(SocketMessage message) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        message.date = df.format(new Date());
        return message;
    }

    /**
     * 定时任务 -> 定时向前端推送数据
     *
     * @return
     * @throws Exception
     */
    @Scheduled(fixedRate = 1000)
    @SendTo("/topic/callback")
    public Object callback() throws Exception {
        // 发现消息
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        messagingTemplate.convertAndSend("/topic/callback", df.format(new Date()));
        return "callback";
    }


    /**
     * client 使用http向指定主题推送消息 + 使用 messagingTemplate 由 server 向客户端推送消息
     * <p>
     * + 向指定用户推送消息
     */
    @MessageMapping("/httpSendToServerToClientUser")
    public String httpSendToServerToClientUser(@RequestParam(value = "msg") String msg) {
        for (WebSocketSession socketSession : MyWebSocketHandlerDecorator.getWebSocketSets()) {
            HttpSession httpSession = (HttpSession) socketSession.getAttributes().get((MyHandlerShareInterceptor.HTTP_SESSION));
            SocketMessage message = new SocketMessage();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            message.date = msg + " : " + df.format(new Date());
            messagingTemplate.convertAndSendToUser(socketSession.getPrincipal().getName(), "/topic/httpSendToServerToClientUser", message);
        }

        return msg + "使用http发送消息成功";
    }

}
