package com.ruoyi.web.core.config.webSocket;

import com.ruoyi.system.service.impl.AssessmentBatchServiceImpl;
import com.ruoyi.system.service.impl.EqListServiceImpl;
import com.ruoyi.web.api.service.SeismicMapDownloadService;
import com.ruoyi.web.api.task.MapServerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author: xiaodemos
 * @date: 2025-02-13 16:57
 * @description: websocket专题图服务
 */

@ServerEndpoint("/ws/map/{sid}")
@Component
public class WebSocketMapServer {

    // 日志对象
    private static final Logger log = LoggerFactory.getLogger(WebSocketMapServer.class);

    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    // concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketMapServer> webSocketSet = new CopyOnWriteArraySet<>();
    // private static ConcurrentHashMap<String,WebSocketServer> websocketList = new ConcurrentHashMap<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 接收sid
    private String sid = "";
    public static WebSocketMapServer webSocketMapServer;

    @PostConstruct
    public void init(){
        webSocketMapServer = this;
    }

    /*
     * 客户端创建连接时触发
     * */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        webSocketSet.add(this); // 加入set中
        addOnlineCount(); // 在线数加1
        log.info("有新窗口开始监听:" + sid + ", 当前在线人数为" + getOnlineCount());
        this.sid = sid;
        try {
            sendMessage("{msg:'连接成功'}");

            // 创建依赖对象
//            SeismicMapDownloadService mapDownloadService = new SeismicMapDownloadService();
//            EqListServiceImpl eqListService = new EqListServiceImpl();
//            AssessmentBatchServiceImpl assessmentBatchService = new AssessmentBatchServiceImpl();

            // 创建 MapServerTask 实例并注入依赖
            // MapServerTask mapServerTask = new MapServerTask(mapDownloadService, eqListService, assessmentBatchService);

            // 调用方法
            // mapServerTask.afterConnectionEstablished();

        } catch (IOException e) {
            log.error("websocket IO异常");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 客户端连接关闭时触发
     **/
    @OnClose
    public void onClose() {
        webSocketSet.remove(this); // 从set中删除
        subOnlineCount(); // 在线数减1
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 接收到客户端消息时触发
     */
    @OnMessage
    public void onMessage(String message, Session session) {

        log.info("收到来自窗口" + sid + "的信息:" + message);
        // 群发消息
        for (WebSocketMapServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 连接发生异常时候触发
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误：{}",error);
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送(向浏览器发消息)
     */
    public void sendMessage(String message) throws IOException {
        log.info("服务器消息推送："+message);
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 发送消息到所有客户端
     * 指定sid则向指定客户端发消息
     * 不指定sid则向所有客户端发送消息
     * */
    public static void sendInfo(String message, @PathParam("sid") String sid) throws IOException {
        log.info("推送消息到窗口" + sid + "，推送内容:" + message);
        for (WebSocketMapServer item : webSocketSet) {
            try {
                // 这里可以设定只推送给这个sid的，为null则全部推送
                if (sid == null) {
                    item.sendMessage(message);
                } else if (item.sid.equals(sid)) {
                    item.sendMessage(message);
                }
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketMapServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketMapServer.onlineCount--;
    }

    public static CopyOnWriteArraySet<WebSocketMapServer> getWebSocketSet() {
        return webSocketSet;
    }


}
