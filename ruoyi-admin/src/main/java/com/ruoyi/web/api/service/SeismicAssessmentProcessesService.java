package com.ruoyi.web.api.service;

import com.ruoyi.system.domain.dto.ResultEventGetBatchDTO;
import com.ruoyi.system.domain.entity.AssessmentBatch;
import com.ruoyi.system.domain.query.EqEventQuery;
import com.ruoyi.system.domain.vo.ResultEventGetBatchVO;
import com.ruoyi.system.service.impl.AssessmentBatchServiceImpl;
import com.ruoyi.web.api.ThirdPartyCommonApi;
import com.ruoyi.web.core.utils.JsonParser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: xiaodemos
 * @date: 2024-12-14 0:29
 * @description: 获取地震评估结果的进度条，需要做定时任务（5秒），反复更新进度返回给前端。
 */



@Service
public class SeismicAssessmentProcessesService {

    @Resource
    private ThirdPartyCommonApi thirdPartyCommonApi;
    @Resource
    private AssessmentBatchServiceImpl assessmentBatchService;

    public AssessmentBatch getSeismicAssessmentProcesses(EqEventQuery dto) {

        String jsonString = thirdPartyCommonApi.getSeismicEventGetBatchByGet(dto);

        ResultEventGetBatchDTO resultEventGetBatchDTO = JsonParser.parseJson(jsonString, ResultEventGetBatchDTO.class);
        List<ResultEventGetBatchVO> eventGetBatchDTOData = resultEventGetBatchDTO.getData();

        for (ResultEventGetBatchVO resultEventGetBatchVO : eventGetBatchDTOData){
            // 存储到数据库
            assessmentBatchService.updateBatchProgress(
                    dto.getEvent(),
                    dto.getEqqueueId(),
                    resultEventGetBatchVO.getProgress());

        }

        // 直接返回查询批次的进度条
        return assessmentBatchService.selectBatchProgressByEqId(
                dto.getEvent(),
                dto.getEqqueueId()
        );
    }

}
