package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.RiskConstructionGeohazards;
import com.ruoyi.system.domain.entity.RoadDamage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RiskConstructionGeohazardsService extends IService<RiskConstructionGeohazards>{

    List<RiskConstructionGeohazards> importExcelRiskConstructionGeohazards(MultipartFile file, String userName, String eqId) throws IOException;

}
