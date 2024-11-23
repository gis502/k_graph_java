package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.RiskConstructionGeohazards;
import com.ruoyi.system.domain.entity.RoadDamage;
import com.ruoyi.system.domain.entity.SupplyWater;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface RiskConstructionGeohazardsService extends IService<RiskConstructionGeohazards>{

    List<RiskConstructionGeohazards> importExcelRiskConstructionGeohazards(MultipartFile file, String userName, String eqId) throws IOException;

    List<RiskConstructionGeohazards> RiskConstructionGeohazardsByEqId(String eqid);

    IPage<RiskConstructionGeohazards> searchData(RequestBTO requestBTO);

    List<RiskConstructionGeohazards> fromRiskConstructionGeohazards(String eqid, LocalDateTime time);
}
