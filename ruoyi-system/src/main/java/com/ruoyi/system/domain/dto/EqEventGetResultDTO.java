package com.ruoyi.system.domain.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author: xiaodemos
 * @date: 2024-11-23 17:48
 * @description: 获取评估结果DTO
 */


@Data
public class EqEventGetResultDTO {

    private String event;
    private String eqqueueId;
    private String type;


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
