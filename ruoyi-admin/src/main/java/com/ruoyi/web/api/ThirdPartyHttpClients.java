package com.ruoyi.web.api;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.ruoyi.common.constant.Constants.PROMOTION_INVOKE_URL_HEAD;

/**
 * @author: xiaodemos
 * @date: 2024-11-22 11:31
 * @description: 调用第三方接口公共类
 */
@Slf4j
@Component
public class ThirdPartyHttpClients {

    /**
     * 发送post请求
     *
     * @param token    需要携带的参数
     * @param url      需要请求地址
     * @param jsonBody 请求体
     * @return 响应字符串
     */
    public String doPost(String token, String url, JSONObject jsonBody) {

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000) // 连接超时时间为5秒
                .setSocketTimeout(60000) // 请求超时时间为5秒
                .build();

        CloseableHttpClient httpClient = HttpClients.createDefault();

        CloseableHttpResponse response = null;

        String result = "";

        try {

//            HttpPost httpPost = new HttpPost(PROMOTION_INVOKE_URL_HEAD + "/api/open" + url);
            HttpPost httpPost = new HttpPost(PROMOTION_INVOKE_URL_HEAD + "/api/open" + url);

            log.info("设置http post请求参数...");

            // 设置请求头
            httpPost.setHeader(new BasicHeader("User-Agent", "Mozilla/5.0"));
            httpPost.setHeader(new BasicHeader("Authorization", "Bearer " + token));

            // 对上传的JSON对象进行解析
            String reload = jsonBody.toString();

            // 设置以JSON的格式进行数据的请求
            StringEntity entity = new StringEntity(reload, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            httpPost.setConfig(requestConfig);

            log.info("开始发送http post请求...{}",entity);
            response = httpClient.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                log.info("http post请求成功...");
                result = EntityUtils.toString(response.getEntity());
            }
        } catch (IOException e) {

            log.error("发送http post请求失败{}", e);

            e.printStackTrace();

        } finally {

            try {

                if (response != null) {

                    response.close();
                }
                if (httpClient != null) {

                    httpClient.close();
                }
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 发送GET请求
     *
     * @param token    携带的token验证
     * @param url      请求地址
     * @param jsonBody 请求的参数对象（将其转换为查询字符串）
     * @return 返回字符串
     */
    public String doGet(String token, String url, JSONObject jsonBody) {

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000) // 连接超时时间为5秒
                .setSocketTimeout(60000) // 请求超时时间为5秒
                .build();

        CloseableHttpClient httpClient = HttpClients.createDefault();

        CloseableHttpResponse response = null;

        String result = "";
        String fullUrl = "";

        try {
            log.info("设置http get请求参数...");

            // 将jsonBody转换为查询字符串
            String queryParams = convertJsonToQueryString(jsonBody);

            if (jsonBody != null) {
                // 拼接URL，将参数添加到URL的查询部分
                fullUrl = PROMOTION_INVOKE_URL_HEAD+"/api/open" + url + "?" + queryParams;
            } else {
                fullUrl = PROMOTION_INVOKE_URL_HEAD+"/api/open" + url;
            }

            HttpGet httpGet = new HttpGet(fullUrl);

            // 设置请求头
            httpGet.setHeader(new BasicHeader("User-Agent", "Mozilla/5.0"));
            httpGet.setHeader(new BasicHeader("Authorization", "Bearer " + token));

            log.info("开始发送http get请求...");
            response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                log.info("http get请求成功...");
                result = EntityUtils.toString(response.getEntity());
            }
        } catch (IOException e) {
            log.error("发送http get请求失败{}", e);
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 将JSONObject对象转换为URL查询字符串
     *
     * @param jsonBody 请求的参数对象
     * @return 转换后的查询字符串
     */
    public String convertJsonToQueryString(JSONObject jsonBody) {
        log.info("开始将json对象转换为查询字符串...");
        StringBuilder queryString = new StringBuilder();

        // 定义日期时间格式化器
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (jsonBody != null) {
            for (String key : jsonBody.keySet()) {
                try {
                    Object rawValue = jsonBody.get(key);
                    String value = null;

                    if (rawValue instanceof LocalDateTime) {
                        // 如果是 LocalDateTime，格式化为字符串
                        value = ((LocalDateTime) rawValue).format(dateTimeFormatter);
                    } else if (rawValue != null) {
                        // 其他类型直接调用 toString
                        value = rawValue.toString();
                    }

                    // 过滤掉 null 值
                    if (value != null) {

                        if (!queryString.isEmpty()) {
                            queryString.append("&");
                        }
                        // 仅对键进行 URL 编码，值保持原始格式，空格替换为 %20
                        queryString.append(URLEncoder.encode(key, "UTF-8"))
                                .append("=")
                                // 将空格替换为 %20
                                .append(value.replace(" ", "%20"));
                    }
                } catch (Exception e) {
                    log.error("Error encoding key-value pair: {}={}", key, jsonBody.get(key), e);
                }
            }
        }

        return queryString.toString();
    }

}


