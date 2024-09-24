package com.ruoyi.system.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.EarthquakeList;
import com.ruoyi.system.mapper.EarthquakeListMapper;
import com.ruoyi.system.service.EarthquakeListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class EarthquakeListServiceImpl extends ServiceImpl<EarthquakeListMapper, EarthquakeList> implements EarthquakeListService {

    @Autowired
    private EarthquakeListMapper earthquakeListMapper;

    @Override
    public int addEq(EarthquakeList eq) {
        return earthquakeListMapper.addEq(eq);
    }

    @Override
    public int deleteEq(String eqid) {
        return earthquakeListMapper.deleteEq(eqid);
    }

    @Override
    public int updateEq(EarthquakeList eq) {
        return earthquakeListMapper.updateEq(eq);
    }

    @Override
    public List<EarthquakeList> selectAllEq() {
        return earthquakeListMapper.selectAllEq();
    }

    @Override
    public List<EarthquakeList> selectKeyEq() {
        return earthquakeListMapper.selectKeyEq();
    }

    @Override
    public List<EarthquakeList> selectLatestEq() {
        return earthquakeListMapper.selectLatestEq();
    }

    @Override
    public List<String> getExcelUploadEarthquake() {
        // 查询所有的 earthquakeList 数据
        // 自定义日期时间格式化器，确保显示秒
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<EarthquakeList> earthquakeLists = earthquakeListMapper.selectAllEq();

        // 拼接 position、time、magnitude 字段
        List<String> result = new ArrayList<>();

        for (EarthquakeList eq : earthquakeLists) {
            String combined =  eq.getOccurrenceTime().format(formatter).toString().replace("T", " ")+ " "+eq.getEarthquakeName() + "  " +"震级：" +eq.getMagnitude();
            result.add(combined);
        }
        return result;
    }

}

