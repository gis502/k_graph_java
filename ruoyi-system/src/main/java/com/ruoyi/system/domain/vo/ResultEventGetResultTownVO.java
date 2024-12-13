package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: xiaodemos
 * @date: 2024-11-27 2:49
 * @description: 乡镇级评估结果返回vo
 */

@Data
public class ResultEventGetResultTownVO {

        @JsonProperty("id")
        private long id; // ID
        @JsonProperty("eqqueueId")
        private String eqqueueId; // 地震评估批次编码
        @JsonProperty("event")
        private String event; // 事件
        @JsonProperty("batch")
        private String batch; // 计算批次
        @JsonProperty("eqName")
        private String eqName; // 地震名称
        @JsonProperty("inty")
        private int inty; // 烈度值
        @JsonProperty("pac")
        private String pac; // 乡镇代码
        @JsonProperty("pacName")
        private String pacName; // 乡镇名称
        @JsonProperty("buildingdamage")
        private double buildingDamage; // 建筑破坏面积
        @JsonProperty("pop")
        private int pop; // 受灾人数
        @JsonProperty("death")
        private int death; // 死亡人数
        @JsonProperty("missing")
        private int missing; // 失踪人数
        @JsonProperty("injury")
        private int injury; // 受伤人数
        @JsonProperty("buriedcount")
        private int buriedCount; // 压埋人数
        @JsonProperty("resetNumber")
        private int resetNumber; // 需紧急安置人员
        @JsonProperty("economicloss")
        private double economicLoss; // 经济损失

}
