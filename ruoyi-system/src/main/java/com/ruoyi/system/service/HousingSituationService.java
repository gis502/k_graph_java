package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.DisasterAreaWeatherForecast;
import com.ruoyi.system.domain.entity.HousingSituation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.PowerSupplyInformation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface HousingSituationService extends IService<HousingSituation>{


    List<HousingSituation> importExcelHousingSituation(MultipartFile file, String userName, String eqId) throws IOException;


    List<HousingSituation> getHousingSituationById(String eqid);

    IPage<HousingSituation> searchData(RequestBTO requestBTO);

}
