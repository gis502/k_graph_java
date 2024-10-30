package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.PowerSupplyInformation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PowerSupplyInformationService extends IService<PowerSupplyInformation>{

    List<PowerSupplyInformation> importExcelPowerSupplyInformation(MultipartFile file, String userName, String eqId) throws IOException;

    List<PowerSupplyInformation> getPowerSupply(String eqid);
}
