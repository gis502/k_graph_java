package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.AftershockInformation;
import com.ruoyi.system.service.AftershockInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
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
    public ResponseEntity<Map<String, Object>> getLatestAftershockData(@RequestParam("eqid") String eqid) {

        // 使用 eqid 获取数据
        Map<String, Object> aftershockData = aftershockInformationService.getLatestAftershockMagnitude(eqid);

        return ResponseEntity.ok(aftershockData);
    }

    @GetMapping("/getAftershock")
    public ResponseEntity<List<Map<String, Object>>> getTotal(@RequestParam("eqid") String eqid) {

        // 使用 eqid 获取数据
        List<Map<String, Object>> aftershockDataList = aftershockInformationService.getTotal(eqid);

        return ResponseEntity.ok(aftershockDataList);

    }

    //返回离提供时间最近的一条数据
    @GetMapping("/fromAftershock")
    public AjaxResult fromAftershock(@RequestParam("eqid") String eqid,
                                     @RequestParam("time") LocalDateTime time){
        List<Map<String, Object>> aftershockDataList  = aftershockInformationService.fromAftershock(eqid,time);
        return AjaxResult.success(aftershockDataList);
    }



}
