package com.ruoyi.web.controller.system;

import com.ruoyi.system.domain.entity.PathPlanning;
import com.ruoyi.system.service.impl.PathPlanningServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/system")
public class PathPlanningController {

    private final PathPlanningServiceImpl pathPlanningService;

    @Autowired
    public PathPlanningController(PathPlanningServiceImpl pathPlanningService) {
        this.pathPlanningService = pathPlanningService;
    }

    @PostMapping("/getRoute")
    public ResponseEntity<Object[]> getRoute(@RequestBody PathPlanning routeRequest) {
        if (routeRequest.getAreas() == null) {
            routeRequest.setAreas(new ArrayList<>()); // 或者设置一个默认值
        }
        System.out.println("Received request: " + routeRequest);
        // 调用服务获取高德地图的处理响应
        System.out.println("from: " + routeRequest.getFrom() + ", end: " + routeRequest.getEnd() +
                ", areas: " + routeRequest.getAreas() + ", key: " + routeRequest.getKey());

        try {
            Object[] responseData = pathPlanningService.getRouteFromAMap(routeRequest);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
