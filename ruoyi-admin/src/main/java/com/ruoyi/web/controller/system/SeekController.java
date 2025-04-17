package com.ruoyi.web.controller.system;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.JsonUtils;
import com.ruoyi.system.domain.dto.AiResult;
import com.ruoyi.system.domain.dto.ContentDto;
import com.ruoyi.system.service.GraphService;
import okhttp3.*;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/seek")
public class SeekController {
    private static final Logger logger = LoggerFactory.getLogger(SeekController.class);

    private static final String DONE = "[DONE]";
    private static final Integer timeout = 60;

    private static final String AI_URL = "https://api.deepseek.com/chat/completions";

    @Value("${api.password:}")
    private String apiPassword;

    @GetMapping("/stream")
    public void handleSse(String message, HttpServletResponse response) {
        System.out.println("Received request to /stream endpoint");
        logger.debug("Received message: " + message);

        // 提取关键词并查询图谱数据
        List<Map<String, Object>> graphData = extractKeywords(message);

        try {
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("utf-8");

            try (PrintWriter pw = response.getWriter()) {

                // 1. 拼接提示词
                String intro = String.format("""
                    问题：%s；
                    你的答案将来自以下图谱数据，绝对不可以参考其他数据，其中部分数据可能与问题无关，务必根据问题对数据作出取舍，取舍后也不要用括号注释赘述，并整理成自然语言作答：
                    图谱数据：
                    %s；
                    """, message, JsonUtils.convertObj2Json(graphData)); // 使用你的工具类转成 JSON 字符串

                // 2. 把提示词发给 AI 作为上下文
                getAiResult(pw, intro);  // 把组合后的内容传入 AI

                // 3. 结束 SSE
                pw.write("data:end\n\n");
                pw.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Autowired
    GraphService graphService;

    private List<Map<String, Object>> extractKeywords(String question) {


        List<Map<String, Object>> graphData = new ArrayList<>();
        try {
            // 构建关键词提取的prompt
            String keywordPrompt = """
                    请严格按以下规则从问题中提取地震信息：
                    1. 识别所有提到的事件类型（如：人员伤亡、经济损失、建筑破坏等）
                    2. 若问题中出现时间限定词（如'截止X月X日'、'目前'等），需将此时间绑定到所有相关事件
                    3. 返回JSON数组格式，每个事件必须包含event字段，time和position根据上下文补充
                    4. 时间格式统一为"X月X"（不要"日/号"）
                    5. 若问题中明确提到多个时间，需分别绑定到对应事件
                    6. 尽量返回关键词，一个问题一定有关键词
                    7. 不要做过多解释，不管怎么说，你只要返回下面的格式即可，往event里填词，绝对有词可以填
                    
                    示例问题1: "芦山县5月20日伤亡情况和宝兴县6月1日建筑损坏"
                    应返回: [
                      {"event": "伤亡", "time": "5月20", "position": "芦山县"},
                      {"event": "建筑", "time": "6月1", "position": "宝兴县"}
                    ]
                    
                    当前问题: """ + question;
            Map<String, Object> params = new HashMap<>();
            params.put("model", "deepseek-chat");

            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", keywordPrompt);

            List<Map> messages = new ArrayList<>();
            messages.add(message);
            params.put("messages", messages);
            params.put("stream", false);

            String jsonParams = JsonUtils.convertObj2Json(params);

            Request.Builder builder = new Request.Builder().url(AI_URL);
            builder.addHeader("Authorization", " Bearer " + apiPassword);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams);
            Request request = builder.post(body).build();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    System.out.println("原始响应: " + responseBody);

                    // 使用Map来灵活解析响应
                    Map<String, Object> responseMap = JsonUtils.convertJson2Obj(responseBody, Map.class);
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");

                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> firstChoice = choices.get(0);
                        Map<String, Object> messageObj = (Map<String, Object>) firstChoice.get("message");

                        if (messageObj != null) {
                            String keywords = (String) messageObj.get("content");

                            keywords = keywords.trim()
                                    .replaceAll("(?i)^```json\\s*", "")  // 去掉开头 ```json（不区分大小写）
                                    .replaceAll("(?i)^```\\s*", "")       // 或者可能开头是 ```，兜底
                                    .replaceAll("```\\s*$", "");      // 去掉末尾 ```

                            ObjectMapper mapper = new ObjectMapper();
                            List<Map<String, String>> keywordList = mapper.readValue(keywords, new TypeReference<>() {});
                            for (Map<String, String> item : keywordList) {
                                String event = item.get("event");
                                String time = item.get("time");
                                String position = item.get("position");
                                System.out.println(event + ", " + time + ", " + position);
                            }

                            System.out.println("关键词：" + keywordList);

                            graphData = graphService.getGraphData(keywordList);
                            System.out.println("返回结果： " + graphData);

                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("关键词提取异常", e);
        }
        return graphData;
    }


    private void handleMessage(String keywordPrompt, Map<String, Object> params) {
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", keywordPrompt);

        List<Map> messages = new ArrayList<>();
        messages.add(message);
        params.put("messages", messages);
    }

    private void getAiResult(PrintWriter pw, String content) throws InterruptedException {
        Map<String, Object> params = new HashMap<>();
        params.put("model", "4.0Ultra");
        //result.put("user", "4.0Ultra");

        handleMessage(content, params);
        params.put("stream", true);
        params.put("model", "deepseek-chat");
        String jsonParams = JsonUtils.convertObj2Json(params);

        Request.Builder builder = new Request.Builder().url(AI_URL);
        builder.addHeader("Authorization", " Bearer " + apiPassword);
        builder.addHeader("Accept", "text/event-stream");
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams);
        Request request = builder.post(body).build();
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.SECONDS).writeTimeout(timeout, TimeUnit.SECONDS).readTimeout(timeout,
                TimeUnit.SECONDS).build();

        // 实例化EventSource，注册EventSource监听器 -- 创建一个用于处理服务器发送事件的实例，并定义处理事件的回调逻辑
        CountDownLatch eventLatch = new CountDownLatch(1);

        RealEventSource realEventSource = new RealEventSource(request, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                if (DONE.equals(data)) {
                    return;
                }
                String content = getContent(data);
                pw.write("data:" + JsonUtils.convertObj2Json(new ContentDto(content)) + "\n\n");
                pw.flush();
            }

            @Override
            public void onClosed(EventSource eventSource) {
                super.onClosed(eventSource);
                eventLatch.countDown();
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                logger.info("调用接口失败{}", t);
                if (eventLatch != null) {
                    eventLatch.countDown();
                }
            }
        });
        // 与服务器建立连接
        realEventSource.connect(client);
        // await() 方法被调用来阻塞当前线程，直到 CountDownLatch 的计数变为0。
        eventLatch.await();
    }

    private static String getContent(String data) {
        AiResult aiResult = JsonUtils.convertJson2Obj(data, AiResult.class);
        return aiResult.getChoices().get(0).getDelta().getContent();
    }
}

