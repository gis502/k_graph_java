package com.ruoyi.web.controller.system;

import com.ruoyi.system.domain.dto.EqEventGetPageDTO;
import com.ruoyi.web.api.IdentityAuthentication;
import com.ruoyi.web.api.ThirdPartyCommonApi;
import com.ruoyi.web.api.ThirdPartyHttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: xiaodemos
 * @date: 2024-11-23 14:36
 * @description: 用于测试接口
 */

@RestController
@RequestMapping("test")
public class TestConmonController {
    @Autowired
    private ThirdPartyHttpClients httpClientService;
    @Resource
    @Qualifier("cacheTemplate")
    private RedisTemplate<String,String> cacheTemplate;
    @Resource
    private IdentityAuthentication identityAuthentication;
    @Resource
    private ThirdPartyCommonApi thirdPartyCommonAPI;
    @GetMapping("/get/resp")
    public String GetResp(){

        String token = cacheTemplate.opsForValue().get("token");
        String res = httpClientService.doGet(token, "/eqevent/getPage", null);

        return res;
    }

    @PostMapping("/post/resp")
    public String PostResp(@RequestBody EqEventGetPageDTO params){

        String seismicEventByGet = thirdPartyCommonAPI.getSeismicEventByGet(params);

        return seismicEventByGet;
    }



}
