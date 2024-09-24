package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.service.EarthquakeListService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("deleteEqById")
    public boolean deleteEqById(@RequestParam(value = "id") String id) {
        return earthquakeListService.removeById(id);
    }
}
