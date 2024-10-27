package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.DisasterAreaWeatherForecast;
import com.ruoyi.system.domain.entity.HousingSituation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface HousingSituationService extends IService<HousingSituation>{


    List<HousingSituation> importExcelHousingSituation(MultipartFile file, String userName, String eqId) throws IOException;




}
