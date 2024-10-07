package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.TrafficControlSections;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TrafficControlSectionsService extends IService<TrafficControlSections>{


    List<TrafficControlSections> importExcelTrafficControlSections(MultipartFile file, String userName, String eqId) throws IOException;
}
