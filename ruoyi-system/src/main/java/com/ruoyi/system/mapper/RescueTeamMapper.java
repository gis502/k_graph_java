package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.annotation.PlotInfoMapper;
import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.domain.entity.RescueTeam;
import com.ruoyi.system.domain.entity.RescueTeam_timeInnerJoin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


import org.locationtech.jts.geom.Geometry;

import java.util.List;

@Mapper
@PlotInfoMapper
public interface RescueTeamMapper extends BaseMapper<RescueTeam> {
    @Select(value = "select rescue_team.uuid,\n" +
            "rescue_team.team_name, \n" +
            "rescue_team.station_province,\n" +
            "rescue_team.station_city,\n" +
            "rescue_team.station_county,\n" +
            "rescue_team.personnel_count, \n" +
            "rescue_team.team_nature,\n" +
            "rescue_team.team_type,\n" +
            "rescue_team.departure_date,\n" +
            "rescue_team.estimated_arrival_date, \n" +
            "rescue_team.planned_rescue_area,\n" +
            "rescue_team.contact_person,\n" +
            "rescue_team.contact_phone,\n" +
            "rescue_team.on_standby, \n" +
            "rescue_team.describe_things,\n" +
            "rescue_team.plot_id, \n" +
            "situation_plot.start_time\n" +
            "from rescue_team inner join situation_plot\n" +
            "on rescue_team.plot_id=situation_plot.plot_id\n" +
            "where eqid=#{eqid}")
    List<RescueTeam_timeInnerJoin> getInfoByEqid(String eqid);
}
