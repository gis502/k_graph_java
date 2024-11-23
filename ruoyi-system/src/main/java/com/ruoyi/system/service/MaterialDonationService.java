package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.BarrierLakeSituation;
import com.ruoyi.system.domain.entity.DisasterReliefMaterials;
import com.ruoyi.system.domain.entity.MaterialDonation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface MaterialDonationService extends IService<MaterialDonation> {

    List<MaterialDonation> importExcelMaterialDonation(MultipartFile file, String userName, String eqId) throws IOException;

    List<MaterialDonation> MaterialDonationByEqId(String eqid);

    IPage<MaterialDonation> searchData(RequestBTO requestBTO);

    List<MaterialDonation> fromMaterialDonation(String eqid, LocalDateTime time);
}
