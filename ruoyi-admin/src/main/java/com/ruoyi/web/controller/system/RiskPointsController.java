package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.domain.entity.RiskPoints;
import com.ruoyi.system.service.EarthquakeListService;
import com.ruoyi.system.service.RiskPointsService;
import com.ruoyi.system.service.impl.PublicOpinionServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/risk_points")
public class RiskPointsController {

    @Resource
    private EarthquakeListService earthquakeListService;
    @Resource
    private RiskPointsService riskPointsService;

    @GetMapping("/list/{eqid}")
    public AjaxResult list(@PathVariable String eqid) {
        // 根据前端传来的 eqid 查询对应地震的名字和震级
        EarthquakeList earthquakeList = earthquakeListService.getById(eqid);
        if (earthquakeList == null) {
            return AjaxResult.error("未找到对应的地震信息");
        }
        // 获取地震名称和震级和经纬度
        String earthquakeName = earthquakeList.getEarthquakeName();
        String magnitudeStr = earthquakeList.getMagnitude();
        Double epicentre_longitude = earthquakeList.getLongitude();
        Double epicentre_latitude = earthquakeList.getLatitude();
        // 为了进行大小比较,将震级转换为 Double
        Double magnitude = null;
        if (magnitudeStr != null) {
            try {
                magnitude = Double.parseDouble(magnitudeStr);
            } catch (NumberFormatException e) {
                return AjaxResult.error("震级数据格式错误");
            }
        }
        // 分解地震名称，提取"省市/州" 和 "县/区"
        String cityName = null;
        String countyName = null;
        if (earthquakeName.contains("市")) {
            cityName = earthquakeName.substring(3, earthquakeName.indexOf("市") + 1);
            countyName = earthquakeName.substring(earthquakeName.indexOf("市") + 1);
        } else if (earthquakeName.contains("州")) {
            cityName = earthquakeName.substring(3, earthquakeName.indexOf("州") + 1);
            countyName = earthquakeName.substring(earthquakeName.indexOf("州") + 1);
        }
        // 构建查询条件
        LambdaQueryWrapper<RiskPoints> wrapper = new LambdaQueryWrapper<>();
        //只查询诱发因素是地震的隐患点
        wrapper.like(RiskPoints::getInducingFactors, "地震");
        //根据震级的不同,筛选的范围不一样,隐患点数量不一样
        if (magnitude != null && magnitude > 4) {
            // 震级大于4，模糊匹配"市/州"级别
            if (cityName != null) {
                wrapper.like(RiskPoints::getLocation, cityName);
            }
        } else {
            // 震级小于等于4，模糊匹配"市/州" + "县/区"级别
            if (cityName != null && countyName != null) {
                wrapper.like(RiskPoints::getLocation, cityName + countyName);
            }
        }
        //将与震中的计算放到service层去
        List<RiskPoints> riskPointslist = riskPointsService.riskPointslist(wrapper,epicentre_longitude, epicentre_latitude);
        return AjaxResult.success(riskPointslist);
    }
}
