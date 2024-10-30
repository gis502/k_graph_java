package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.system.domain.entity.RescueActionCasualties;
import com.ruoyi.system.domain.entity.RescueTeam_timeInnerJoin;
import com.ruoyi.system.mapper.RescueActionCasualtiesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.RescueTeam;
import com.ruoyi.system.mapper.RescueTeamMapper;
import com.ruoyi.system.service.RescueTeamService;
@Service
public class RescueTeamServiceImpl extends ServiceImpl<RescueTeamMapper, RescueTeam> implements RescueTeamService{

    @Autowired
    private RescueTeamMapper rescueTeamMapper;
    public List<RescueTeam> getByEqid(String eqid) {
        // 根据 eqid 查询数据库
        QueryWrapper<RescueTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("eqid", eqid);  // 查询条件为 eqid
        return rescueTeamMapper.selectList(queryWrapper);
    }
    public List<RescueTeam_timeInnerJoin> getInfoByEqid(String eqid) {
        return rescueTeamMapper.getInfoByEqid(eqid);
    }

}
