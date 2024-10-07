package com.ruoyi.web.controller.system;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.system.domain.dto.EqFormDto;
import com.ruoyi.system.domain.dto.GeometryDTO;
import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.service.EarthquakeListService;
import org.locationtech.jts.geom.Point;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.ruoyi.common.enums.BusinessType;

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

//    @GetMapping("/geteq")
//    public List<EarthquakeList> selectAllEq() {
////        List<EqList> data = eqListService.list();
//        List<EarthquakeList> data = earthquakeListService.selectAllEq();
//        return data;
//    }

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
        LambdaQueryWrapper<EarthquakeList> QueryWrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getEarthquakeName() != null && !queryDTO.getEarthquakeName().isEmpty()) {
            QueryWrapper.like(EarthquakeList::getEarthquakeName, queryDTO.getEarthquakeName());
        }
        if (queryDTO.getOccurrenceTime() != null && !queryDTO.getOccurrenceTime().isEmpty()) {
            // 解析时间范围字符串
            String[] timeRange = queryDTO.getOccurrenceTime().split(" 至 ");
            if (timeRange.length == 2) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime startTime = LocalDateTime.parse(timeRange[0], formatter);
                    LocalDateTime endTime = LocalDateTime.parse(timeRange[1], formatter);

                    QueryWrapper.between(EarthquakeList::getOccurrenceTime, startTime, endTime);
                } catch (Exception e) {
                    // 处理解析错误
                    e.printStackTrace();
                }
            }
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


    @PostMapping("saveEq")
    @Log(title = "地震信息", businessType = BusinessType.INSERT)
    public boolean saveEq(@RequestBody EarthquakeList earthquakeList) {
        return earthquakeListService.save(earthquakeList);
    }

    @RequestMapping("deleteEqById")
    @Log(title = "地震信息", businessType = BusinessType.DELETE)
    public boolean deleteEqById(@RequestParam(value = "id") String id) {
        return earthquakeListService.removeById(id);
    }

    @PostMapping("/nearby")
    public List<EarthquakeList> getNearbyEarthquakes(@RequestBody GeometryDTO geometryDTO) {
        Point point = geometryDTO.getPoint();
        return earthquakeListService.getEarthquakesWithinDistance(point, 1000.0);
    }


}
