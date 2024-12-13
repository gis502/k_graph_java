package com.ruoyi.web.api.service;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.common.exception.*;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.system.domain.dto.EqEventGetYxcDTO;
import com.ruoyi.system.domain.dto.EqEventTriggerDTO;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.service.impl.*;
import com.ruoyi.web.api.ThirdPartyCommonApi;
import com.ruoyi.web.core.utils.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Time;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author: xiaodemos
 * @date: 2024-11-26 2:45
 * @description: 地震触发业务逻辑
 */

@Slf4j
@Service
public class SeismicTriggerService {
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

    private boolean asyncIntensity = false, asyncTown = false, asyncOutputMap = false, asyncOutputReport = false;

    /**
     * @param params 手动触发的地震事件参数
     * @return 返回各数据保存是否成功的状态码，如果返回false，表示数据保存失败，如果返回true，表示数据保存成功
     * @author: xiaodemos
     * @date: 2024/11/26 2:53
     * @description: 地震事件触发时，将进行地震影响场、烈度圈、乡镇级、经济建筑人员伤亡的灾损评估。
     * 异步的将评估结果保存到数据库，并且下载灾情报告和专题图到本地，路径存储到数据库中。
     * 触发的地震数据将同步到双方的数据库中。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean seismicEventTrigger(EqEventTriggerDTO params) {
        String eqqueueId = null;
        try {
            // 把前端上传的数据保存到第三方数据库中
            eqqueueId = handleThirdPartySeismicTrigger(params);
            eqqueueId = JsonParser.parseJsonToEqQueueId(eqqueueId);
            // eqqueueId = "T2024110313362251182600";

            // 如果返回的结果是一个空字符串，表示数据已经插入成功，否则抛出异常，事务回滚
            if (StringUtils.isEmpty(eqqueueId)) {
                throw new ParamsIsEmptyException(MessageConstants.RETURN_IS_EMPTY);
            }

            // 数据插入到第三方数据库成功后，插入到本地数据库
            getWithSave(params, eqqueueId);

            // 异步进行地震影响场灾损评估
            handleSeismicYxcEventAssessment(params, eqqueueId);

            // 异步进行乡镇级评估
            handleTownLevelAssessment(params, eqqueueId);

            // 异步获取专题图评估结果
            handleSpecializedAssessment(params, eqqueueId);

            // 异步获取灾情报告评估结果
            handleDisasterReportAssessment(params, eqqueueId);

            // 检查四个评估结果的数据是否成功
            retrySaving(params, eqqueueId);

            // 返回每个阶段的保存数据状态
            return isSaved();

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
    private void retrySaving(EqEventTriggerDTO params, String eqqueueId) {

        if (!asyncIntensity) {
            log.info("正在重试保存地震影响场数据...");
            handleSeismicYxcEventAssessment(params, eqqueueId);  // 对地震影响场的灾损评估数据进行保存重试
        }

        if (!asyncTown) {
            log.info("正在重试保存乡镇级评估数据...");
            handleTownLevelAssessment(params, eqqueueId);       // 对乡镇级的灾损评估数据进行保存重试
        }

        if (!asyncOutputMap) {
            log.info("正在重试保存专题图文件...");
            handleSpecializedAssessment(params, eqqueueId);     // 对专题图的灾损评估数据进行保存重试
        }

        if (!asyncOutputReport) {
            log.info("正在重试保存灾情报告文件...");
            handleDisasterReportAssessment(params, eqqueueId);  // 对灾情报告的数据进行保存重试
        }

        updateEventState(params.getEvent(),eqqueueId,2);    // 修改批次表中的地震状态

    }

    /**
     * @param eqqueueId 触发地震接口返回的eqqueueId
     * @param params    触发接口时上传的数据
     * @author: xiaodemos
     * @date: 2024/12/4 18:05
     * @description: 补偿机制：当事务回滚后需要重新执行第三方接口数据保存操作
     */
    private void retryThirdPartySave(String eqqueueId, EqEventTriggerDTO params) {
        try {
            // 根据 eqqueueId 或其他标识判断是否需要重新插入数据
            handleThirdPartySeismicTrigger(params); // 重新调用第三方接口
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
     * @description: 调用第三方地震触发接口
     * @return: 返回eqqueueid
     */
    private String handleThirdPartySeismicTrigger(EqEventTriggerDTO params) {

        try {
            return thirdPartyCommonApi.getSeismicTriggerByPost(params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ThirdPartyApiException(MessageConstants.THIRD_PARTY_API_ERROR);
        }

    }

    /**
     * @param params    触发地震时上传的数据
     * @param eqqueueId 触发地震时返回的eqqueueid
     * @author: xiaodemos
     * @date: 2024/12/4 18:10
     * @description: 进行地震影响场的灾损评估
     */
    private void handleSeismicYxcEventAssessment(EqEventTriggerDTO params, String eqqueueId) {

        assessmentBatchService.updateBatchState(params.getEvent(), eqqueueId, 1);    // 修改状态正在执行评估中...

        CompletableFuture<String> future = fetchSeismicEventGetYxc(params, eqqueueId);
        try {

            String fileJsonstring = future.get(10, TimeUnit.MINUTES);  // 等待异步任务完成并获取返回结果
            String filePath = JsonParser.parseJsonToFileField(fileJsonstring);

            if (filePath == "" | filePath.isEmpty() || filePath.equals("")) {

                throw new ResultNullPointException(MessageConstants.RETURN_IS_EMPTY);
            }

            saveIntensity(params, filePath, eqqueueId, "geojson");  // 把数据插入到己方数据库

            FileUtils.downloadFile(filePath, Constants.FILE_FULL_NAME);     // 下载文件并保存到本地

        } catch (InterruptedException | ExecutionException | TimeoutException | IOException e) {

            updateEventState(params.getEvent(),eqqueueId,4);    // 修改状态评估异常停止...

            e.printStackTrace();
            throw new AsyncExecuteException(MessageConstants.YXC_ASYNC_EXECUTE_ERROR);
        }

    }

    /**
     * @param params    触发地震时上传的数据
     * @param eqqueueId 触发地震时返回的eqqueueid
     * @author: xiaodemos
     * @date: 2024/12/4 18:10
     * @description: 进行乡镇级经济建筑人员伤亡的灾损评估
     */
    private void handleTownLevelAssessment(EqEventTriggerDTO params, String eqqueueId) {

        CompletableFuture<String> future = fetchSeismicEventResultTown(params, eqqueueId);
        try {

            String seismicEventResultTown = future.get(10, TimeUnit.MINUTES);  // 等待异步任务完成并获取返回结果

            ResultEventGetResultTownDTO resultEventGetResultTownDTO = JsonParser.parseJson(
                    seismicEventResultTown,
                    ResultEventGetResultTownDTO.class);

            List<ResultEventGetResultTownVO> eventGetResultTownDTOData = resultEventGetResultTownDTO.getData();

            if (eventGetResultTownDTOData.size() == MessageConstants.RESULT_ZERO) {
                throw new ResultNullPointException(MessageConstants.RETURN_IS_EMPTY);
            }

            saveTownResult(eventGetResultTownDTOData);  // 保存到己方数据库

        } catch (InterruptedException | ExecutionException | TimeoutException e) {

            updateEventState(params.getEvent(),eqqueueId,4);    // 修改状态评估异常停止...

            e.printStackTrace();

            throw new AsyncExecuteException(MessageConstants.XZ_ASYNC_EXECUTE_ERROR);
        }

    }

    /**
     * @param params    触发地震时上传的数据
     * @param eqqueueId 触发地震时返回的eqqueueid
     * @author: xiaodemos
     * @date: 2024/12/4 18:10
     * @description: 获取地震评估的专题图
     */
    private void handleSpecializedAssessment(EqEventTriggerDTO params, String eqqueueId) {

        CompletableFuture<String> future = fetchSeismicEventGetMap(params, eqqueueId);
        try {

            String eventGetMap = future.get(10, TimeUnit.MINUTES);  // 等待异步任务完成并获取返回结果
            ResultEventGetMapDTO resultEventGetMapDTO = JsonParser.parseJson(eventGetMap, ResultEventGetMapDTO.class);
            List<ResultEventGetMapVO> eventGetMapDTOData = resultEventGetMapDTO.getData();

            if (eventGetMapDTOData.size() == MessageConstants.RESULT_ZERO) {
                throw new ResultNullPointException(MessageConstants.RETURN_IS_EMPTY);
            }

            saveMap(eventGetMapDTOData, params.getEvent());  // 保存到己方数据库

        } catch (InterruptedException | ExecutionException |TimeoutException e) {

            updateEventState(params.getEvent(),eqqueueId,4);    // 修改状态评估异常停止...

            e.printStackTrace();

            throw new AsyncExecuteException(MessageConstants.ZTT_ASYNC_EXECUTE_ERROR);
        }

    }

    /**
     * @param params    触发地震时上传的数据
     * @param eqqueueId 触发地震时返回的eqqueueid
     * @author: xiaodemos
     * @date: 2024/12/4 18:10
     * @description: 获取地震评估的灾情报告
     */
    private void handleDisasterReportAssessment(EqEventTriggerDTO params, String eqqueueId) {

        CompletableFuture<String> stringCompletableFutureByEventGetReport = fetchSeismicEventGetReport(params, eqqueueId);
        try {

            String eventGetReport = stringCompletableFutureByEventGetReport.get(10, TimeUnit.MINUTES);// 等待异步任务完成并获取返回结果
            ResultEventGetReportDTO resultEventGetReportDTO = JsonParser.parseJson(eventGetReport, ResultEventGetReportDTO.class);
            List<ResultEventGetReportVO> eventGetReportDTOData = resultEventGetReportDTO.getData();

            if (eventGetReportDTOData.size() == MessageConstants.RESULT_ZERO) {
                throw new ResultNullPointException(MessageConstants.RETURN_IS_EMPTY);
            }

            saveReport(eventGetReportDTOData, params.getEvent());  // 保存到己方数据库

        } catch (InterruptedException | ExecutionException | TimeoutException e) {

            updateEventState(params.getEvent(),eqqueueId,4);    // 修改状态评估异常停止...

            e.printStackTrace();

            throw new AsyncExecuteException(MessageConstants.BG_ASYNC_EXECUTE_ERROR);
        }

    }

    /**
     * @param eqId           地震id
     * @param eventGetReport 灾情报告
     * @author: xiaodemos
     * @date: 2024/12/4 14:32
     * @description: 保存灾情报告结果
     */
    public void saveReport(List<ResultEventGetReportVO> eventGetReport, String eqId) {

        List<AssessmentOutput> saveList = new ArrayList<>();
        for (ResultEventGetReportVO res : eventGetReport) {
            AssessmentOutput assessmentOutput = AssessmentOutput.builder()
                    // TODO 获取保存全路径
                    .localSourceFile("")
                    .eqId(eqId)
                    .type("2")
                    .build();
            BeanUtils.copyProperties(res, assessmentOutput);

            saveList.add(assessmentOutput);

            try {
                FileUtils.downloadFile(res.getSourceFile(), Constants.FILE_FULL_NAME);
            } catch (IOException e) {
                e.printStackTrace();
                throw new FileDownloadException(MessageConstants.FILE_DOWNLOAD_ERROR);
            }
        }

        asyncOutputReport = assessmentOutputService.saveBatch(saveList);

    }

    /**
     * @param eventGetMap 专题图数据
     * @param eqId        地震id
     * @author: xiaodemos
     * @date: 2024/12/3 23:51
     * @description: 保存专题图数据到数据库并下载文件到本地
     */
    public void saveMap(List<ResultEventGetMapVO> eventGetMap, String eqId) {

        List<AssessmentOutput> saveList = new ArrayList<>();
        for (ResultEventGetMapVO res : eventGetMap) {
            AssessmentOutput assessmentOutput = AssessmentOutput.builder()
                    // TODO 获取保存全路径
                    .localSourceFile("")
                    .eqId(eqId)
                    .type("1")
                    .build();
            BeanUtils.copyProperties(res, assessmentOutput);

            saveList.add(assessmentOutput);

            try {
                FileUtils.downloadFile(res.getSourceFile(), Constants.FILE_FULL_NAME);
            } catch (IOException e) {
                e.printStackTrace();
                throw new FileDownloadException(MessageConstants.FILE_DOWNLOAD_ERROR);
            }
        }

        asyncOutputMap = assessmentOutputService.saveBatch(saveList);
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
            AssessmentResult assessmentResult = AssessmentResult.builder().id(UUID.randomUUID().toString()).eqId(res.getEvent()).build();
            BeanUtils.copyProperties(res, assessmentResult);

            saveList.add(assessmentResult);
        }
        asyncTown = assessmentResultService.saveBatch(saveList);
    }

    /**
     * @param params    上传的参数
     * @param filePath  返回的影响场文件路径
     * @param eqqueueId 评估批次编码
     * @author: xiaodemos
     * @date: 2024/12/2 23:17
     * @description: 保存地震影响场的灾损评估结果到数据库
     */
    public void saveIntensity(EqEventTriggerDTO params, String filePath, String eqqueueId, String fileType) {

        AssessmentIntensity assessmentIntensity = AssessmentIntensity.builder()
                .id(UUID.randomUUID().toString())
                .eqqueueId(eqqueueId).batch("1")
                .file(filePath)
                .eqId(params.getEvent())
                .fileType(fileType)
                // TODO 需要保存全路径
                .localFile(filePath).build();

        asyncIntensity = assessmentIntensityService.save(assessmentIntensity);
    }

    /**
     * @param eqqueueId 查询触发的那条地震
     * @author: xiaodemos
     * @date: 2024/11/26 17:07
     * @description: 需要把字段转换成保存数据到我们的数据库中
     */
    public void getWithSave(EqEventTriggerDTO params, String eqqueueId) {
        // 这个eqqueueid可能存在多个批次，所以需要最新的那一个批次保存到本地，批次应该插入到多对多的那张表中
        AssessmentBatch batch = AssessmentBatch.builder()
                .eqqueueId(eqqueueId)
                .eqId(params.getEvent())
                .batch(1)
                .state(0)
                .type("1")
                .build();

        boolean flag = assessmentBatchService.save(batch);

        if (!flag) {
            throw new DataSaveException(MessageConstants.DATA_SAVE_FAILED);
        }

        log.info("触发的数据已经同步到批次表中 -> : ok");

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
                .eqId(resultEventGetPageVO.getEvent())
                .earthquakeName(resultEventGetPageVO.getEqName())
                .earthquakeFullName(resultEventGetPageVO.getEqFullName())
                .geom(point)
                .depth(resultEventGetPageVO.getEqDepth().toString())
                .magnitude(resultEventGetPageVO.getEqMagnitude())
                //.occurrenceTime(params.getEqTime())     //这里是上传dto时保存的地震时间
                .pac("")
                .type("")
                .isDeleted(0)
                .build();

        BeanUtils.copyProperties(resultEventGetPageVO, eqList);

        eqListService.save(eqList);

        log.info("触发的数据已经同步到 eqlist 表中 -> : ok");

    }

    /**
     * @param params    触发地震时的数据
     * @param eqqueueId 地震触发返回的地震批次编码
     * @author: xiaodemos
     * @date: 2024/11/27 1:50
     * @description: 异步执行地震影响场的灾损评估方法
     * @return: 返回烈度圈的文件路径
     */
    public CompletableFuture<String> fetchSeismicEventGetYxc(EqEventTriggerDTO params, String eqqueueId) {

        EqEventGetYxcDTO eventGetYxcDTO = EqEventGetYxcDTO.builder()
                .event(params.getEvent())
                .eqqueueId(eqqueueId)
                //.type("shpfile") //如果不指定type类型则默认返回geojson类型的数据
                .build();
        return CompletableFuture.supplyAsync(() -> {
            return thirdPartyCommonApi.getSeismicEventGetYxcByGet(eventGetYxcDTO);
        });
    }

    /**
     * @param params    触发地震时的数据
     * @param eqqueueId 地震触发返回的地震批次编码
     * @author: xiaodemos
     * @date: 2024/12/4 8:14
     * @description: 获取灾情报告
     * @return: 返回灾情报告结果
     */
    public CompletableFuture<String> fetchSeismicEventGetReport(EqEventTriggerDTO params, String eqqueueId) {
        EqEventGetReportDTO getReportDTO = EqEventGetReportDTO.builder()
                .event(params.getEvent())
                .eqqueueId(eqqueueId)
                .build();

        return CompletableFuture.supplyAsync(() -> {
            return thirdPartyCommonApi.getSeismicEventGetReportByGET(getReportDTO);
        });
    }

    /**
     * @param params    触发地震时的数据
     * @param eqqueueId 地震触发返回的地震批次编码
     * @author: xiaodemos
     * @date: 2024/12/4 8:08
     * @description: 异步执行专题图评估方法
     * @return: 返回专题图的路径
     */
    public CompletableFuture<String> fetchSeismicEventGetMap(EqEventTriggerDTO params, String eqqueueId) {
        EqEventGetMapDTO getMapDTO = EqEventGetMapDTO.builder().event(params.getEvent()).eqqueueId(eqqueueId).build();

        return CompletableFuture.supplyAsync(() -> {
            return thirdPartyCommonApi.getSeismicEventGetMapByGet(getMapDTO);
        });
    }

    /**
     * @param params    触发地震时的数据
     * @param eqqueueId 地震触发返回的地震批次编码
     * @author: xiaodemos
     * @date: 2024/11/27 3:00
     * @description: 异步执行乡镇级灾损评估方法
     * @return: 返回乡镇级灾损评估结果
     */
    public CompletableFuture<String> fetchSeismicEventResultTown(EqEventTriggerDTO params, String eqqueueId) {

        EqEventGetResultTownDTO eqEventGetResultTownDTO = EqEventGetResultTownDTO.builder()
                .event(params.getEvent())
                .eqqueueId(eqqueueId)
                .build();

        return CompletableFuture.supplyAsync(() -> {
            return thirdPartyCommonApi.getSeismicEventGetGetResultTownByGet(eqEventGetResultTownDTO);
        });
    }

    /**
     * @author: xiaodemos
     * @date: 2024/12/6 13:19
     * @description: 用于判断地震触发后获取到的灾损评估数据是否保存到己方数据库成功
     * @return: 返回True 或者 False
     */
    public boolean isSaved() {
        return asyncIntensity && asyncTown && asyncOutputMap && asyncOutputReport;
    }

    public void updateEventState(String eqId, String eqqueueId,int state) {
        assessmentBatchService.updateBatchState(eqId, eqqueueId, state);
    }
}
