package demo.spring.boot.demospringboot.config.wesocket.javax;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class WebSocketJavaxController {


    @Scheduled(fixedRate = 1000)
    public Object Scheduled() {
        // 发现消息
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (WebSocket ws : WebSocket.getWebSocketSets()) {
            ws.getSession().getAsyncRemote().sendText("广播：" + new Date());
        }
        return "广播成功";
    }


    @RequestMapping("/httpSendWebSocket")
    public Object httpSendWebSocket(String msg) {
        // 发现消息
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (WebSocket ws : WebSocket.getWebSocketSets()) {
            ws.getSession().getAsyncRemote().sendText("广播：" + new Date());
        }
        return "广播成功";
    }

}
