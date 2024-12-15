package com.ruoyi.system.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.YaanJson;
import com.ruoyi.system.mapper.YaanJsonMapper;
import com.ruoyi.system.service.YaanJsonService;

import java.util.List;

@Service
public class YaanJsonServiceImpl extends ServiceImpl<YaanJsonMapper, YaanJson> implements YaanJsonService {
    @Autowired
    private YaanJsonMapper yaanJsonMapper;

    @Override
    public String getAreaStr(String name) {
        return yaanJsonMapper.getAreaStr(name);
    }

    @Override
    public String getintersectionArea(String Area, String Outcir) {
        return yaanJsonMapper.getintersectionArea(Area, Outcir);
    }

    @Override
    public Double computeIntersectionRatio(String intersectionArea, String Outcir) {
        return yaanJsonMapper.computeIntersectionRatio(intersectionArea, Outcir);
    }

    @Override
    public String getPlotBelongCounty(String lon, String lat) {
        String rescounty = null;
        String flag = "f";
        String searchPointGeom = "POINT(" + lon + " " + lat + ")";
//        System.out.println(searchPointGeom);
//        System.out.println("searchPointGeom");
        List<String> CountyArr = List.of("雨城区", "名山区", "荥经县", "汉源县", "石棉县", "天全县", "芦山县", "宝兴县");
        for (int i = 0; i < CountyArr.size(); i++) {
            flag = yaanJsonMapper.getPlotBelongCounty(searchPointGeom, CountyArr.get(i));
            if ("t".equals(flag)) {
                rescounty = CountyArr.get(i);
                break;
            }
        }
        if ("f".equals(flag)) {
//            System.out.println("f");
            rescounty = "不在雅安市";
        }
        System.out.println(rescounty);
        return rescounty;
    }
}
