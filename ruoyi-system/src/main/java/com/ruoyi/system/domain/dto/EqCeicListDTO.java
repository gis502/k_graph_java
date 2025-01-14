package com.ruoyi.system.domain.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author: xiaodemos
 * @date: 2024-11-23 16:48
 * @description: 获取地震目录（速报）DTO
 */


@Data
@Builder
public class EqCeicListDTO {

    private String createTime;
    private Integer count;
    private String pac;
    private String type;
     public JSONObject toJSONObject() {
        // Create a JSONObject using FastJSON
        JSONObject jsonObject = new JSONObject();

        // Manually convert LocalDateTime fields to String, as FastJSON doesn't support LocalDateTime by default
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String formattedEqTime = eqtime != null ? eqtime.format(formatter) : null;

        // Put values into the JSON object
        // jsonObject.put("eqtime", formattedEqTime);
         jsonObject.put("count", count);
         jsonObject.put("pac", pac);
         jsonObject.put("type", type);
         jsonObject.put("createTime", createTime);
         return jsonObject;
    }


}
