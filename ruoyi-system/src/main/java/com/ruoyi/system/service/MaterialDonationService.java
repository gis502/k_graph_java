package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.BarrierLakeSituation;
import com.ruoyi.system.domain.entity.MaterialDonation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MaterialDonationService extends IService<MaterialDonation> {

    List<MaterialDonation> importExcelMaterialDonation(MultipartFile file, String userName, String eqId) throws IOException;



}
