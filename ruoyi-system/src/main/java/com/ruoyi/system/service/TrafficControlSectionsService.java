package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.TrafficControlSections;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.TransferSettlementInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TrafficControlSectionsService extends IService<TrafficControlSections>{


    List<TrafficControlSections> importExcelTrafficControlSections(MultipartFile file, String userName, String eqId) throws IOException;

    IPage<TrafficControlSections> searchData(RequestBTO requestBTO);
}
