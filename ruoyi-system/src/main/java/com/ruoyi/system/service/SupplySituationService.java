package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.BarrierLakeSituation;
import com.ruoyi.system.domain.entity.HousingSituation;
import com.ruoyi.system.domain.entity.SupplySituation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface SupplySituationService extends IService<SupplySituation>{

    List<SupplySituation> importExcelSupplySituation(MultipartFile file, String userName, String eqId) throws IOException;

    List<SupplySituation> getSupplySituationById(String eqid);

    IPage<SupplySituation> searchData(RequestBTO requestBTO);

    List<SupplySituation> fromSupplySituation(String eqid, LocalDateTime time);
}
