package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.EarthquakeList;
import com.baomidou.mybatisplus.extension.service.IService;
import org.locationtech.jts.geom.Geometry;

import java.util.List;

public interface EarthquakeListService extends IService<EarthquakeList>{

    List<String> getExcelUploadEarthquake();

    List<EarthquakeList> getEarthquakesWithinDistance(Geometry point, double v);

    List<EarthquakeList> selectAllEq();


    List<EarthquakeList> getGeomById(String id);
}
