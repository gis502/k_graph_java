package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.LargeSpecialRescueEquipment;
import com.ruoyi.system.domain.entity.RescueForces;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface LargeSpecialRescueEquipmentService extends IService<LargeSpecialRescueEquipment> {
    List<LargeSpecialRescueEquipment> importExcelLargeSpecialRescueEquipment(MultipartFile file, String userName, String eqId) throws IOException;

    List<LargeSpecialRescueEquipment> LargeSpecialRescueEquipmentByEqId(String eqid);

    IPage<LargeSpecialRescueEquipment> searchData(RequestBTO requestBTO);

}
