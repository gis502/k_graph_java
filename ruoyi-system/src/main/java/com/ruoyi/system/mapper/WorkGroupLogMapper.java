package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.AfterSeismicInformation;
import com.ruoyi.system.domain.entity.WorkGroupLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkGroupLogMapper extends BaseMapper<WorkGroupLog> {


    @Select("SELECT earthquake_area_name as workGroupName, COUNT(*) AS workGroupNameInfo " +
            "FROM (" +
            "    SELECT earthquake_area_name , submission_deadline, record_time " +
            "    FROM work_group_log " +
            "    WHERE earthquake_id = #{eqId} " +
            "    ORDER BY submission_deadline DESC, record_time DESC" +
            ") AS sorted_logs " +
            "GROUP BY earthquake_area_name")
    List<Map<String, Object>> getAreaUploadData(String eqId);

    @Select("SELECT report_department as workGroupName, COUNT(*) AS workGroupNameInfo " +
            "FROM (" +
            "    SELECT report_department, submission_deadline, record_time " +
            "    FROM work_group_log " +
            "    WHERE earthquake_id = #{eqId} " +
            "    ORDER BY submission_deadline DESC, record_time DESC" +
            ") AS sorted_logs " +
            "GROUP BY report_department")
    List<Map<String, Object>> getWorkGroupData(String eqId);


}
