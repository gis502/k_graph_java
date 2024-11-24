package com.ruoyi.common.utils.http;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


@Data
@AllArgsConstructor
@Slf4j
public class HttpClientUtil {

    /**
     * 发送post请求
     *
     * @param token    需要携带的参数
     * @param url      需要请求地址
     * @param jsonBody 请求体
     * @return 响应字符串
     */
    public static String doPost(String token, String url, JSONObject jsonBody) {

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(Constants.HTTP_URL + url);

        CloseableHttpResponse response = null;

        String result = "";

        try {

            log.info("设置http post请求参数...");
            // 设置请求头
            httpPost.setHeader(new BasicHeader("User-Agent", Constants.USER_AGENT));
            httpPost.setHeader(new BasicHeader("Authorization", "Bearer " + token));

            // 对上传的JSON对象进行解析
            String reload = jsonBody.toString();

            // 设置以JSON的格式进行数据的请求
            StringEntity entity = new StringEntity(reload, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            log.info("开始发送http post请求...");
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
     * 发送get请求
     *
     * @param token 携带的token验证
     * @param url   请求地址
     * @return 返回字符串
     */
    public static String doGet(String token, String url, JSONObject jsonBody) {

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(Constants.HTTP_URL + url);

        CloseableHttpResponse response = null;

        String result = "";

        try {

            log.info("设置http post请求参数...");
            // 设置请求头
            httpGet.setHeader(new BasicHeader("User-Agent", Constants.USER_AGENT));
            httpGet.setHeader(new BasicHeader("Authorization", "Bearer " + token));

            // 对上传的JSON对象进行解析
            String reload = jsonBody.toString();

            // 设置以JSON的格式进行数据的请求
            StringEntity entity = new StringEntity(reload, ContentType.APPLICATION_JSON);
            //httpGet.setEntity(entity);

            log.info("开始发送http post请求...");
            response = httpClient.execute(httpGet);

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

}
