package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.dto.EqEventGetMapDTO;
import com.ruoyi.system.domain.dto.EqEventGetReportDTO;
import com.ruoyi.web.api.service.SeismicTriggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: xiaodemos
 * @date: 2024-11-24 14:15
 * @description: 第三方接口API控制层
 */


@Slf4j
@RestController
@RequestMapping("/tp/api/open")
public class ThirdPartyApiController {
    @Autowired
    private SeismicTriggerService seismicTriggerService;

    //TODO 调用业务层来走接口

    /**
     * 获取灾情报告和专题图
     */
    @GetMapping("/getReportandmap")
    public String getReport(@RequestBody EqEventGetReportDTO eqEventGetReportDTO){
       //需要返回前端我灾情报告的本地储存地址

        return null;
    }

    /**
     * 获取专题图
     */
    @GetMapping("/getReport")
    public String getReport(@RequestBody EqEventGetMapDTO eqEventGetMapDTO){
        return null;
    }

}
