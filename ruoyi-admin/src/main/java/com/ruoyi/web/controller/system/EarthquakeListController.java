package com.ruoyi.web.controller.system;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.CloudWords;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.domain.entity.EqList;
import com.ruoyi.system.domain.vo.ResultEqListVO;
import com.ruoyi.system.mapper.CloudWordsMapper;
import com.ruoyi.system.mapper.EqListMapper;
import com.ruoyi.system.service.CloudWordsService;
import com.ruoyi.system.service.EarthquakeListService;
import com.ruoyi.system.service.IEqListService;
import com.ruoyi.system.service.impl.CloudWordsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.ruoyi.common.enums.BusinessType;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Validated
@RestController
@RequestMapping("/system")
public class EarthquakeListController {
    @Resource
    private EarthquakeListService earthquakeListService;

    @Resource
    private IEqListService eqListService;
    @Resource
    private EqListMapper eqListMapper;

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private CloudWordsMapper cloudWordsService;

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
//    @PostMapping("getEqListById")
//    public EqList queryEqListById(@RequestParam(value = "id") String id) {
//        return eqListService.getById(id);
//    }

    @PostMapping("getEqListById")
    public ResultEqListDTO getEqListById(@RequestParam(value = "id") String id) {
        // Create a wrapper for querying the EqList by id (assuming eqid is the unique identifier)
        LambdaQueryWrapper<EqList> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EqList::getEqid, id); // Filter by eqid

        // Fetch the EqList record based on the wrapper conditions
        EqList record = eqListMapper.selectOne(wrapper);

        // Initialize the DTO object
        ResultEqListDTO resultEqListDTO = ResultEqListDTO.builder().build();

        // If record is not null, map the data to the DTO
        if (record != null) {
            Geometry geom = record.getGeom();
            double longitude = geom.getCoordinate().x;
            double latitude = geom.getCoordinate().y;

            resultEqListDTO = ResultEqListDTO.builder()
                    .longitude(longitude)
                    .latitude(latitude)
                    .depth(record.getDepth())
                    .eqAddrCode(record.getEqAddrCode())
                    .source(record.getSource())
                    .eqType(record.getEqType())
                    .occurrenceTime(String.valueOf(record.getOccurrenceTime()))
                    .pac(record.getPac())
                    .earthquakeFullName(record.getEarthquakeFullName())
                    .earthquakeName(record.getEarthquakeName())
                    .eqAddr(record.getEqAddr())
                    .eqid(record.getEqid())
                    .eqqueueId(record.getEqqueueId())
                    .intensity(record.getIntensity())
                    .magnitude(record.getMagnitude())
                    .townCode(record.getTownCode())
                    .type(record.getType())
                    .build();
        }

        // Return the DTO object
        return resultEqListDTO;
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

    @GetMapping("/queryEqList")
    public List<EqList> queryEqList(@RequestParam(value = "queryValue", required = false) String queryValue) {
        System.out.println(queryValue);
        LambdaQueryWrapper<EqList> QueryWrapper = new LambdaQueryWrapper<>();
        QueryWrapper.like(EqList::getEarthquakeName, queryValue)
                .or().like(EqList::getMagnitude, queryValue)
                .or().like(EqList::getDepth, queryValue)
                .or().apply("ST_AsText(geom) LIKE {0}", "%" + queryValue + "%") // 使用 ST_AsText 转换 geom
                .or().apply("to_char(occurrence_time, 'YYYY-MM-DD HH24:MI:SS') LIKE {0}", "%" + queryValue + "%")
                .orderByDesc(EqList::getOccurrenceTime);
        return eqListService.list(QueryWrapper);
    }

    public static OffsetDateTime convertToOffsetDateTime(String timeStr) {
        // 使用 ISO_DATE_TIME 来解析带时区的时间字符串（包含 Z 表示 UTC）
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        // 解析为 OffsetDateTime 类型，这样能够正确处理时区（即 'Z' 表示 UTC）
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(timeStr, formatter);

        // 返回原始的 OffsetDateTime，保持时间的 UTC 时区
        return offsetDateTime;
    }


    //筛选
    @PostMapping("/fromEq")
    public List<EarthquakeList> fromEq(@RequestBody EqFormDto queryDTO) {
        LambdaQueryWrapper<EarthquakeList> queryWrapper = new LambdaQueryWrapper<>();

        System.out.println("55555555555555" + queryDTO);
        // 按名称模糊查询
        if (queryDTO.getEarthquakeName() != null && !queryDTO.getEarthquakeName().isEmpty()) {
            queryWrapper.like(EarthquakeList::getEarthquakeName, queryDTO.getEarthquakeName());
        }

        // 筛选 occurrence_time，前端传递了 startTime 和 endTime 时使用
        if (queryDTO.getStartTime() != null && queryDTO.getEndTime() != null) {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//
//            // 直接将前端传递的时间字符串解析为 OffsetDateTime（保留时区信息，处理 UTC）
//            OffsetDateTime startTime = convertToOffsetDateTime(queryDTO.getStartTime());
//            OffsetDateTime endTime = convertToOffsetDateTime(queryDTO.getEndTime());


            //做了时区转换，向前一天   eg:31---->30
//            LocalDateTime startTime = LocalDateTime.parse(queryDTO.getStartTime(), formatter);
//            LocalDateTime endTime = LocalDateTime.parse(queryDTO.getEndTime(), formatter);

            queryWrapper.between(EarthquakeList::getOccurrenceTime, queryDTO.getStartTime(), queryDTO.getEndTime());
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

    @PostMapping("/fromEqList")
    public List<EqList> fromEqList(@RequestBody EqFormDto queryDTO) {
        LambdaQueryWrapper<EqList> queryWrapper = new LambdaQueryWrapper<>();

        System.out.println("55555555555555" + queryDTO);
        // 按名称模糊查询
        if (queryDTO.getEarthquakeName() != null && !queryDTO.getEarthquakeName().isEmpty()) {
            queryWrapper.like(EqList::getEarthquakeName, queryDTO.getEarthquakeName());
        }

        // 筛选 occurrence_time，前端传递了 startTime 和 endTime 时使用
        if (queryDTO.getStartTime() != null && queryDTO.getEndTime() != null) {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//
//            // 直接将前端传递的时间字符串解析为 OffsetDateTime（保留时区信息，处理 UTC）
//            OffsetDateTime startTime = convertToOffsetDateTime(queryDTO.getStartTime());
//            OffsetDateTime endTime = convertToOffsetDateTime(queryDTO.getEndTime());


            //做了时区转换，向前一天   eg:31---->30
//            LocalDateTime startTime = LocalDateTime.parse(queryDTO.getStartTime(), formatter);
//            LocalDateTime endTime = LocalDateTime.parse(queryDTO.getEndTime(), formatter);

            queryWrapper.between(EqList::getOccurrenceTime, queryDTO.getStartTime(), queryDTO.getEndTime());
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
        queryWrapper.orderByDesc(EqList::getOccurrenceTime);

        return eqListService.list(queryWrapper);
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


    @PostMapping("/trigger")
    public AjaxResult trigger(@RequestBody TriggerDTO triggerDTO) {
        try {
            eqListService.trigger(triggerDTO);
            return AjaxResult.success("地震启动成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("地震启动失败");
        }
    }

    @PostMapping("/autoTrigger")
    public AjaxResult autoTrigger() {
        try {
            eqListService.autoTrigger();
            return AjaxResult.success("地震自动启动成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("地震自动启动失败");
        }
     }

    @PostMapping("/addNewEq")
    public void addNewEq(@RequestBody EqEventTriggerDTO eqEventTriggerDTO) {
        eqListService.addNewEq(eqEventTriggerDTO);
    }

    @PostMapping("/addEq")
    public AjaxResult addEq(@Valid @RequestBody EarthquakeList earthquakeList) {
        earthquakeList.generateUuidIfNotPresent();
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
        // 保留震级小数点后 1 位
        BigDecimal magnitudeBigDecimal = new BigDecimal(magnitude);
        magnitudeBigDecimal = magnitudeBigDecimal.setScale(1, RoundingMode.HALF_UP); // 保留 1 位小数

        earthquakeList.setMagnitude(magnitudeBigDecimal.toString()); // 设置格式化后的震级

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
        return AjaxResult.success(earthquakeListService.save(earthquakeList));
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

    @GetMapping("getGeomById")
    public List<EarthquakeList> getGeomById(@RequestParam(value = "id") String id) {
        return earthquakeListService.getGeomById(id);
    }

    @GetMapping("getGeomByEqListId")
    public List<EqList> getGeomByEqListId(@RequestParam(value = "id") String id) {
        QueryWrapper<EqList> eqListQueryWrapper = new QueryWrapper<>();
        eqListQueryWrapper.eq("eqid", id);
        return eqListService.list(eqListQueryWrapper);
    }

    //关于态势标会5.0级以上的模糊查询
    @GetMapping("/queryName")
    public AjaxResult queryName(@RequestParam(value = "inputData", required = false) String inputData) {
        LambdaQueryWrapper<EqList> wrapper = new LambdaQueryWrapper<>();
        if (inputData != null && !inputData.trim().isEmpty()) {
            wrapper
                    .like(EqList::getEarthquakeName, inputData)
                    .or()
                    .like(EqList::getMagnitude, inputData)
                    .or()
                    .apply("TO_CHAR(occurrence_time, 'YYYY-MM-DD HH24:MI:SS') LIKE {0}", "%" + inputData + "%");
        }
        List<EqList> resultList = eqListService.list(wrapper);
        return AjaxResult.success(resultList);
    }



    @GetMapping("/cloudword")
    public AjaxResult getRecentlyEqCloud(@RequestParam("eqid") String eqid) {

         QueryWrapper wrapper = new QueryWrapper();
         wrapper.eq("eqid", eqid);
        return AjaxResult.success(cloudWordsService.selectList(wrapper));
    }
    public static <T> T parseJson(String jsonBody, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonBody, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
