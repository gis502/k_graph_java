package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.DisasterAreaWeatherForecast;
import com.ruoyi.system.domain.entity.SecondaryDisasterInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DisasterAreaWeatherForecastService extends IService<DisasterAreaWeatherForecast> {

    List<DisasterAreaWeatherForecast> importExcelDisasterAreaWeatherForecast(MultipartFile file, String userName, String eqId) throws IOException;



}
