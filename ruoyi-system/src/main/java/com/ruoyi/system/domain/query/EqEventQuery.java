package com.ruoyi.system.domain.query;

import com.alibaba.fastjson.JSONObject;
import lombok.Builder;
import lombok.Data;

/**
 * @author: xiaodemos
 * @date: 2024-12-14 0:35
 * @description: 批次编码与事件编码的查询参数
 */

@Data
@Builder
public class EqEventQuery {

    private String event;

    public JSONObject toJSONObject() {
        // Create a JSONObject using FastJSON
        JSONObject jsonObject = new JSONObject();

        // Put values into the JSON object
        jsonObject.put("event", event);

        return jsonObject;
    }


}
