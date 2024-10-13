package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.YaanJson;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface YaanJsonMapper extends BaseMapper<YaanJson> {
    String getAreaStr(String name);
    String getintersectionArea(@Param("Outcir") String Outcir, @Param("Area") String Area);
    Double computeIntersectionRatio(@Param("intersectionArea") String intersectionArea, @Param("Outcir") String Outcir);
}