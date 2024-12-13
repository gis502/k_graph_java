package com.ruoyi.web.api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.common.exception.*;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.domain.vo.ResultEventGetMapVO;
import com.ruoyi.system.domain.vo.ResultEventGetPageVO;
import com.ruoyi.system.domain.vo.ResultEventGetReportVO;
import com.ruoyi.system.domain.vo.ResultEventGetResultTownVO;
import com.ruoyi.system.service.impl.*;
import com.ruoyi.web.api.ThirdPartyCommonApi;
import com.ruoyi.web.core.utils.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

    private boolean asyncIntensity = false, asyncTown = false, asyncOutputMap = false, asyncOutputReport = false;

    @Transactional(rollbackFor = Exception.class)
    public boolean seismicEventReassessment(EqEventReassessmentDTO params) {

        String eqqueueId = null;
        try {
            // 重新对地震进行评估
             eqqueueId = handleThirdPartySeismicReassessment(params);
             eqqueueId = JsonParser.parseJsonToEqQueueId(eqqueueId);
            // eqqueueId = "T2024110313362251182600";

            // 如果返回的结果是一个空字符串，表示数据已经插入成功，否则抛出异常，事务回滚
            if (StringUtils.isEmpty(eqqueueId)) {
                throw new ParamsIsEmptyException(MessageConstants.RETURN_IS_EMPTY);
            }

            // 数据更新到第三方数据库成功后，再更新到本地数据库
            getWithSave(params, eqqueueId);

            // 异步进行地震影响场灾损评估
            handleSeismicYxcEventAssessment(params, eqqueueId);

            // 异步进行乡镇级评估
            handleTownLevelAssessment(params, eqqueueId);

            // 异步获取专题图评估结果
            handleSpecializedAssessment(params, eqqueueId);

            // 异步获取灾情报告评估结果
            handleDisasterReportAssessment(params, eqqueueId);

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
     * @description: 调用第三方地震触发接口
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
     * @param params    触发地震时上传的数据
     * @param eqqueueId 触发地震时返回的eqqueueid
     * @author: xiaodemos
     * @date: 2024/12/4 18:10
     * @description: 进行地震影响场的灾损评估
     */
    private void handleSeismicYxcEventAssessment(EqEventReassessmentDTO params, String eqqueueId) {

        CompletableFuture<List<String>> future = fetchSeismicEventGetYxc(params, eqqueueId);

        try {

            List<String> filePaths = future.get();  // 等待异步任务完成并获取返回结果

            if (filePaths == null || filePaths.isEmpty()) {
                throw new ResultNullPointException(MessageConstants.RETURN_IS_EMPTY);
            }

            String geoJsonFilePath  = JsonParser.parseJsonToFileField(filePaths.get(0));

            if (geoJsonFilePath == "" | geoJsonFilePath.isEmpty() || geoJsonFilePath.equals("")) {
                throw new ResultNullPointException(MessageConstants.RETURN_IS_EMPTY);
            }

            String shpFilePath   = JsonParser.parseJsonToFileField(filePaths.get(1));

            if (shpFilePath == "" | shpFilePath.isEmpty() || shpFilePath.equals("")) {
                throw new ResultNullPointException(MessageConstants.RETURN_IS_EMPTY);
            }

            saveIntensity(params, geoJsonFilePath, eqqueueId, "geojson");  // 把数据插入到己方数据库
            saveIntensity(params, shpFilePath, eqqueueId, "shpfile");  // 把数据插入到己方数据库

            FileUtils.downloadFile(geoJsonFilePath, Constants.FILE_FULL_NAME);     // 下载文件并保存到本地
            FileUtils.downloadFile(shpFilePath, Constants.FILE_FULL_NAME);     // 下载文件并保存到本地

        } catch (InterruptedException | ExecutionException | IOException e) {
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
    private void handleTownLevelAssessment(EqEventReassessmentDTO params, String eqqueueId) {

        CompletableFuture<String> future = fetchSeismicEventResultTown(params, eqqueueId);
        try {

            String seismicEventResultTown = future.get();  // 等待异步任务完成并获取返回结果
            ResultEventGetResultTownDTO resultEventGetResultTownDTO = JsonParser.parseJson(seismicEventResultTown, ResultEventGetResultTownDTO.class);
            List<ResultEventGetResultTownVO> eventGetResultTownDTOData = resultEventGetResultTownDTO.getData();

            if (eventGetResultTownDTOData.size() == MessageConstants.RESULT_ZERO) {
                throw new ResultNullPointException(MessageConstants.RETURN_IS_EMPTY);
            }

            saveTownResult(eventGetResultTownDTOData);  // 保存到己方数据库

        } catch (InterruptedException | ExecutionException e) {
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
    private void handleSpecializedAssessment(EqEventReassessmentDTO params, String eqqueueId) {

        CompletableFuture<String> future = fetchSeismicEventGetMap(params, eqqueueId);
        try {

            String eventGetMap = future.get();  // 等待异步任务完成并获取返回结果
            ResultEventGetMapDTO resultEventGetMapDTO = JsonParser.parseJson(eventGetMap, ResultEventGetMapDTO.class);
            List<ResultEventGetMapVO> eventGetMapDTOData = resultEventGetMapDTO.getData();

            if (eventGetMapDTOData.size() == MessageConstants.RESULT_ZERO) {
                throw new ResultNullPointException(MessageConstants.RETURN_IS_EMPTY);
            }

            saveMap(eventGetMapDTOData, params.getEvent());  // 保存到己方数据库

        } catch (InterruptedException | ExecutionException e) {
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
    private void handleDisasterReportAssessment(EqEventReassessmentDTO params, String eqqueueId) {

        CompletableFuture<String> stringCompletableFutureByEventGetReport = fetchSeismicEventGetReport(params, eqqueueId);
        try {

            String eventGetReport = stringCompletableFutureByEventGetReport.get();// 等待异步任务完成并获取返回结果
            ResultEventGetReportDTO resultEventGetReportDTO = JsonParser.parseJson(eventGetReport, ResultEventGetReportDTO.class);
            List<ResultEventGetReportVO> eventGetReportDTOData = resultEventGetReportDTO.getData();

            if (eventGetReportDTOData.size() == MessageConstants.RESULT_ZERO) {
                throw new ResultNullPointException(MessageConstants.RETURN_IS_EMPTY);
            }

            saveReport(eventGetReportDTOData, params.getEvent());  // 保存到己方数据库

        } catch (InterruptedException | ExecutionException e) {
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
                    .localSourceFile("").eqId(eqId).type("2").build();
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
                    .localSourceFile("").eqId(eqId).type("1").build();
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
    public void saveIntensity(EqEventReassessmentDTO params, String filePath, String eqqueueId, String fileType) {

        AssessmentIntensity assessmentIntensity = AssessmentIntensity.builder().id(UUID.randomUUID().toString()).eqqueueId(eqqueueId).batch("1").file(filePath).eqId(params.getEvent()).fileType(fileType)
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
    public void getWithSave(EqEventReassessmentDTO params, String eqqueueId) {

        // 查询批次表的当前批次
        Integer batchVersion = assessmentBatchService.gainBatchVersion(params);

        if (batchVersion == null) {
            throw new ResultNullPointException(MessageConstants.RETURN_IS_EMPTY);
        }

        AssessmentBatch batch = AssessmentBatch.builder()
                .eqqueueId(eqqueueId)
                .eqId(params.getEvent())
                .batch(batchVersion + 1)
                .state(0)
                .type("1")
                .isDeleted(0)
                .build();

        boolean flag = assessmentBatchService.save(batch);

        if (!flag) {
            throw new DataSaveException(MessageConstants.DATA_SAVE_FAILED);
        }

        log.info("重新评估的数据已经同步到批次表中 -> : ok");

    }

    /**
     * @param params    触发地震时的数据
     * @param eqqueueId 地震触发返回的地震批次编码
     * @author: xiaodemos
     * @date: 2024/11/27 1:50
     * @description: 异步执行地震影响场的灾损评估方法
     * @return: 返回烈度圈的文件路径
     */
    public CompletableFuture<List<String>> fetchSeismicEventGetYxc(EqEventReassessmentDTO params, String eqqueueId) {

        EqEventGetYxcDTO geoJsonRequest = EqEventGetYxcDTO.builder()
                .event(params.getEvent())
                .eqqueueId(eqqueueId)
                .build();

        EqEventGetYxcDTO shpFileRequest  = EqEventGetYxcDTO.builder()
                .event(params.getEvent())
                .eqqueueId(eqqueueId)
                .type("shpfile")
                .build();

        // 异步请求两个文件格式
        CompletableFuture<String> geoJsonFuture = CompletableFuture.supplyAsync(() -> {
            return thirdPartyCommonApi.getSeismicEventGetYxcByGet(geoJsonRequest);
        });

        CompletableFuture<String> shpFileFuture = CompletableFuture.supplyAsync(() -> {
            return thirdPartyCommonApi.getSeismicEventGetYxcByGet(shpFileRequest);
        });

        return CompletableFuture.allOf(geoJsonFuture, shpFileFuture).thenApply(v -> {
            List<String> results = new ArrayList<>();
            try {
                results.add(geoJsonFuture.get());    // 获取geojson和shpfile的下载结果
                results.add(shpFileFuture.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return results;
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
    public CompletableFuture<String> fetchSeismicEventGetReport(EqEventReassessmentDTO params, String eqqueueId) {
        EqEventGetReportDTO getReportDTO = EqEventGetReportDTO.builder().event(params.getEvent()).eqqueueId(eqqueueId).build();

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
    public CompletableFuture<String> fetchSeismicEventGetMap(EqEventReassessmentDTO params, String eqqueueId) {
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
    public CompletableFuture<String> fetchSeismicEventResultTown(EqEventReassessmentDTO params, String eqqueueId) {

        EqEventGetResultTownDTO eqEventGetResultTownDTO = EqEventGetResultTownDTO.builder().event(params.getEvent()).eqqueueId(eqqueueId).build();

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
    public boolean isSaved() { return asyncIntensity && asyncTown && asyncOutputMap && asyncOutputReport; }


}
