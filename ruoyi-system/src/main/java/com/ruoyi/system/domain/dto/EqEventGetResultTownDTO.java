package com.ruoyi.system.domain.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author: xiaodemos
 * @date: 2024-11-26 16:56
 * @description: 乡镇及评估结果
 */

@Data
public class EqEventGetResultTownDTO {

    private String event;
    private String eqqueueId;

    public JSONObject toJSONObject() {
        // Create a JSONObject using FastJSON
        JSONObject jsonObject = new JSONObject();

        // Put values into the JSON object
        jsonObject.put("event", event);
        jsonObject.put("eqqueueId", eqqueueId);

        return jsonObject;
    }


}
