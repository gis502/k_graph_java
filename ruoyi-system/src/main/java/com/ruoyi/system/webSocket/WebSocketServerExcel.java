package com.ruoyi.system.webSocket;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.domain.entity.SituationPlot;
import com.ruoyi.system.mapper.SituationPlotMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

// @ServerEndpoint 声明并创建了webSocket端点, 并且指明了请求路径
// id 为客户端请求时携带的参数, 用于服务端区分客户端使用
@ServerEndpoint("/WebSocketServerExcel/{sid}")
@Component
public class WebSocketServerExcel {


    // 日志对象
    private static final Logger log = LoggerFactory.getLogger(WebSocketServerExcel.class);

    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    // concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServerExcel> webSocketSet = new CopyOnWriteArraySet<>();
    // private static ConcurrentHashMap<String,WebSocketServer> websocketList = new ConcurrentHashMap<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 接收sid
    private String sid = "";

    @Autowired
    private SituationPlotMapper situationPlotMapper;

    public static WebSocketServerExcel SituationPlotMapperUtil;

    @PostConstruct
    public void init(){
        SituationPlotMapperUtil = this;
        SituationPlotMapperUtil.situationPlotMapper = this.situationPlotMapper;
    }

//    public static int insertPlot(SituationPlot sp){
//        return SituationPlotMapperUtil.situationPlotMapper.addPlot(sp);
//    }
//
//    public static int insertPlotMore(List<SituationPlot> spM){
//        return SituationPlotMapperUtil.situationPlotMapper.addPlotMore(spM);
//    }
//
//    public static int deletePlot(String id){
//        return SituationPlotMapperUtil.situationPlotMapper.deletePlot(id);
//    }

//    public static List<SituationPlot> situationPlotlist(JSONObject obj){
//        List<SituationPlot> spl = new ArrayList<SituationPlot>();
//
//        return spl;
//    }


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
        } catch (IOException e) {
            log.error("websocket IO异常");
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
        JSONObject jsonObject = JSONObject.parseObject(message);
        JSONObject data = jsonObject.getJSONObject("data");
        String operate = jsonObject.getString("operate");
        if(Objects.equals(operate, "delete")){
            String id = jsonObject.getString("id");
//            int resDelete = deletePlot(id);
//            log.info("成功删除"+resDelete+"条");
        }else if(Objects.equals(operate, "add")){
            String drawType = jsonObject.getString("type");
            String eqid = data.getString("eqid");
            String latitude = data.getString("lat");
            String longitude = data.getString("lon");
            String timestamp = "";
            String drawtype = jsonObject.getString("type");
            String drawid = data.getString("id");
            String pointType = data.getString("type");
            String pointdescribe = data.getString("describe");
            String pointname = data.getString("name");
            String img = data.getString("img");
            String height = data.getString("height");
            List<SituationPlot> spm= new ArrayList<SituationPlot>();
            JSONArray positions = data.getJSONArray("positions");
//            switch (drawType){
//                case "point":
//                    SituationPlot sp = new SituationPlot();
//                    sp.setEqid(eqid);
//                    sp.setTimestamp(timestamp);
//                    sp.setDrawtype(drawtype);
//                    sp.setLatitude(latitude);
//                    sp.setLongitude(longitude);
//                    sp.setDrawid(drawid);
//                    sp.setPointtype(pointType);
//                    sp.setPointdescribe(pointdescribe);
//                    sp.setPointname(pointname);
//                    sp.setHeight(height);
//                    sp.setImg(img);
//                    int res = insertPlot(sp);
//                    log.info("新增"+ res + "t");
//                    break;
//                case "polyline":
//                    for(int i=0;i<positions.size();i++){
//                        SituationPlot spPolyline = new SituationPlot();
//                        spPolyline.setEqid(eqid);
//                        spPolyline.setTimestamp(timestamp);
//                        spPolyline.setDrawtype(drawtype);
//                        spPolyline.setDrawid(drawid);
//                        spPolyline.setPointtype(pointType);
//                        spPolyline.setPointdescribe(pointdescribe);
//                        spPolyline.setPointname(pointname);
//                        spPolyline.setImg(img);
//                        spPolyline.setLatitude(positions.getJSONObject(i).getString("y"));
//                        spPolyline.setLongitude(positions.getJSONObject(i).getString("x"));
//                        spPolyline.setHeight(positions.getJSONObject(i).getString("z"));
//                        spm.add(spPolyline);
//                    }
//                    int resPolyline = insertPlotMore(spm);
//                    log.info("asdf"+spm);
//                    log.info("插入成功："+resPolyline+"条线");
//                    break;
//                case "polygon":
//                    for(int i=0;i<positions.size();i++){
//                        SituationPlot spPolygon = new SituationPlot();
//                        spPolygon.setEqid(eqid);
//                        spPolygon.setTimestamp(timestamp);
//                        spPolygon.setDrawtype(drawtype);
//                        spPolygon.setDrawid(drawid);
//                        spPolygon.setPointtype(pointType);
//                        spPolygon.setPointdescribe(pointdescribe);
//                        spPolygon.setPointname(pointname);
//                        spPolygon.setImg(img);
//                        spPolygon.setLatitude(positions.getJSONObject(i).getString("y"));
//                        spPolygon.setLongitude(positions.getJSONObject(i).getString("x"));
//                        spPolygon.setHeight(positions.getJSONObject(i).getString("z"));
//                        spm.add(spPolygon);
//                    }
//                    int resPolygon = insertPlotMore(spm);
//                    log.info("asdf"+spm);
//                    log.info("插入成功："+resPolygon+"个面");
//                    break;
//            }
        }

//        if(data==null){
//
//        }else{
//            String operate = jsonObject.getString("operate");
//            String eqid = "";
//            String timestamp = "";
//            String drawtype = jsonObject.getString("type");
//            String latitude = data.getString("lat");
//            String longitude = data.getString("lon");
//            String drawid = data.getString("id");
//            String pointType = data.getString("type");
//            String pointdescribe = data.getString("describe");
//            String pointname = data.getString("name");
//            String height = data.getString("height");
//            String img = data.getString("img");
//
//            SituationPlot sp = new SituationPlot();
//            sp.setEqid(eqid);
//            sp.setTimestamp(timestamp);
//            sp.setDrawtype(drawtype);
//            sp.setLatitude(latitude);
//            sp.setLongitude(longitude);
//            sp.setDrawid(drawid);
//            sp.setPointtype(pointType);
//            sp.setPointdescribe(pointdescribe);
//            sp.setPointname(pointname);
//            sp.setHeight(height);
//            sp.setImg(img);
//
//
//            log.info("cece"+situationPlotMapper);
//            int res = insertPlot(sp);
//            log.info("新增"+ res + "列");
//
//        }
        log.info("收到来自窗口" + sid + "的信息:" + message);
        // 群发消息
        for (WebSocketServerExcel item : webSocketSet) {
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
        log.error("发生错误");
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
        for (WebSocketServerExcel item : webSocketSet) {
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
        WebSocketServerExcel.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServerExcel.onlineCount--;
    }

    public static CopyOnWriteArraySet<WebSocketServerExcel> getWebSocketSet() {
        return webSocketSet;
    }

}
