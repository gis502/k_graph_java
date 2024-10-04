package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.AftershockInformation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface AftershockInformationMapper extends BaseMapper<AftershockInformation> {
    /**
     * 获取最新的余震数据
     * @param eqid
     * @return
     */
    @Select("SELECT yas.magnitude_3_3_9, yas.magnitude_4_4_9, yas.magnitude_5_5_9 " +
            "FROM aftershock_information yas " +
            "WHERE yas.earthquake_identifier = #{eqid} " +
            "ORDER BY yas.system_insert_time DESC " +
            "LIMIT 1")
    Map<String, Integer> getLatestAftershockData(String eqid);
}
