package com.ruoyi.system.service.impl;

import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.mapper.EarthquakeListMapper;
import com.ruoyi.system.service.EarthquakeListService;

import javax.annotation.Resource;

@Service
public class EarthquakeListServiceImpl extends ServiceImpl<EarthquakeListMapper, EarthquakeList> implements EarthquakeListService{

    @Resource
    private    EarthquakeListMapper earthquakeListMapper;
    @Override
    public List<String> getExcelUploadEarthquake() {
        // 查询所有的 EqList 数据
        // 自定义日期时间格式化器，确保显示秒
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<EarthquakeList> eqLists = earthquakeListMapper.selectList(null);

        // 拼接 position、time、magnitude 字段
        List<String> result = new ArrayList<>();

        for (EarthquakeList eq : eqLists) {
            String combined =  eq.getOccurrenceTime().format(formatter).toString().replace("T", " ")+ " "+eq.getEarthquakeName() + "  " +"震级：" +eq.getMagnitude();
            result.add(combined);
        }
        return result;
    }


    public List<EarthquakeList> getEarthquakesWithinDistance(Geometry point, double distance) {
        return earthquakeListMapper.selectWithinDistance(point, distance);
    }
}
