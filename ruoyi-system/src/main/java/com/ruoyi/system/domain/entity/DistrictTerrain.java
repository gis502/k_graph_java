package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "district_terrain")
public class DistrictTerrain {

  private String countyDistrict;
  private long highestAltitude;
  private long minimumAltitude;
  private String terrain;
  private String landform;
}
