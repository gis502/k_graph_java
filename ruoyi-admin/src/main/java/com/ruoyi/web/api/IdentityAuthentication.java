package com.ruoyi.web.api;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author: xiaodemos
 * @date: 2024-11-22 12:03
 * @description: 进行身份验证 获取、更新 token
 */

@Slf4j
@Component
public class IdentityAuthentication {

    @Autowired
    private ThirdPartyHttpClients httpClientService;
    @Resource
    @Qualifier("cacheTemplate")
    private RedisTemplate<String, String> cacheTemplate;
//    @Value("${third.party.username}")
//    private String username;
//    @Value("${third.party.password}")
//    private String password;

    /**
     * @author: xiaodemos
     * @date: 2024/11/22 12:39
     * @description: 获取token
     * @return: 返回获取到的token值
     */
    @Retryable(value = {TimeoutException.class, RuntimeException.class},
            maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public String getAuthenticToken() {

        // 以json格式传入参数
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "test");
        jsonObject.put("password", "Open#Api2025!");

        try {
            String res = httpClientService.doPost("", "/auth", jsonObject);

            // 解析 JSON 字符串为 JSONObject
            JSONObject data = JSONObject.parseObject(res).getJSONObject("data");

            int expireIn = data.getInteger("expire_in");
            String token = data.getString("token");

            log.info("获取到的token为：" + token);
            log.info("token有效期为：" + expireIn);

            storeTokenInRedis(token, expireIn);

            return token;
        } catch (Exception e) {

            log.error("请求第三方接口失败，正在重试... 错误信息: {}", e.getMessage());

            throw e; // 抛出异常以触发重试
        }
    }

    /**
     * @param token    携带的验证
     * @param expireIn token有效期
     * @author: xiaodemos
     * @date: 2024/11/22 12:38
     * @description: 将token存入redis
     */
    private void storeTokenInRedis(String token, int expireIn) {
        // 设置 token 和过期时间
        cacheTemplate.opsForValue().set("token", token, expireIn, TimeUnit.SECONDS);
    }

    /**
     * @author: xiaodemos
     * @date: 2024/11/22 12:41
     * @description: 每五分钟执行一次定时任务，刷新token
     */
//    @Scheduled(fixedRate = 300000)
    public void scheduleTokenRefresh() {

        log.info("Scheduled task started: refreshing token...");

        String authenticToken = getAuthenticToken();

        // log.info("Scheduled task completed: refreshed token: {}", authenticToken);
        log.info("Scheduled task completed...");

    }

    /**
     * @author:  xiaodemos
     * @date:  2024/11/23 4:48
     * @description:  恢复到异常时
     * @param e 异常信息
     */
    @Recover
    public void recover(Exception e) {
        log.error("获取 token 失败，已重试超过最大次数，错误信息: {}", e.getMessage());
        // 可以在这里执行一些失败后的逻辑，比如发送通知或记录失败信息
    }

}
