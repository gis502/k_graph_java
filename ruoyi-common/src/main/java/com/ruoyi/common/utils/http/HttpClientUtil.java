package com.ruoyi.common.utils.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
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

    private static final String USER_AGENT = "Mozilla/5.0";
    private String token;
    private String url = "http://tq-test.xixily.com:10340/api/open/auth";

    public String doPost(String url, String jsonText) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        HttpResponse response = null;
        String result = "";
        try {
            System.out.println("发送post请求");

            // 设置请求头，包括token
            httpPost.setHeader(new BasicHeader("User-Agent", USER_AGENT));
            httpPost.setHeader(new BasicHeader("Authorization", "Bearer " + token));

            // 将Java对象转换为JSON字符串
            StringEntity entity = new StringEntity(jsonText, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity());
            }
        } catch (IOException e) {
            System.out.println("http post io 异常发生了");
            e.printStackTrace();
        } finally {
            try {
                System.out.println("释放资源、关闭连接");
                if (response != null) {
                    ((CloseableHttpResponse) response).close();
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
