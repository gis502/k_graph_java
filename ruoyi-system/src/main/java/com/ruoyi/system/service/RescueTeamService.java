package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.RescueTeam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RescueTeamService extends IService<RescueTeam>{


    List<RescueTeam> getByEqid(String eqid);
}

