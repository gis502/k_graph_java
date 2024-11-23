package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.BarrierLakeSituation;
import com.ruoyi.system.domain.entity.RiskConstructionGeohazards;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface BarrierLakeSituationService extends IService<BarrierLakeSituation> {

    List<BarrierLakeSituation> importExcelBarrierLakeSituation(MultipartFile file, String userName, String eqId) throws IOException;

    List<BarrierLakeSituation> BarrierLakeSituationByEqId(String eqid);

    IPage<BarrierLakeSituation> searchData(RequestBTO requestBTO);

    List<BarrierLakeSituation> fromBarrierLakeSituation(String eqid, LocalDateTime time);
}
