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



//    @PostMapping("/addEq")
//    @Log(title = "新增地震信息", businessType = BusinessType.INSERT)
//    public AjaxResult addEq(@Validated @RequestBody EarthquakeList earthquakeList) {
//        // 新增地震信息
//        boolean success = earthquakeListService.save(earthquakeList);
//        return success ? AjaxResult.success("新增地震信息成功！") : AjaxResult.error("新增地震信息失败！");
//    }
//
//    @PostMapping("/updataeq")
//    @Log(title = "修改地震信息", businessType = BusinessType.UPDATE)
//    public AjaxResult updataeq(@Validated @RequestBody EarthquakeList earthquakeList){
//        boolean success = earthquakeListService.updateById(earthquakeList);
//        return success ? AjaxResult.success("新增地震信息成功！") : AjaxResult.error("新增地震信息失败！");
//    }
//
//    @PostMapping("/deleteEq")
//    @Log(title = "删除地震信息", businessType = BusinessType.DELETE)
//    public AjaxResult deleteEq(@RequestParam("eqid") String eqid){
//        return AjaxResult.success(earthquakeListService.removeById(eqid));
//    }

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
                .or().apply("ST_AsText(geom) LIKE {0}", "%" + queryValue + "%") // 使用 ST_AsText 转换 geom
                .or().apply("to_char(occurrence_time, 'YYYY-MM-DD HH24:MI:SS') LIKE {0}", "%" + queryValue + "%")
                .orderByDesc(EarthquakeList::getOccurrenceTime);
        return earthquakeListService.list(QueryWrapper);

    }

    @PostMapping("/fromEq")
    public List<EarthquakeList> fromEq(@RequestBody EqFormDto queryDTO) {
        LambdaQueryWrapper<EarthquakeList> queryWrapper = new LambdaQueryWrapper<>();

        // 按名称模糊查询
        if (queryDTO.getEarthquakeName() != null && !queryDTO.getEarthquakeName().isEmpty()) {
            queryWrapper.like(EarthquakeList::getEarthquakeName, queryDTO.getEarthquakeName());
        }

        // 筛选 occurrence_time，前端传递了 startTime 和 endTime 时使用
        if (queryDTO.getStartTime() != null && queryDTO.getEndTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            LocalDateTime startTime = LocalDateTime.parse(queryDTO.getStartTime(), formatter);
            LocalDateTime endTime = LocalDateTime.parse(queryDTO.getEndTime(), formatter);
            queryWrapper.between(EarthquakeList::getOccurrenceTime, startTime, endTime);
        }

        // 验证并添加震级筛选条件
        if (isValidNumeric(queryDTO.getStartMagnitude()) && isValidNumeric(queryDTO.getEndMagnitude())) {
            double startMagnitude = Double.parseDouble(queryDTO.getStartMagnitude());
            double endMagnitude = Double.parseDouble(queryDTO.getEndMagnitude());
            if (startMagnitude > endMagnitude) {
                throw new IllegalArgumentException("起始震级必须小于等于结束震级");
            }
            queryWrapper.apply("CAST(magnitude AS NUMERIC) >= {0}", startMagnitude);
            queryWrapper.apply("CAST(magnitude AS NUMERIC) <= {0}", endMagnitude);
        }

        // 验证并添加深度筛选条件
        if (isValidNumeric(queryDTO.getStartDepth()) && isValidNumeric(queryDTO.getEndDepth())) {
            double startDepth = Double.parseDouble(queryDTO.getStartDepth());
            double endDepth = Double.parseDouble(queryDTO.getEndDepth());
            if (startDepth > endDepth) {
                throw new IllegalArgumentException("起始深度必须小于等于结束深度");
            }
            queryWrapper.apply("CAST(depth AS NUMERIC) >= {0}", startDepth);
            queryWrapper.apply("CAST(depth AS NUMERIC) <= {0}", endDepth);
        }

        // 按时间倒序排列
        queryWrapper.orderByDesc(EarthquakeList::getOccurrenceTime);

        return earthquakeListService.list(queryWrapper);
    }
    // 辅助方法，用于检查是否为有效数值
    private boolean isValidNumeric(String value) {
        return value != null && !value.trim().isEmpty() && value.matches("-?\\d+(\\.\\d+)?");
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

        // 震级验证
        String magnitudeStr = earthquakeList.getMagnitude();
        if (magnitudeStr == null || magnitudeStr.isEmpty()) {
            throw new IllegalArgumentException("震级不能为空");
        }
        Double magnitude = null;
        try {
            magnitude = Double.valueOf(magnitudeStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("震级必须为数字");
        }
        if (magnitude < 3 || magnitude > 10) {
            throw new IllegalArgumentException("震级必须在 3 到 10 之间");
        }

        // 深度验证
        String depthStr = earthquakeList.getDepth();
        if (depthStr == null || depthStr.isEmpty()) {
            throw new IllegalArgumentException("深度不能为空");
        }
        Double depth = null;
        try {
            depth = Double.valueOf(depthStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("深度必须为数字");
        }
        if (depth < 0) {
            throw new IllegalArgumentException("深度不能为负数");
        }

        // 经度验证
        Double longitude = earthquakeList.getLongitude();
        if (longitude == null) {
            throw new IllegalArgumentException("经度不能为空");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("经度应在 -180 到 180 之间");
        }

        // 纬度验证
        Double latitude = earthquakeList.getLatitude();
        if (latitude == null) {
            throw new IllegalArgumentException("纬度不能为空");
        }
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("纬度应在 -90 到 90 之间");
        }



        // 创建 GeometryFactory
        GeometryFactory geometryFactory = new GeometryFactory();

//        // 获取经纬度
//        Double longitude = earthquakeList.getLongitude();
//        Double latitude = earthquakeList.getLatitude();
//        System.out.println("经纬度------------------------------:"+longitude);
//
//        // 检查经纬度是否有效
//        if (longitude != null && latitude != null) {
//            // 创建 Point 对象
//            Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
//            // 设置 geom
//            earthquakeList.setGeom(point);
//        }


        // 创建 Point 对象
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        // 设置 geom
        earthquakeList.setGeom(point);

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
