package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.impl.WorkGroupLogServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/system")
@RestController
public class WorkGroupLogController {

    @Resource
    private WorkGroupLogServiceImpl workGroupLogService;

    @GetMapping("/getWorkGroupInfo")
    public AjaxResult getWorkGroupLogs(@RequestParam String eqid) {

        List<Map<String, Object>> areaUploadData = workGroupLogService.getAreaUploadData(eqid);
        List<Map<String, Object>> workGroupData = workGroupLogService.getWorkGroupData(eqid);
        String lastDeadlineDateTime = workGroupLogService.getLastDeadlineDateTime(eqid);

        Map<String, Object> hashMap = new HashMap<>();

        hashMap.put("areaUploadData", areaUploadData);

        hashMap.put("workGroupData", workGroupData);
        hashMap.put("lastTime", lastDeadlineDateTime);

        AjaxResult wordCloudLib = getWordCloudLib(eqid);

        System.out.println("wordCloudLib = " + wordCloudLib);

        return AjaxResult.success(hashMap);

    }

    @GetMapping("/getWordCloudLib")
    public AjaxResult getWordCloudLib(@RequestParam String eqId) {


        workGroupLogService.getWordCloudLib(eqId);


        return AjaxResult.success();

    }

}
