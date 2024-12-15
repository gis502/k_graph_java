package com.ruoyi.web.controller.system;

import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.system.domain.bto.QueryParams;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.entity.AssessmentIntensity;
import com.ruoyi.system.domain.entity.AssessmentOutput;
import com.ruoyi.system.domain.entity.AssessmentResult;
import com.ruoyi.system.domain.entity.EqList;
import com.ruoyi.system.service.impl.AssessmentIntensityServiceImpl;
import com.ruoyi.system.service.impl.AssessmentOutputServiceImpl;
import com.ruoyi.system.service.impl.AssessmentResultServiceImpl;
import com.ruoyi.system.service.impl.EqListServiceImpl;
import com.ruoyi.web.api.service.SeismicReassessmentService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.api.service.SeismicTriggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


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
    public AjaxResult eqEventGetList(@RequestBody QueryParams queryParams) {

        List<ResultEqListDTO> lists = eqListService.eqEventGetList(queryParams);

        return AjaxResult.success(lists);
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
    public AjaxResult eqEventSpecialData(@RequestBody EqEventDTO dto) {

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
    public AjaxResult eqEventIntensityData(@RequestBody EqEventDTO dto) {

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
    public AjaxResult eqEventOutputMapData( @RequestParam("eqid") String eqid,
                                            @RequestParam("eqqueueId") String eqqueueId) {
        System.out.println("eqid");
        System.out.println(eqid);
        EqEventDTO dto = new EqEventDTO();
        dto.setEqId(eqid);
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
        dto.setEqId(eqid);
        dto.setEqqueueId(eqqueueId);
        List<AssessmentOutput> outputs = assessmentOutputService.eqEventOutputReportData(dto);

        return AjaxResult.success(outputs);
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
