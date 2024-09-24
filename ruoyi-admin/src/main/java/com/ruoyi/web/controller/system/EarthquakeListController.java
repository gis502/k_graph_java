package com.ruoyi.web.controller.system;

import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.service.EarthquakeListService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("earthquake")
public class EarthquakeListController {

    @Resource
    private EarthquakeListService earthquakeListService;

    @PostMapping("queryAllEq")
    public List<EarthquakeList> queryAllEq() {
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
