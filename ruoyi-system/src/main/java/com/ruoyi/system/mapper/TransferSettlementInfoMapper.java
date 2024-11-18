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


//    @Select("SELECT " +
//            "   earthquake_id, " +
//            "   earthquake_area_name, " +
//            "   (SELECT reporting_deadline " +
//            "    FROM transfer_settlement_info t2 " +
//            "    WHERE t2.earthquake_area_name = t1.earthquake_area_name " +
//            "    AND t2.earthquake_id = t1.earthquake_id " +
//            "    ORDER BY reporting_deadline DESC, system_inserttime DESC " +
//            "    LIMIT 1) AS reporting_deadline, " +
//            "   (SELECT system_inserttime " +
//            "    FROM transfer_settlement_info t2 " +
//            "    WHERE t2.earthquake_area_name = t1.earthquake_area_name " +
//            "    AND t2.earthquake_id = t1.earthquake_id " +
//            "    ORDER BY reporting_deadline DESC, system_inserttime DESC " +
//            "    LIMIT 1) AS system_inserttime, " +
//            "   SUM(emergency_shelters) AS emergency_shelters, " +
//            "   SUM(temporary_shelters) AS temporary_shelters, " +
//            "   SUM(newly_transferred) AS newly_transferred, " +
//            "   SUM(cumulative_transferred) AS cumulative_transferred, " +
//            "   SUM(centralized_settlement) AS centralized_settlement " +
//            "FROM transfer_settlement_info t1 " +
//            "WHERE earthquake_id = #{eqid} " +
//            "GROUP BY earthquake_area_name, earthquake_id " +
//            "ORDER BY earthquake_area_name")

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
            "FROM public.transfer_settlement_info yas " +
            "JOIN ( " +
            "    SELECT affected_area, MIN(ABS(EXTRACT(EPOCH FROM (earthquake_time - #{time})))) AS min_time_diff " +
            "    FROM public.transfer_settlement_info " +
            "    WHERE earthquake_id = #{eqid} " +
            "    GROUP BY affected_area " +
            ") sub ON yas.affected_area = sub.affected_area " +
            "AND ABS(EXTRACT(EPOCH FROM (yas.earthquake_time - #{time}))) = sub.min_time_diff " +
            "WHERE yas.earthquake_id = #{eqid} " +
            "ORDER BY yas.affected_area")
    List<TransferSettlementInfo> fromtransferSettlementInfo(String eqid, LocalDateTime time);

}
