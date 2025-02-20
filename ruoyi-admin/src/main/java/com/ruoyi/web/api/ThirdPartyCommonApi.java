package com.ruoyi.web.api;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.query.EqEventQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @author: xiaodemos
 * @date: 2024-11-23 4:53
 * @description: 获取第三方接口实现
 */

@Slf4j
@Component
public class ThirdPartyCommonApi {
    @Resource
    private ThirdPartyHttpClients httpClientService;
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
     * @param event 批次编码和事件编码
     * @author: xiaodemos
     * @date: 2024/11/23 16:58
     * @description:
     * @return: 返回评估批次 List
     */
    public String getSeismicEventGetBatchByGet(String event) {

        String token = cacheTemplate.opsForValue().get("token");

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("event",event);

        String res = httpClientService.doGet(token, "/eqevent/getBatch", jsonBody);

        return res;
    }

    /**
     * @param params 前端传入的参数
     * @author: xiaodemos
     * @date: 2024/11/23 17:45
     * @description: 重新启动评估
     * @return: 返回地震评估批次编码 string
     */
    public String getSeismicEventReassessmentByPost(EqEventReassessmentDTO params) {

        String token = cacheTemplate.opsForValue().get("token");

        JSONObject jsonBody = params.toJSONObject();

        String res = httpClientService.doPost(token, "/eqevent/reassessment", jsonBody);

        return res;
    }

    /**
     * @param params 前端传入的参数
     * @author: xiaodemos
     * @date: 2024/11/23 17:50
     * @description: 获取评估结果
     * @return: 返回评估结果 Map<string,string>
     */
    public String getSeismicEventGetResultByGet(EqEventGetResultDTO params) {

        String token = cacheTemplate.opsForValue().get("token");

        JSONObject jsonBody = params.toJSONObject();

        String res = httpClientService.doGet(token, "/eqevent/getResult", jsonBody);

        return res;
    }

    /**
     * @param params 前端传入的参数  type参数类型（geojson、shpfile），默认geojson文件
     * @author: xiaodemos
     * @date: 2024/11/23 17:50
     * @description: 获取地震事件评估影响场
     * @return: 返回文件件路径 string
     */
    public String getSeismicEventGetYxcByGet(EqEventGetYxcDTO params) {

        String token = cacheTemplate.opsForValue().get("token");

        JSONObject jsonBody = params.toJSONObject();

        String res = httpClientService.doGet(token, "/eqevent/getYxc", jsonBody);

        return res;
    }

    /**
     * @param params 前端传入的参数
     * @author: xiaodemos
     * @date: 2024/11/23 17:59
     * @description: 获取地震灾情报告
     * @return: 返回地震灾情报告 List
     */
    public String getSeismicEventGetReportByGET(EqEventGetReportDTO params) {

        String token = cacheTemplate.opsForValue().get("token");

        JSONObject jsonBody = params.toJSONObject();

        String res = httpClientService.doGet(token, "/eqevent/getReport", jsonBody);

        return res;
    }

    /**
     * @param params 前端传入的参数
     * @author: xiaodemos
     * @date: 2024/11/23 18:02
     * @description: 获取地震专题图
     * @return: 返回专题图路径 List
     */
    public String getSeismicEventGetMapByGet(EqEventGetMapDTO params) {

        String token = cacheTemplate.opsForValue().get("token");

        JSONObject jsonBody = params.toJSONObject();

        String res = httpClientService.doGet(token, "/eqevent/getMap", jsonBody);

        return res;
    }

    //TODO 增加接口

    /**
     * @param params 前端传入的参数
     * @author: jiangshaung
     * @date: 2024/11/24 14:35
     * @description: 获取乡镇级评估结果
     * @return: 返回乡镇级评估结果 List
     */
    public String getSeismicEventGetGetResultTownByGet(EqEventGetResultTownDTO params) {

        String token = cacheTemplate.opsForValue().get("token");

        JSONObject jsonBody = params.toJSONObject();

        String res = httpClientService.doGet(token, "/eqevent/getResultTown", jsonBody);

        return res;
    }

    /**
     * @param params 批次编码和事件编码
     * @author: jiangshaung
     * @date: 2024/11/24 15:35
     * @description: 删除地震事件
     * @return: 返回是否删除成功 (true 或 false )
     */
    public String getSeismicEventGetDeleteByPost(EqEventQuery params) {

        String token = cacheTemplate.opsForValue().get("token");

        JSONObject jsonBody = params.toJSONObject();

        String res = httpClientService.doPost(token, "/eqevent/delete", jsonBody);

        return res;
    }
}


