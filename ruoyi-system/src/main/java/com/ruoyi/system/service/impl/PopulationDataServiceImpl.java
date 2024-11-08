package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.PopulationData;
import com.ruoyi.system.mapper.PopulationDataMapper;
import com.ruoyi.system.service.PopulationDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class PopulationDataServiceImpl extends
        ServiceImpl<PopulationDataMapper, PopulationData>
        implements PopulationDataService {
    @Resource
    private PopulationDataMapper populationDataMapper;

    @Override
    public List<PopulationData> listall() {
        // 获取所有PopulationData数据
        List<PopulationData> populationDataList = populationDataMapper.selectList(null);

        // 创建一个Map用于聚合区域数据
        Map<String, PopulationData> aggregatedData = new HashMap<>();
        // 遍历数据，根据区域前缀聚合
        for (PopulationData data : populationDataList) {
            // 提取区域的前缀，假设前两个字符是大区域名称
            String areaPrefix = data.getArea().substring(0, 3); // 根据实际情况调整

            // 检查是否已存在该区域的聚合数据
            PopulationData aggregated = aggregatedData.getOrDefault(areaPrefix, new PopulationData());
            aggregated.setArea(areaPrefix);
            aggregated.setUpdateTime(data.getUpdateTime());

            // 累加总人口数
            int currentPopulation = aggregated.getTotalPopulation() != null ? Integer.parseInt(aggregated.getTotalPopulation()) : 0;
            int newPopulation = Integer.parseInt(data.getTotalPopulation());
            aggregated.setTotalPopulation(String.valueOf(currentPopulation + newPopulation));

            // 将聚合后的数据放入Map
            aggregatedData.put(areaPrefix, aggregated);
        }

        // 将Map的值转换为List返回
        return new ArrayList<>(aggregatedData.values());
    }
}