package com.ruoyi.web.controller.system;

import com.ruoyi.system.service.AftershockInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/system")
public class AftershockInformationController {
    /**
     * 最新余震数据
     */
    @Autowired
    private AftershockInformationService aftershockInformationService;

    @GetMapping("/getLatestAftershockMagnitude")
    public ResponseEntity<Map<String, Integer>> getLatestAftershockData(@RequestParam("eqid") String eqid) {

        // 使用 eqid 获取数据
        Map<String, Integer> aftershockData = aftershockInformationService.getLatestAftershockMagnitude(eqid);

        return ResponseEntity.ok(aftershockData);
    }
}
