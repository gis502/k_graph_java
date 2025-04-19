package com.ruoyi.system.domain.dto;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author: xiaodemos
 * @date: 2024-11-23 15:49
 * @description: 地震触发DTO
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EqEventTriggerDTO {

    private String event;   //地震事件编码
    private String eqName;  //地震名称
    private String eqTime;  //地震时间
    private String eqAddr;  //震中位置
    private Double longitude;      //经度
    private Double latitude;      //纬度
    private Double eqMagnitude;  //地震震级
    private Double eqDepth;     //地震深度 单位km
    private String eqType;  //地震类型（Z正式，Y演练，T测试）




 // To convert the DTO to JSONObject
    public JSONObject toJSONObject() {
        // Create a JSONObject using FastJSON
        JSONObject jsonObject = new JSONObject();

        // Manually convert LocalDateTime fields to String, as FastJSON doesn't support LocalDateTime by default
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String formattedEqTime = eqTime != null ? eqTime.format(formatter) : null;

        // Put values into the JSON object
        jsonObject.put("event", event);
        jsonObject.put("eqName", eqName);
        jsonObject.put("eqTime", eqTime);
        jsonObject.put("eqAddr", eqAddr);
        jsonObject.put("longitude", longitude);
        jsonObject.put("latitude", latitude);
        jsonObject.put("eqMagnitude", eqMagnitude);
        jsonObject.put("eqDepth", eqDepth);
        jsonObject.put("eqType", eqType);

        return jsonObject;
    }

}

