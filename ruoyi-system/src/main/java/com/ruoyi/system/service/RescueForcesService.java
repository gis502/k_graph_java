package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.AfterSeismicInformation;
import com.ruoyi.system.domain.entity.RescueForces;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RescueForcesService extends IService<RescueForces> {
    List<RescueForces> importExcelRescueForces(MultipartFile file, String userName, String eqId) throws IOException;
}
