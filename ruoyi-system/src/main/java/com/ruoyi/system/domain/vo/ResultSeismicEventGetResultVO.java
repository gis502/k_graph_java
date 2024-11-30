package com.ruoyi.system.domain.vo;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: xiaodemos
 * @date: 2024-11-27 1:29
 * @description: 返回结果经济建筑人员伤亡评估dto
 */

@Data
@Builder
public class ResultSeismicEventGetResultVO {

    @JsonProperty("jj")
    private EconomicLoss jj;
    @JsonProperty("jz")
    private BuildingDamage jz;
    @JsonProperty("sw")
    private SocialInfo sw;

    public ResultSeismicEventGetResultVO(
            @JsonProperty("jj") EconomicLoss jj,
            @JsonProperty("jz") BuildingDamage jz,
            @JsonProperty("sw") SocialInfo sw) {
        this.jj = jj;
        this.jz = jz;
        this.sw = sw;
    }

    @Data
    public static class EconomicLoss {
        @JsonProperty("economicloss")
        private String economicLoss;
    }

    @Data
    public static class BuildingDamage {
        @JsonProperty("buildingdamage")
        private String buildingDamage;
    }

    @Data
    public static class SocialInfo {
        @JsonProperty("death")
        private String death;
        @JsonProperty("buried")
        private String buried;
        @JsonProperty("injury")
        private String injury;
        @JsonProperty("placement")
        private String placement;
    }

}
