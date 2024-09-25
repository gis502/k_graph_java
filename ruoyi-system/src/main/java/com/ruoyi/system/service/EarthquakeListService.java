package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.EarthquakeList;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface EarthquakeListService extends IService<EarthquakeList>{

    List<String> getExcelUploadEarthquake();
}
