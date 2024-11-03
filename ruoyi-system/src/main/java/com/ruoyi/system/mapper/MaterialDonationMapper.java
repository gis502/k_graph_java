package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.MaterialDonation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MaterialDonationMapper extends BaseMapper<MaterialDonation> {
    @Select("""
        WITH RankedDonations AS (
            SELECT 
                *,
                ROW_NUMBER() OVER (
                    PARTITION BY earthquake_area_name 
                    ORDER BY reporting_deadline DESC, system_insert_time DESC
                ) AS rn
            FROM 
                material_donation
            WHERE 
                earthquake_id = #{eqid}
        )
        SELECT 
            *
        FROM 
            RankedDonations
        WHERE 
            rn = 1
    """)
    List<MaterialDonation> MaterialDonationEqId(String eqid);
}
