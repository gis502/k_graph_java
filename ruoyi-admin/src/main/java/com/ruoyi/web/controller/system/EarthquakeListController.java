package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.service.EarthquakeListService;
import com.ruoyi.web.controller.common.EqQueryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/system")
public class EarthquakeListController {
    @Autowired
    private EarthquakeListService earthquakeListService;

    @PostMapping("/addEq")
    public int addPlotIcon(@RequestBody EarthquakeList eq) {
        int data = earthquakeListService.addEq(eq);
        return data;
    }

    @GetMapping("/deleteEq")
    public int deleteEq(String eqid) {
        int data = earthquakeListService.deleteEq(eqid);
        return data;
    }

    @PostMapping("/updateEq")
    public int updateEq(@RequestBody EarthquakeList eq) {
        int data = earthquakeListService.updateEq(eq);
        return data;
    }

    @GetMapping("/getEq")
    public List<EarthquakeList> selectAllEq() {
        List<EarthquakeList> data = earthquakeListService.selectAllEq();
        return data;
    }

    @GetMapping("/queryEq")
    public List<EarthquakeList> queryEq(@RequestParam(value = "queryValue", required = false) String queryValue) {
        LambdaQueryWrapper<EarthquakeList> QueryWrapper = new LambdaQueryWrapper<>();
        QueryWrapper.like(EarthquakeList::getEarthquakeName, queryValue)
                .or().like(EarthquakeList::getMagnitude, queryValue)
                .or().like(EarthquakeList::getDepth, queryValue)
                .or().apply("to_char(time, 'YYYY-MM-DD HH24:MI:SS') LIKE {0}", "%" + queryValue + "%")
                .orderByDesc(EarthquakeList::getOccurrenceTime);
        return earthquakeListService.list(QueryWrapper);
    }

    @PostMapping("/fromEq")
    public List<EarthquakeList> fromEq(@RequestBody EqQueryDTO queryDTO) {
        LambdaQueryWrapper<EarthquakeList> QueryWrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getPosition() != null && !queryDTO.getPosition().isEmpty()) {
            QueryWrapper.like(EarthquakeList::getEarthquakeName, queryDTO.getPosition());
        }
        if (queryDTO.getTime() != null && !queryDTO.getTime().isEmpty()) {
            // 解析时间范围字符串
            String[] timeRange = queryDTO.getTime().split(" 至 ");
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


    @GetMapping("/getKeyEq")
    public List<EarthquakeList> selectKeyEq() {
        List<EarthquakeList> data = earthquakeListService.selectKeyEq();
        return data;
    }

    @GetMapping("/getLatesteq")
    public List<EarthquakeList> selectLatestEq() {
        List<EarthquakeList> data = earthquakeListService.selectLatestEq();
        return data;
    }

    //获取完整的地震列表
    @GetMapping("/getExcelUploadEarthquake")
    public List<String> selectearthquakeList() {
        List<String> data = earthquakeListService.getExcelUploadEarthquake();
        return data;
    }

    @PostMapping("/geteqbyid")
    public EarthquakeList selectEqByID(@RequestParam String eqid) {
        QueryWrapper<EarthquakeList> earthquakeListQueryWrapper = new QueryWrapper<>();
        earthquakeListQueryWrapper.eq("eqid", eqid);
        EarthquakeList one = earthquakeListService.getOne(earthquakeListQueryWrapper);
        return one;
    }

}
