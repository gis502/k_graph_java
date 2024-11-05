package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.RiskPoints;
import com.ruoyi.system.mapper.RiskPointsMapper;
import com.ruoyi.system.service.RiskPointsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class RiskPointsServiceImpl
        extends ServiceImpl<RiskPointsMapper, RiskPoints>
        implements RiskPointsService {
    @Resource  RiskPointsMapper riskPointsMapper;
    @Override
    public List<RiskPoints> riskPointslist(LambdaQueryWrapper<RiskPoints> wrapper, Double epicentreLongitude, Double epicentreLatitude) {
        List<RiskPoints> riskPoints = riskPointsMapper.selectList(wrapper);
        // 计算每个 RiskPoint 到地震中心的距离
        for (RiskPoints point : riskPoints) {
            Double locationLongitude = point.getLongitude();
            Double locationLatitude = point.getLatitude();
            if (locationLongitude != null && locationLatitude != null) {
                double distance = calculateDistance(epicentreLongitude, epicentreLatitude, locationLongitude, locationLatitude);
                // 保留两位小数
                distance = BigDecimal.valueOf(distance).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                point.setDistance(distance); // 假设 RiskPoints 类中有 distance 字段
            }
        }
        // 根据 riskLevel 排序，顺序为 大型 > 中型 > 小型
        riskPoints.sort(Comparator.comparingInt(point -> {
            switch (point.getRiskLevel()) {
                case "大型":
                    return 1;
                case "中型":
                    return 2;
                case "小型":
                    return 3;
                default:
                    return 4; // 其他级别排在最后
            }
        }));

//        System.out.println("这是riskPoints啊啊啊啊啊啊"+riskPoints);
        return riskPoints;
    }
    // 计算两点之间的直线距离（假设使用 Haversine 公式）
    private double calculateDistance(Double lon1, Double lat1, Double lon2, Double lat2) {
        final int R = 6371; // 地球半径，单位为公里
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // 返回距离，单位为公里
    }
}
