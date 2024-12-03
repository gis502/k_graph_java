package com.ruoyi.system.domain.dto;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author: xiaodemos
 * @date: 2024-11-26 16:56
 * @description: 乡镇级评估结果
 */

@Data
@Builder
public class EqEventGetResultTownDTO {

    private String event;
    private String eqqueueId;

    @JsonCreator
    public EqEventGetResultTownDTO(@JsonProperty("event") String event, @JsonProperty("eqqueueId") String eqqueueId) {
        this.event = event;
        this.eqqueueId = eqqueueId;
    }

    public JSONObject toJSONObject() {
        // Create a JSONObject using FastJSON
        JSONObject jsonObject = new JSONObject();

        // Put values into the JSON object
        jsonObject.put("event", event);
        jsonObject.put("eqqueueId", eqqueueId);

        return jsonObject;
    }


}
