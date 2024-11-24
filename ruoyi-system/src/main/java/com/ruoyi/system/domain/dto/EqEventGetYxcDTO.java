package com.ruoyi.system.domain.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author: xiaodemos
 * @date: 2024-11-23 17:48
 * @description: 获取地震事件评估影响场DTO
 */


@Data
public class EqEventGetYxcDTO {

    private String event;
    private String eqqueueId;
    private String type;    //数据类型（geojson、shpfile），默认geojson文件


    public JSONObject toJSONObject() {
        // Create a JSONObject using FastJSON
        JSONObject jsonObject = new JSONObject();

        // Put values into the JSON object
        jsonObject.put("event", event);
        jsonObject.put("eqqueueId", eqqueueId);
        jsonObject.put("type", type);

        return jsonObject;
    }


}
