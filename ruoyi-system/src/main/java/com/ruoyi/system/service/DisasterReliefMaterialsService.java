package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.DisasterReliefMaterials;
import com.ruoyi.system.domain.entity.LargeSpecialRescueEquipment;
import com.ruoyi.system.domain.entity.RescueForces;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DisasterReliefMaterialsService extends IService<DisasterReliefMaterials> {
    List<DisasterReliefMaterials> importExcelDisasterReliefMaterials(MultipartFile file, String userName, String eqId) throws IOException;

    List<DisasterReliefMaterials> DisasterReliefMaterialsByEqId(String eqid);

    IPage<DisasterReliefMaterials> searchData(RequestBTO requestBTO);
}
