package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.TransferSettlementInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TransferSettlementInfoMapper extends BaseMapper<TransferSettlementInfo> {

    @Select("""
        SELECT DISTINCT ON (earthquake_area_name) *
        FROM transfer_settlement_info
        WHERE earthquake_id = #{eqid}
        ORDER BY earthquake_area_name, reporting_deadline DESC, system_inserttime DESC
    """)
    List<TransferSettlementInfo> getTotal(@Param("eqid") String eqid);



    @Select("SELECT tsi.earthquake_area_name, " +
            "SUM(tsi.emergency_shelters) AS emergency_shelters, " +
            "SUM(tsi.temporary_shelters) AS temporary_shelters, " +
            "SUM(tsi.newly_transferred) AS newly_transferred, " +
            "SUM(tsi.cumulative_transferred) AS cumulative_transferred, " +
            "tsi.reporting_deadline " +
            "FROM public.transfer_settlement_info tsi " +
            "JOIN ( " +
            "    SELECT earthquake_area_name, MAX(reporting_deadline) AS latest_deadline " +
            "    FROM public.transfer_settlement_info " +
            "    WHERE earthquake_id = #{eqid} " +
            "    GROUP BY earthquake_area_name " +
            ") sub ON tsi.earthquake_area_name = sub.earthquake_area_name " +
            "AND tsi.reporting_deadline = sub.latest_deadline " +
            "WHERE tsi.earthquake_id = #{eqid} " +
            "GROUP BY tsi.earthquake_area_name, tsi.reporting_deadline")
    List<TransferSettlementInfo> getTransferInfo(String eqid);

    @Select("SELECT yas.* " +
            "FROM transfer_settlement_info yas " +
            "JOIN LATERAL (" +
            "    SELECT earthquake_area_name, " +
            "           reporting_deadline, " +
            "           system_inserttime, " +
            "           ROW_NUMBER() OVER (" +
            "               PARTITION BY earthquake_area_name " +
            "               ORDER BY " +
            "                   ABS(EXTRACT(EPOCH FROM (reporting_deadline - #{time}::timestamp))) ASC, " +
            "                   ABS(EXTRACT(EPOCH FROM (system_inserttime - #{time}::timestamp))) ASC" +
            "           ) AS rn " +
            "    FROM transfer_settlement_info " +
            "    WHERE earthquake_id = #{eqid} " +
            "    AND earthquake_area_name = yas.earthquake_area_name " +
            ") sub ON yas.earthquake_area_name = sub.earthquake_area_name " +
            "AND yas.reporting_deadline = sub.reporting_deadline " +
            "AND yas.system_inserttime = sub.system_inserttime " +
            "WHERE yas.earthquake_id = #{eqid} " +
            "AND sub.rn = 1 " +
            "ORDER BY yas.earthquake_area_name")
    List<TransferSettlementInfo> fromtransferSettlementInfo(@Param("eqid") String eqid, @Param("time") LocalDateTime time);


}
