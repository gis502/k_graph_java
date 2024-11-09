package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.AfterSeismicInformation;
import com.ruoyi.system.domain.entity.DisasterAreaWeatherForecast;
import com.ruoyi.system.domain.entity.RescueForces;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RescueForcesService extends IService<RescueForces> {
    List<RescueForces> importExcelRescueForces(MultipartFile file, String userName, String eqId) throws IOException;

    List<RescueForces> RescueForcesByEqId(String eqid);

    IPage<RescueForces> searchData(RequestBTO requestBTO);

}
