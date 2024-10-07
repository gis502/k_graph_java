package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.TransferSettlementInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TransferSettlementInfoMapper extends BaseMapper<TransferSettlementInfo> {
    @Select("SELECT " +
            "SUM(emergency_shelters) AS emergency_shelters, " +
            "SUM(temporary_shelters) AS temporary_shelters, " +
            "SUM(newly_transferred) AS newly_transferred, " +
            "SUM(cumulative_transferred) AS cumulative_transferred,"+
            "MAX(reporting_deadline) AS reporting_deadline " +
            "FROM public.transfer_settlement_info " +
            "WHERE earthquake_id = #{eqid}")
    List<TransferSettlementInfo> getTotal(String eqid);
}