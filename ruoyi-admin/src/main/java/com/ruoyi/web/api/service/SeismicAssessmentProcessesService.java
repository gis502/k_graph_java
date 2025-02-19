package com.ruoyi.web.api.service;

import com.ruoyi.system.domain.dto.ResultEventGetBatchDTO;
import com.ruoyi.system.domain.entity.AssessmentBatch;
import com.ruoyi.system.domain.vo.ResultEventGetBatchVO;
import com.ruoyi.system.service.impl.AssessmentBatchServiceImpl;
import com.ruoyi.web.api.ThirdPartyCommonApi;
import com.ruoyi.web.core.utils.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: xiaodemos
 * @date: 2024-12-14 0:29
 * @description: 获取地震评估结果的进度条，需要做定时任务（5秒），反复更新进度返回给前端。
 */


@Slf4j
@Service
public class SeismicAssessmentProcessesService {

    @Resource
    private ThirdPartyCommonApi thirdPartyCommonApi;
    @Resource
    private AssessmentBatchServiceImpl assessmentBatchService;
    // 获取进度条
    public AssessmentBatch getSeismicAssessmentProcesses(String event) {

        String jsonString = thirdPartyCommonApi.getSeismicEventGetBatchByGet(event);
        if (jsonString == null || jsonString.isEmpty()) {
            log.error("API 返回的 JSON 为空");
            return null; // 或者抛出异常
        }

        ResultEventGetBatchDTO resultEventGetBatchDTO = JsonParser.parseJson(jsonString, ResultEventGetBatchDTO.class);
        if (resultEventGetBatchDTO == null) {
            log.error("JSON 解析失败，返回 null");
            return null; // 或者抛出异常
        }

        List<ResultEventGetBatchVO> eventGetBatchDTOData = resultEventGetBatchDTO.getData();
        if (eventGetBatchDTOData == null || eventGetBatchDTOData.isEmpty()) {
            log.warn("API 返回的 data 为空");
            return null;
        }

        for (ResultEventGetBatchVO resultEventGetBatchVO : eventGetBatchDTOData){

            // 存储到数据库
            assessmentBatchService.updateBatchProgress(
                    event,
                    resultEventGetBatchVO.getProgress());

        }

        // 直接返回查询批次的进度条
        return assessmentBatchService.selectBatchProgressByEqId(event);
    }

}
