package com.ruoyi.web.api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jhlabs.image.LifeFilter;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.common.exception.*;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.entity.AssessmentBatch;
import com.ruoyi.system.domain.entity.AssessmentIntensity;
import com.ruoyi.system.domain.entity.AssessmentResult;
import com.ruoyi.system.domain.entity.EqList;
import com.ruoyi.system.domain.query.EqEventQuery;
import com.ruoyi.system.domain.vo.ResultEventGetPageVO;
import com.ruoyi.system.domain.vo.ResultEventGetResultTownVO;
import com.ruoyi.system.service.impl.*;
import com.ruoyi.web.api.ThirdPartyCommonApi;
import com.ruoyi.web.core.utils.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author: xiaodemos
 * @date: 2024-12-04 14:50
 * @description: 地震重新评估实现类
 */
@Slf4j
@Service
public class SeismicReassessmentService {

    @Resource
    private ThirdPartyCommonApi thirdPartyCommonApi;
    @Resource
    private EqListServiceImpl eqListService;
    @Resource
    private AssessmentResultServiceImpl assessmentResultService;
    @Resource
    private AssessmentBatchServiceImpl assessmentBatchService;
    @Resource
    private AssessmentIntensityServiceImpl assessmentIntensityService;
    @Resource
    private AssessmentOutputServiceImpl assessmentOutputService;
    @Resource
    private SeismicDeletedService seismicDeletedService;

    private boolean asyncIntensity = false, asyncTown = false, asyncOutputMap = false, asyncOutputReport = false;
    @Resource
    private SeismicAssessmentProcessesService assessmentProcessesService;

    @Async // 参数改为 EqEventReassessmentDTO params
    public CompletableFuture<Void> seismicEventReassessment(EqEventReassessmentDTO params) {

        String eqqueueId = null;

        try {

            System.out.println("修改参数 {} ->" + params);

            // 查询批次表的当前批次
            Integer batchVersion = assessmentBatchService.gainBatchVersion(params);

            if (batchVersion == null) {
                throw new ResultNullPointException(MessageConstants.RETURN_IS_EMPTY);
            }

            // 对原有的数据进行删除，重新评估
            EqEventQuery query = EqEventQuery.builder()
                    .event(params.getEvent())
                    .build();

            Boolean deleted = seismicDeletedService.SeismicEventReassessment(query);
            if (!deleted) {
                throw new ParamsIsEmptyException(MessageConstants.SEISMIC_DELETED_ERROR);
            }

            // 把前端上传的数据保存到第三方数据库中
            eqqueueId = handleThirdPartySeismicReassessment(params);
            eqqueueId = JsonParser.parseJsonToEqQueueId(eqqueueId);

            // 如果返回的结果是一个空字符串，表示数据已经插入成功，否则抛出异常，事务回滚
            if (StringUtils.isEmpty(eqqueueId)) {
                throw new ParamsIsEmptyException(MessageConstants.SEISMIC_TRIGGER_ERROR);
            }

            // 数据插入到第三方数据库成功后，插入到本地数据库
            getWithSave(params, eqqueueId, batchVersion);

            // 异步进行地震影响场灾损评估
            handleSeismicYxcEventAssessment(params, eqqueueId);

            // 异步进行乡镇级评估
            handleTownLevelAssessment(params, eqqueueId);

            // 检查评估结果的数据是否成功
            retrySaving(params, eqqueueId);

            // 返回每个阶段的保存数据状态
            return CompletableFuture.completedFuture(null);

        } catch (Exception ex) {
            // 如果事务回滚，执行补偿机制，重新保存到第三方接口
            if (eqqueueId != null) {

                retryThirdPartySave(eqqueueId, params); // 调用补偿机制
            }
            throw ex;   // 抛出异常进行回滚
        }
    }

    /**
     * @param eqqueueId 触发地震接口返回的eqqueueId
     * @param params    触发接口时上传的数据
     * @date: 2024/12/10 8:42
     * @description: 对存库失败进行重试操作，确保每个阶段的存库都正常
     */
    private void retrySaving(EqEventReassessmentDTO params, String eqqueueId) {

        if (!asyncIntensity) {
            log.info("正在重试保存地震影响场数据...");
            handleSeismicYxcEventAssessment(params, eqqueueId);  // 对地震影响场的灾损评估数据进行保存重试
        }

        if (!asyncTown) {
            log.info("正在重试保存乡镇级评估数据...");
            handleTownLevelAssessment(params, eqqueueId);       // 对乡镇级的灾损评估数据进行保存重试
        }

        updateEventState(params.getEvent(), eqqueueId, 2);    // 修改批次表中的地震状态

    }


    /**
     * @param eqqueueId 触发地震接口返回的eqqueueId
     * @param params    触发接口时上传的数据
     * @author: xiaodemos
     * @date: 2024/12/4 18:05
     * @description: 补偿机制：当事务回滚后需要重新执行第三方接口数据保存操作
     */
    private void retryThirdPartySave(String eqqueueId, EqEventReassessmentDTO params) {
        try {
            // 根据 eqqueueId 或其他标识判断是否需要重新插入数据
            handleThirdPartySeismicReassessment(params); // 重新调用第三方接口
            log.info("Successfully retried third-party save for eqqueueId: {}", eqqueueId);
        } catch (Exception ex) {
            log.error("Error retrying third-party save for eqqueueId: {}", eqqueueId, ex);
            // 可以根据需求进行重试次数限制或其他补偿处理
        }
    }

    /**
     * @param params 触发地震时的数据
     * @author: xiaodemos
     * @date: 2024/12/4 18:09
     * @description: 调用第三方地震重新评估接口
     * @return: 返回eqqueueid
     */
    private String handleThirdPartySeismicReassessment(EqEventReassessmentDTO params) {

        try {

            return thirdPartyCommonApi.getSeismicEventReassessmentByPost(params);

        } catch (Exception e) {

            e.printStackTrace();

            throw new ThirdPartyApiException(MessageConstants.THIRD_PARTY_API_ERROR);
        }
    }


    /**
     * @param params    上传的参数
     * @param filePath  返回的影响场文件路径
     * @param eqqueueId 评估批次编码
     * @author: xiaodemos
     * @date: 2024/12/2 23:17
     * @description: 保存地震影响场的灾损评估结果到数据库
     */
    public void saveIntensity(EqEventReassessmentDTO params, String filePath, String eqqueueId, String fileType) {

        AssessmentIntensity assessmentIntensity = AssessmentIntensity.builder()
                .id(UUID.randomUUID().toString())
                .eqqueueId(eqqueueId)
                .batch("1")
                .file(filePath)
                .eqid(params.getEvent())
                .fileType(fileType)
                .localFile(Constants.PROMOTION_URL_HEAD + filePath)
                .build();

        asyncIntensity = assessmentIntensityService.save(assessmentIntensity);
    }

    /**
     * @param eqqueueId 查询触发的那条地震
     * @author: xiaodemos
     * @date: 2024/11/26 17:07
     * @description: 需要把字段转换成保存数据到我们的数据库中
     */
    public void getWithSave(EqEventReassessmentDTO params, String eqqueueId, int batchVersion) {

        AssessmentBatch batch = AssessmentBatch.builder()
                .eqqueueId(eqqueueId)
                .eqid(params.getEvent())
                .batch(batchVersion + 1)
                .state(0)
                .type("1")
                .progress(0.0)
                .remark("")
                .build();

        boolean flag = assessmentBatchService.save(batch);

        if (!flag) {
            throw new DataSaveException(MessageConstants.DATA_SAVE_FAILED);
        }

        log.info("重新评估的数据已经同步到批次表中 -> : ok");

        EqEventGetPageDTO dto = EqEventGetPageDTO.builder().event(params.getEvent()).build();

        String seismicEvent = thirdPartyCommonApi.getSeismicEventByGet(dto);

        log.info("解析的json字符串 seismicEvent -> : {}", seismicEvent);

        //转换为JSONObject
        ResultEventGetPageDTO parsed = JsonParser.parseJson(seismicEvent, ResultEventGetPageDTO.class);
        ResultEventGetPageVO resultEventGetPageVO = parsed.getData().getRows().get(0);

        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(params.getLongitude(), params.getLatitude()));

        // TODO 修改数据库字段与dto保持一致可以优化这段代码
        EqList eqList = EqList.builder()
                .eqid(resultEventGetPageVO.getEvent())
                .eqqueueId(eqqueueId)
                .earthquakeName(resultEventGetPageVO.getEqName())
                .earthquakeFullName(resultEventGetPageVO.getEqFullName())
                .geom(point)
                .depth(resultEventGetPageVO.getEqDepth().toString())
                .magnitude(resultEventGetPageVO.getEqMagnitude())
                .isDeleted(0)
                .build();

        eqListService.updateEqList(eqList);

        log.info("修改地震信息成功");

    }

    /**
     * @param params    触发地震时上传的数据
     * @param eqqueueId 触发地震时返回的eqqueueid
     * @author: xiaodemos
     * @date: 2024/12/4 18:10
     * @description: 进行地震影响场的灾损评估
     */
    @Async
    public CompletableFuture<Void> handleSeismicYxcEventAssessment(EqEventReassessmentDTO params, String eqqueueId) {

        assessmentBatchService.updateBatchState(params.getEvent(), eqqueueId, 1);    // 修改状态正在执行评估中...

        try {
            EqEventGetYxcDTO eventGetYxcDTO = EqEventGetYxcDTO.builder()
                    .event(params.getEvent())
                    .eqqueueId(eqqueueId)
                    //.type("shpfile") //如果不指定type类型则默认返回geojson类型的数据
                    .build();

            String fileJsonstring = thirdPartyCommonApi.getSeismicEventGetYxcByGet(eventGetYxcDTO);

            Double progress = getEventProgress(params.getEvent(),eqqueueId);

            while (progress < 10.00) {

                Thread.sleep(4000);  // 9秒后重新请求

                progress = getEventProgress(params.getEvent(),eqqueueId);

            }
            fileJsonstring = thirdPartyCommonApi.getSeismicEventGetYxcByGet(eventGetYxcDTO);
            String filePath = JsonParser.parseJsonToFileField(fileJsonstring);

            if (filePath != "" | StringUtils.isNotEmpty(filePath)) {

                saveIntensity(params, filePath, eqqueueId, "geojson");  // 把数据插入到己方数据库

                FileUtils.downloadFile(filePath, Constants.PROMOTION_DOWNLOAD_PATH);     // 下载文件并保存到本地

                return CompletableFuture.completedFuture(null);
            }

            return CompletableFuture.failedFuture(new AsyncExecuteException(MessageConstants.YXC_ASYNC_EXECUTE_ERROR));

        } catch (Exception e) {

            updateEventState(params.getEvent(), eqqueueId, 4);    // 修改状态评估异常停止...

            e.printStackTrace();

            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * @param params    触发地震时上传的数据
     * @param eqqueueId 触发地震时返回的eqqueueid
     * @author: xiaodemos
     * @date: 2024/12/4 18:10
     * @description: 进行乡镇级经济建筑人员伤亡的灾损评估
     */
    @Async
    public CompletableFuture<Void> handleTownLevelAssessment(EqEventReassessmentDTO params, String eqqueueId) {

        try {

            EqEventGetResultTownDTO eqEventGetResultTownDTO = EqEventGetResultTownDTO.builder()
                    .event(params.getEvent())
                    .eqqueueId(eqqueueId)
                    .build();


            String seismicEventResultTown = thirdPartyCommonApi.getSeismicEventGetGetResultTownByGet(eqEventGetResultTownDTO);

            Double progress = getEventProgress(params.getEvent(),eqqueueId);

            while (progress < 25.00) {

                Thread.sleep(4000);  // 9秒后重新请求

                progress = getEventProgress(params.getEvent(),eqqueueId);

            }

            seismicEventResultTown = thirdPartyCommonApi.getSeismicEventGetGetResultTownByGet(eqEventGetResultTownDTO);

            ResultEventGetResultTownDTO resultEventGetResultTownDTO = JsonParser.parseJson(
                    seismicEventResultTown,
                    ResultEventGetResultTownDTO.class);

            List<ResultEventGetResultTownVO> eventGetResultTownDTOData = resultEventGetResultTownDTO.getData();

            if (eventGetResultTownDTOData.size() != MessageConstants.RESULT_ZERO) {

                saveTownResult(eventGetResultTownDTOData);  // 保存到己方数据库

                return CompletableFuture.completedFuture(null);
            }

            return CompletableFuture.failedFuture(new AsyncExecuteException(MessageConstants.XZ_ASYNC_EXECUTE_ERROR));

        } catch (Exception e) {

            updateEventState(params.getEvent(), eqqueueId, 4);    // 修改状态评估异常停止...

            e.printStackTrace();

            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * @param eventResult 评估结果
     * @author: xiaodemos
     * @date: 2024/12/2 19:41
     * @description: 批量保存乡镇级结果到己方数据库
     */
    public void saveTownResult(List<ResultEventGetResultTownVO> eventResult) {

        List<AssessmentResult> saveList = new ArrayList<>();

        for (ResultEventGetResultTownVO res : eventResult) {
            AssessmentResult assessmentResult = AssessmentResult.builder()
                    .id(UUID.randomUUID().toString())
                    .eqid(res.getEvent())
                    .build();
            BeanUtils.copyProperties(res, assessmentResult);

            saveList.add(assessmentResult);
        }
        asyncTown = assessmentResultService.saveBatch(saveList);
    }

    /**
     * @param eqId      事件编码
     * @param eqqueueId 批次编码
     * @param state     状态
     * @author: xiaodemos
     * @date: 2024/12/14 16:08
     * @description: 对状态进行更新
     */
    public void updateEventState(String eqId, String eqqueueId, int state) {
        assessmentBatchService.updateBatchState(eqId, eqqueueId, state);
    }

    /**
     * @param eqId 事件编码
     * @author: xiaodemos
     * @date: 2024/12/14 16:09
     * @description: 根据Id查询这场评估结果的进度
     * @return: 返回批次进度
     */
    public Double getEventProgress(String eqId,String eqqueueId) {

        AssessmentBatch processes = assessmentProcessesService.getSeismicAssessmentProcesses(eqId,eqqueueId);

        return processes.getProgress();
    }

}
