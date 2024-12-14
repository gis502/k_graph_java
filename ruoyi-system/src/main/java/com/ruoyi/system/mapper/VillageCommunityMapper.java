package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.VillageCommunity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Mapper
public interface VillageCommunityMapper extends BaseMapper<VillageCommunity> {
    @Select("SELECT * FROM village_community v " +
            "WHERE ST_DWithin(" +
            "   ST_Transform(v.geom, 3857), " + // 将 geom 转换为米单位的坐标系
            "   ST_Transform(ST_SetSRID(ST_MakePoint(#{longitude}, #{latitude}), 4326), 3857), " + // 将经纬度转换为米
            "   20000) " // 20公里 = 20000米
    )
    List<VillageCommunity> findNearbyVillages(@Param("longitude") double longitude, @Param("latitude") double latitude);
}
