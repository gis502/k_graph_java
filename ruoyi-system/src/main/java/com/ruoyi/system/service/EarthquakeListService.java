package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.EarthquakeList;

import java.util.List;

public interface EarthquakeListService extends IService<EarthquakeList> {
    public int addEq(EarthquakeList eq);
    public int deleteEq(String eqid);
    public int updateEq(EarthquakeList eq);
    public List<EarthquakeList> selectAllEq();
    public List<EarthquakeList> selectKeyEq();
    public List<EarthquakeList> selectLatestEq();

    List<String> getExcelUploadEarthquake();

}
