package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.CommunicationFacilityDamageRepairStatus;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.TrafficControlSections;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CommunicationFacilityDamageRepairStatusService extends IService<CommunicationFacilityDamageRepairStatus>{


    List<CommunicationFacilityDamageRepairStatus> importExcelCommunicationFacilityDamageRepairStatus(MultipartFile file, String userName, String eqId) throws IOException;

    List<CommunicationFacilityDamageRepairStatus> facility(String eqid);

    IPage<CommunicationFacilityDamageRepairStatus> searchData(RequestBTO requestBTO);

}
