package com.ruoyi.web.controller.system;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.dto.EqFormDto;
import com.ruoyi.system.domain.dto.GeometryDTO;
import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.service.EarthquakeListService;
import org.apache.ibatis.annotations.Delete;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.ruoyi.common.enums.BusinessType;
@Validated
@RestController
@RequestMapping("/system")
public class EarthquakeListController {
    @Resource
    private EarthquakeListService earthquakeListService;

    @GetMapping("/getExcelUploadEarthquake")
    public List<String> selectEarthquakeList() {
        List<String> data = earthquakeListService.getExcelUploadEarthquake();
        return data;
    }

    @GetMapping("/geteq")
    public List<EarthquakeList> selectAllEq() {
        return earthquakeListService.selectAllEq();
    }

    @GetMapping("getLatesteq")
    public List<EarthquakeList> getLatesteq() {
        return earthquakeListService.list();
    }

    @PostMapping("queryEqById")
    public EarthquakeList queryEqById(@RequestParam(value = "id") String id) {
        return earthquakeListService.getById(id);
    }

    @GetMapping("/queryEq")
    public List<EarthquakeList> queryEq(@RequestParam(value = "queryValue", required = false) String queryValue) {
        LambdaQueryWrapper<EarthquakeList> QueryWrapper = new LambdaQueryWrapper<>();
        QueryWrapper.like(EarthquakeList::getEarthquakeName, queryValue)
                .or().like(EarthquakeList::getMagnitude, queryValue)
                .or().like(EarthquakeList::getDepth, queryValue)
                .or().apply("to_char(occurrence_time, 'YYYY-MM-DD HH24:MI:SS') LIKE {0}", "%" + queryValue + "%")
                .orderByDesc(EarthquakeList::getOccurrenceTime);
        return earthquakeListService.list(QueryWrapper);
    }

    @PostMapping("/fromEq")
    public List<EarthquakeList> fromEq(@RequestBody EqFormDto queryDTO) {
        // 验证震级和深度的顺序
        if (Double.valueOf(queryDTO.getStartMagnitude()) > Double.valueOf(queryDTO.getEndMagnitude())) {
            throw new IllegalArgumentException("起始震级必须小于等于结束震级");
        }
        if (Double.valueOf(queryDTO.getStartDepth()) > Double.valueOf(queryDTO.getEndDepth())) {
            throw new IllegalArgumentException("起始深度必须小于等于结束深度");
        }
        LambdaQueryWrapper<EarthquakeList> QueryWrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getEarthquakeName() != null && !queryDTO.getEarthquakeName().isEmpty()) {
            QueryWrapper.like(EarthquakeList::getEarthquakeName, queryDTO.getEarthquakeName());
        }

        // 如果前端传递了 startTime 和 endTime，则用于筛选 occurrence_time
        if (queryDTO.getStartTime() != null && queryDTO.getEndTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // ISO格式
            LocalDateTime startTime = LocalDateTime.parse(queryDTO.getStartTime(), formatter);
            LocalDateTime endTime = LocalDateTime.parse(queryDTO.getEndTime(), formatter);

            QueryWrapper.between(EarthquakeList::getOccurrenceTime, startTime, endTime);
            System.out.println("筛选时间范围: " + startTime + " 至 " + endTime);
        }


        if (queryDTO.getStartMagnitude() != null && !queryDTO.getStartMagnitude().isEmpty()) {
            QueryWrapper.apply("CAST(magnitude AS NUMERIC) >= {0}", Double.valueOf(queryDTO.getStartMagnitude()));
        }
        if (queryDTO.getEndMagnitude() != null && !queryDTO.getEndMagnitude().isEmpty()) {
            QueryWrapper.apply("CAST(magnitude AS NUMERIC) <= {0}", Double.valueOf(queryDTO.getEndMagnitude()));
        }
        if (queryDTO.getStartDepth() != null && !queryDTO.getStartDepth().isEmpty()) {
            // 使用 apply 将 depth 转换为数值类型再进行比较
            QueryWrapper.apply("CAST(depth AS NUMERIC) >= {0}", Double.valueOf(queryDTO.getStartDepth()));
        }
        if (queryDTO.getEndDepth() != null && !queryDTO.getEndDepth().isEmpty()) {
            // 使用 apply 将 depth 转换为数值类型再进行比较
            QueryWrapper.apply("CAST(depth AS NUMERIC) <= {0}", Double.valueOf(queryDTO.getEndDepth()));
        }


        QueryWrapper.orderByDesc(EarthquakeList::getOccurrenceTime);

        return earthquakeListService.list(QueryWrapper);
    }
    @PostMapping("/saveEq")
    @Log(title = "地震信息", businessType = BusinessType.INSERT)
    public boolean saveEq(@RequestBody EarthquakeList earthquakeList) {
        return earthquakeListService.save(earthquakeList);
    }

    @PostMapping("/deleteEq")
    @Log(title = "地震信息", businessType = BusinessType.DELETE)
    public boolean deleteEq(@RequestParam(value = "eqid") String id) {
        return earthquakeListService.removeById(id);
    }

    @PostMapping("/nearby")
    public List<EarthquakeList> getNearbyEarthquakes(@RequestBody GeometryDTO geometryDTO) {
        Point point = geometryDTO.getPoint();
        return earthquakeListService.getEarthquakesWithinDistance(point, 1000.0);
    }

    @PostMapping("/addEq")
    public boolean addEq(@Valid @RequestBody EarthquakeList earthquakeList) {


        // 创建 GeometryFactory
        GeometryFactory geometryFactory = new GeometryFactory();

        // 获取经纬度
        Double longitude = earthquakeList.getLongitude();
        Double latitude = earthquakeList.getLatitude();
        System.out.println("经纬度------------------------------:"+longitude);

        // 检查经纬度是否有效
        if (longitude != null && latitude != null) {
            // 创建 Point 对象
            Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
            // 设置 geom
            earthquakeList.setGeom(point);
        }

        // 保存地震信息
        return earthquakeListService.save(earthquakeList);
    }

    @PostMapping("/updataeq")
    public boolean update(@Valid @RequestBody EarthquakeList earthquakeList) {
        // 创建 GeometryFactory
        GeometryFactory geometryFactory = new GeometryFactory();

        // 获取经纬度
        Double longitude = earthquakeList.getLongitude();
        Double latitude = earthquakeList.getLatitude();

        // 检查经纬度是否有效
        if (longitude != null && latitude != null) {
            // 创建 Point 对象
            Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
            // 设置 geom
            earthquakeList.setGeom(point);
        }

        // 更新地震信息
        return earthquakeListService.updateById(earthquakeList);
    }
}
