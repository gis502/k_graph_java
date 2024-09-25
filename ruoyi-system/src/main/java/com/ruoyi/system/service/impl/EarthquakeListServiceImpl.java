package com.ruoyi.system.service.impl;

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
    private    EarthquakeListMapper EarthquakeListMapper;
    @Override
    public List<String> getExcelUploadEarthquake() {
        // 查询所有的 EqList 数据
        // 自定义日期时间格式化器，确保显示秒
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<EarthquakeList> eqLists = EarthquakeListMapper.selectAllEq();

        // 拼接 position、time、magnitude 字段
        List<String> result = new ArrayList<>();

        for (EarthquakeList eq : eqLists) {
            String eqid = eq.getEqid().toString();
            String combined =  eq.getOccurrenceTime().format(formatter).toString().replace("T", " ")+ " "+eq.getEarthquakeName() + "  " +"震级：" +eq.getMagnitude();
            String resultString = eqid + " - " + combined; // 使用 "-" 或其他分隔符连接
            result.add(resultString);
        }
        return result;
    }
}
