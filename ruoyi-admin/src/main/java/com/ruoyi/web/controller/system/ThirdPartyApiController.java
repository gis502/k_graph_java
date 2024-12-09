package com.ruoyi.web.controller.system;

import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.system.domain.dto.EqEventReassessmentDTO;
import com.ruoyi.web.api.service.SeismicReassessmentService;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.dto.EqEventTriggerDTO;
import com.ruoyi.web.api.service.SeismicTriggerService;
import com.ruoyi.system.domain.dto.EqEventGetMapDTO;
import com.ruoyi.system.domain.dto.EqEventGetReportDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * @author: xiaodemos
 * @date: 2024-11-24 14:15
 * @description: 第三方接口API控制层
 */


@Slf4j
@RestController
@RequestMapping("/tp/api/open")
public class ThirdPartyApiController {

    @Resource
    private SeismicTriggerService seismicTriggerService;
    @Resource
    private SeismicReassessmentService seismicReassessmentService;

    /**
     * @param params 触发的地震数据
     * @author: xiaodemos
     * @date: 2024/12/7 19:35
     * @description: 触发地震
     */
    @PostMapping("/trigger")
    public AjaxResult eqEventTrigger(@RequestBody EqEventTriggerDTO params) {

        seismicTriggerService.seismicEventTrigger(params);

        return AjaxResult.success(MessageConstants.SEISMIC_TRIGGER_SUCCESS);
    }

    /**
     * @param params 触发的地震数据
     * @author: xiaodemos
     * @date: 2024/12/7 19:39
     * @description: 重新启动评估
     */
    @PostMapping("/reassessment")
    public AjaxResult eqEventReassessment(@RequestBody EqEventReassessmentDTO params) {

        seismicReassessmentService.seismicEventReassessment(params);

        return AjaxResult.success(MessageConstants.SEISMIC_REASSESSMENT_SUCCESS);
    }

    /**
     * @param event 地震事件编码
     * @author: xiaodemos
     * @date: 2024/12/7 19:47
     * @description: 获取评估批次的所有数据
     * @return: 返回一场地震的所有批次数据
     */
    @GetMapping("/batch/version")
    public AjaxResult eqEventBatchList(String event) {
        // TODO write your code in here
        return AjaxResult.success();
    }


    /**
     * 获取灾情报告和专题图
     */
    @GetMapping("/getReportandmap")
    public String getReport(@RequestBody EqEventGetReportDTO eqEventGetReportDTO) {
        //需要返回前端我灾情报告的本地储存地址

        return null;
    }

    /**
     * 获取专题图
     */
    @GetMapping("/getReport")
    public String getReport(@RequestBody EqEventGetMapDTO eqEventGetMapDTO) {
        return null;
    }

}
