package com.ruoyi.system.domain.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

/**
 * @author: xiaodemos
 * @date: 2024-11-23 17:04
 * @description: 重新启动评估DTO, 针对现有地震重新启动评估，同时修改地震基本信息。如果不修改地震信息，则除地震事件编码以外的参数可以不传或为null
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EqEventReassessmentDTO {

    private String event;
    private String eqName;
    private String eqAddr;
    private Double longitude;
    private Double latitude;
    private Double eqMagnitude;


    public JSONObject toJSONObject() {
        // Create a JSONObject using FastJSON
        JSONObject jsonObject = new JSONObject();

        // Put values into the JSON object
        jsonObject.put("event", event);
        jsonObject.put("eqName", eqName);
        jsonObject.put("eqAddr", eqAddr);
        jsonObject.put("longitude", longitude);
        jsonObject.put("latitude", latitude);
        jsonObject.put("eqMagnitude", eqMagnitude);

        return jsonObject;

    }

}
