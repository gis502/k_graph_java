package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.EqList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EqListMapper extends BaseMapper<EqList> {

    @Select("SELECT * FROM eqlist WHERE eqid = #{eqId}")
    List<EqList> findEarthquakeIdByTimeAndPosition(@Param("eqId") String eqId);
}
