package com.ruoyi.web.controller.system;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.system.domain.bto.QueryParams;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.domain.query.EqEventQuery;
import com.ruoyi.system.service.impl.AssessmentIntensityServiceImpl;
import com.ruoyi.system.service.impl.AssessmentOutputServiceImpl;
import com.ruoyi.system.service.impl.AssessmentResultServiceImpl;
import com.ruoyi.system.service.impl.EqListServiceImpl;
import com.ruoyi.web.api.ThirdPartyCommonApi;
import com.ruoyi.web.api.service.SeismicAssessmentProcessesService;
import com.ruoyi.web.api.service.SeismicDeletedService;
import com.ruoyi.web.api.service.SeismicReassessmentService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.api.service.SeismicTriggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;


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
    @Resource
    private AssessmentResultServiceImpl assessmentResultService;
    @Resource
    private AssessmentIntensityServiceImpl assessmentIntensityService;
    @Resource
    private AssessmentOutputServiceImpl assessmentOutputService;
    @Resource
    private EqListServiceImpl eqListService;
    @Resource
    private SeismicAssessmentProcessesService assessmentProcessesService;
    @Resource
    private SeismicDeletedService seismicDeletedService;

    /**
     * @param params 触发的地震数据
     * @author: xiaodemos
     * @date: 2024/12/7 19:35
     * @description: 触发地震
     */
    @PostMapping("/trigger")
    public AjaxResult eqEventTrigger(@RequestBody EqEventTriggerDTO params) {
        System.out.println(params);

        CompletableFuture<Void> future = seismicTriggerService.seismicEventTrigger(params);

        log.info("触发地震异步执行成功 {}", future);

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

        CompletableFuture<Void> future = seismicReassessmentService.seismicEventReassessment(params);

        log.info("重新评估地震异步执行成功 {}", future);

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
    public AjaxResult eqEventBatchList(@RequestBody String event) {
        // TODO write your code in here
        return AjaxResult.success();
    }

    /**
     * @author: xiaodemos
     * @date: 2024/12/10 23:05
     * @description: 分页查询地震列表数据
     * @return: 返回eqlist表的所有数据
     */
    @GetMapping("/eq/list")
    public AjaxResult eqEventGetList() {

        List<ResultEqListDTO> lists = eqListService.eqEventGetList();

        return AjaxResult.success(lists);
    }

    @GetMapping("/getExcelUploadEqList")
        public List<String> getExcelUploadEqList() {
            List<String> data = eqListService.getExcelUploadEqList();
            return data;
        }

    /**
     * @param dto 前端查询的参数
     * @author: xiaodemos
     * @date: 2024/12/12 17:42
     * @description: 查询某一场地震的详细信息
     * @return: 返回一场地震的信息
     */
    @GetMapping("/eq/info")
    public AjaxResult eqEventGetDetailsInfo(@RequestBody EqEventDTO dto) {

        ResultEqListDTO resultEqListDTO = eqListService.eqEventGetDetailsInfo(dto);

        return AjaxResult.success(resultEqListDTO);
    }

    /**
     * @param dto 前端查询的参数
     * @author: xiaodemos
     * @date: 2024/12/12 20:38
     * @description: 批量查询专题图数据
     * @return: 返回专题数据（人员伤亡、经济损失、建筑破坏）的评估结果
     */
    @GetMapping("/eq/assessment")
    public AjaxResult eqEventSpecialData(EqEventDTO dto) {

        List<AssessmentResult> results = assessmentResultService.eqEventSpecialData(dto);

        return AjaxResult.success(results);
    }

    /**
     * @param dto 前端查询的参数
     * @author: xiaodemos
     * @date: 2024/12/12 20:47
     * @description: 根据Id查询影响场的一条数据
     * @return: 返回（单体查询）影响场（烈度圈）的数据
     */
    @GetMapping("/eq/intensity")
    public AjaxResult eqEventIntensityData(EqEventDTO dto) {

        AssessmentIntensity intensity = assessmentIntensityService.eqEventIntensityData(dto);

        return AjaxResult.success(intensity);
    }

    /**
     * @param dto 前端查询的参数
     * @author: xiaodemos
     * @date: 2024/12/12 20:56
     * @description: 批量查询专题图和灾情报告
     * @return: （批量查询）专题图与灾情报告
     */
    @GetMapping("/eq/output/map")
    public AjaxResult eqEventOutputMapData(@RequestParam("eqid") String eqid,
                                           @RequestParam("eqqueueId") String eqqueueId) {
        System.out.println("eqid");
        System.out.println(eqid);
        EqEventDTO dto = new EqEventDTO();
        dto.setEqid(eqid);
        dto.setEqqueueId(eqqueueId);
        List<AssessmentOutput> outputs = assessmentOutputService.eqEventOutputMapData(dto);

        return AjaxResult.success(outputs);
    }

    /**
     * @param dto 前端查询的参数
     * @author: xiaodemos
     * @date: 2024/12/12 21:11
     * @description: 批量查询专题图和灾情报告
     * @return: （批量查询）专题图与灾情报告
     */
    @GetMapping("/eq/output/report")
    public AjaxResult eqEventOutputReportData(@RequestParam("eqid") String eqid,
                                              @RequestParam("eqqueueId") String eqqueueId) {
        EqEventDTO dto = new EqEventDTO();
        dto.setEqid(eqid);
        dto.setEqqueueId(eqqueueId);
        List<AssessmentOutput> outputs = assessmentOutputService.eqEventOutputReportData(dto);

        return AjaxResult.success(outputs);
    }

    /**
     * @param event 地震事件编码
     * @author: xiaodemos
     * @date: 2024/12/19 17:57
     * @description: 获取评估中的进度条
     * @return: 返回评估结果的进度条
     */

    @GetMapping("/eq/processes")
    public AjaxResult eqEventGetProcessesData(@RequestParam("eqid") String event) {

        AssessmentBatch batch = assessmentProcessesService.getSeismicAssessmentProcesses(event);

        return AjaxResult.success(batch.getProgress());

    }

    /**
     * @param eqid 事件编码
     * @author: xiaodemos
     * @date: 2024/12/19 17:55
     * @description: 对eqlist和earthquakelist表进行删除
     */
    @PostMapping("/eq/delete")
    public AjaxResult eqEventDeleted(@RequestParam(value = "eqid") String eqid) {

        EqEventQuery query = EqEventQuery.builder()
                .event(eqid)
                .build();

        seismicDeletedService.SeismicEventDelete(query);

        return AjaxResult.success(MessageConstants.SEISMIC_DELETED_SUCCESS);
    }

    /**
     * @author: xiaodemos
     * @date: 2024/12/12 17:15
     * @description: 拼接成 日期 地震 震级 的字符串
     * @return: 返回拼接的数据
     */
    public AjaxResult eqEventGetSelectData() {

        // eqListService.eqEventGetSelectData();

        return AjaxResult.success();
    }

}
