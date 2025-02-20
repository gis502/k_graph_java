package com.ruoyi.web.api.service;

import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.common.exception.DeleteDataException;
import com.ruoyi.common.exception.ThirdPartyApiException;
import com.ruoyi.system.domain.query.EqEventQuery;
import com.ruoyi.system.service.impl.*;
import com.ruoyi.web.api.ThirdPartyCommonApi;
import com.ruoyi.web.core.utils.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: xiaodemos
 * @date: 2024-12-10 9:15
 * @description: 删除第三方接口地震数据
 */

@Slf4j
@Service
public class SeismicDeletedService {
    @Resource
    private ThirdPartyCommonApi thirdPartyCommonApi;
    @Resource
    private AssessmentBatchServiceImpl assessmentBatchService;
    @Resource
    private AssessmentIntensityServiceImpl assessmentIntensityService;
    @Resource
    private AssessmentOutputServiceImpl assessmentOutputService;
    @Resource
    private AssessmentResultServiceImpl assessmentResultService;
    @Resource
    private EqListServiceImpl eqListService;

    private Boolean batchFlag = false, intensityFlag = false,
            outputFlag = false, townResultFlag = false, eqListFlag = false;

    /**
     * @param params 批次编码和事件编码
     * @author: xiaodemos
     * @date: 2024/12/10 9:21
     * @description: 同步删除第三方数据库和本地数据库的数据
     * @return: 返回是否删除成功
     */
    public Boolean SeismicEventDelete(EqEventQuery params) {

        String flag = thirdPartyCommonApi.getSeismicEventGetDeleteByPost(params);
        boolean isDeleted = JsonParser.parseJsonToBooleanField(flag);

        if (!isDeleted) {
            throw new ThirdPartyApiException(MessageConstants.SEISMIC_DELETED_ERROR);
        }

        try {

            batchFlag = assessmentBatchService.deletedBatchData(params.getEvent());
            intensityFlag = assessmentIntensityService.deletedIntensityData(params.getEvent());
            outputFlag = assessmentOutputService.deletedOutputData(params.getEvent());
            townResultFlag = assessmentResultService.deletedTownResultData(params.getEvent());
            eqListFlag = eqListService.deletedEqListData(params.getEvent());

            return batchFlag && intensityFlag && outputFlag && townResultFlag && eqListFlag;

        } catch (DeleteDataException e) {
            e.printStackTrace();
            throw new DeleteDataException(MessageConstants.SEISMIC_DELETED_ERROR);
        }

    }

}
