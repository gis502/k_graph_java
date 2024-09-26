package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.dto.GeometryDTO;
import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.service.EarthquakeListService;
import org.locationtech.jts.geom.Point;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/system")
public class EarthquakeListController {
    @Resource
    private EarthquakeListService earthquakeListService;

    @GetMapping("/getExcelUploadEarthquake")
    public List<String> selectEqList() {
        List<String> data = earthquakeListService.getExcelUploadEarthquake();
        return data;
    }

    @PostMapping("queryAllEq")
    public List<EarthquakeList> queryAllEq() {
        return earthquakeListService.list();
    }

    @GetMapping("getLatesteq")
    public List<EarthquakeList> getLatesteq() {
        return earthquakeListService.list();
    }

    @PostMapping("queryEqById")
    public EarthquakeList queryEqById(@RequestParam(value = "id") String id) {
        return earthquakeListService.getById(id);
    }

    @PostMapping("saveEq")
    public boolean saveEq(@RequestBody EarthquakeList earthquakeList) {
        return earthquakeListService.save(earthquakeList);
    }

    @RequestMapping("deleteEqById")
    public boolean deleteEqById(@RequestParam(value = "id") String id) {
        return earthquakeListService.removeById(id);
    }

    @PostMapping("/nearby")
    public List<EarthquakeList> getNearbyEarthquakes(@RequestBody GeometryDTO geometryDTO) {
        Point point = geometryDTO.getPoint();
        return earthquakeListService.getEarthquakesWithinDistance(point, 1000.0);
    }


}
