package com.ruoyi.system.domain.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author: xiaodemos
 * @date: 2024-11-23 15:21
 * @description: 获取地震事件参数DTO
 */


@Data
@Builder
public class EqEventGetPageDTO {

    private String event;   //地震事件编码
    private String eqName;  //地震名称
    private String eqType;  //地震类型（Z正式，Y演练，T测试）
    private LocalDateTime startTime;    //地震开始时间
    private LocalDateTime endTime;      //地震结束时间
    private Double maxMagnitude;        //最大震级
    private Double minMagnitude;        //最小震级
    private Integer source;             //数据来源（0：12322，21：手动触发）
    private Integer pageNum;            //当前页数
    private Integer pageSize;           //分页大小


    /**
     * @author:  xiaodemos
     * @date:  2024/11/23 15:32
     * @description: 把前端传入过来的JSON字符串对象转化为JSONObject对象 用于第三方接口的参数传递
     *
     * @return: 返回JSONObject对象
     */
    public JSONObject toJSONObject() {
        // Create a JSONObject using FastJSON
        JSONObject jsonObject = new JSONObject();

        // Manually convert LocalDateTime fields to string, since LocalDateTime is not directly serializable by FastJSON
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedStartTime = startTime != null ? startTime.format(formatter) : null;
        String formattedEndTime = endTime != null ? endTime.format(formatter) : null;

        jsonObject.put("event", event);
        jsonObject.put("eqName", eqName);
        jsonObject.put("eqType", eqType);
        jsonObject.put("startTime", formattedStartTime);
        jsonObject.put("endTime", formattedEndTime);
        jsonObject.put("maxMagnitude", maxMagnitude);
        jsonObject.put("minMagnitude", minMagnitude);
        jsonObject.put("source", source);
        jsonObject.put("pageNum", pageNum);
        jsonObject.put("pageSize", pageSize);

        return jsonObject;
    }

}
