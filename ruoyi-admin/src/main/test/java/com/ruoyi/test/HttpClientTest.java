package com.ruoyi.test;


import com.ruoyi.system.service.api.ThirdPartyHttpClientService;
import com.ruoyi.web.api.ThirdPartyCommonAPI;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class HttpClientTest {


    @Resource
    private ThirdPartyHttpClientService httpClientService;
    @Resource
    private ThirdPartyCommonAPI thirdPartyCommonAPI;
    @Resource
    @Qualifier("cacheTemplate")
    private RedisTemplate<String,String> cacheTemplate;
    @Test
    void test123(){
        //TODO 测试
    }

}
