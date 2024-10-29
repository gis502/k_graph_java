package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.BarrierLakeSituation;
import com.ruoyi.system.domain.entity.SupplySituation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SupplySituationService extends IService<SupplySituation>{

    List<SupplySituation> importExcelSupplySituation(MultipartFile file, String userName, String eqId) throws IOException;

    List<SupplySituation> getsupplySituationById(String eqid);
}
