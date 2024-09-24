package com.ruoyi.web.controller.system;

import com.ruoyi.system.service.EarthquakeListService;
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


}
