package com.ruoyi.web.api;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.system.domain.dto.EqCeicListDTO;
import com.ruoyi.system.domain.dto.EqEventGetPageDTO;
import com.ruoyi.system.domain.dto.EqEventTriggerDTO;
import com.ruoyi.system.service.api.service.ThirdPartyHttpClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @author: xiaodemos
 * @date: 2024-11-23 4:53
 * @description: 获取第三方接口实现类
 */

@Slf4j
@Component
public class ThirdPartyCommonAPI {
    @Resource
    private ThirdPartyHttpClientService httpClientService;
    @Resource
    @Qualifier("cacheTemplate")
    private RedisTemplate<String, String> cacheTemplate;

    /**
     * @param params 前端传入的参数
     * @author: xiaodemos
     * @date: 2024/11/23 10:37
     * @description: 获取地震事件
     * @return: 返回第地震事件的数据 List
     */
    public String getSeismicEventByGet(EqEventGetPageDTO params) {

        // 获取token
        String token = cacheTemplate.opsForValue().get("token");

        //前端传入JSON字符串，需要进行解析成JSON对象再传入到doGet或者doPost中
        JSONObject jsonBody = params.toJSONObject();

        String res = httpClientService.doGet(token, "/eqevent/getPage", jsonBody);

        return res;
    }

    /**
     * @param params 前端传入的参数
     * @author: xiaodemos
     * @date: 2024/11/23 15:47
     * @description: 获取地震事件触发
     * @return: 返回地震事件触发数据 eqqueueId（String）
     */
    public String getSeismicTriggerByPost(EqEventTriggerDTO params) {

        String token = cacheTemplate.opsForValue().get("token");

        JSONObject jsonBody = params.toJSONObject();

        String res = httpClientService.doPost(token, "/eqevent/trigger", jsonBody);

        return res;
    }

    /**
     * @param params 前端传入的参数
     * @author: xiaodemos
     * @date: 2024/11/23 16:47
     * @description: 获取地震目录（速报）
     * @return: 返回地震目录数据 List
     */
    public String getSeismicListByGet(EqCeicListDTO params) {

        String token = cacheTemplate.opsForValue().get("token");

        JSONObject jsonBody = params.toJSONObject();

        String res = httpClientService.doGet(token, "/ceic/list", jsonBody);

        return res;
    }

    /**
     * @param event 批次编码
     * @author: xiaodemos
     * @date: 2024/11/23 16:58
     * @description:
     * @return: 返回评估批次 List
     */
    public String getSeismicEventGetBatchByGet(String event) {

        String token = cacheTemplate.opsForValue().get("token");

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("event", event);

        String res = httpClientService.doGet(token, "/eqevent/getBatch", jsonBody);

        return res;

    }

    public String getSeismicEventReassessmentByPost(String event) {

        return "";
    }

}
