package com.ruoyi.web.api.service;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.system.domain.dto.EqEventGetMapDTO;
import com.ruoyi.system.domain.dto.EqEventGetReportDTO;
import com.ruoyi.system.domain.dto.ResultEventGetMapDTO;
import com.ruoyi.system.domain.dto.ResultEventGetReportDTO;
import com.ruoyi.system.domain.entity.AssessmentBatch;
import com.ruoyi.system.domain.entity.AssessmentOutput;
import com.ruoyi.system.domain.vo.ResultEventGetMapVO;
import com.ruoyi.system.domain.vo.ResultEventGetReportVO;
import com.ruoyi.system.service.impl.AssessmentBatchServiceImpl;
import com.ruoyi.system.service.impl.AssessmentOutputServiceImpl;
import com.ruoyi.web.api.ThirdPartyCommonApi;
import com.ruoyi.web.core.utils.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author: xiaodemos
 * @date: 2025-01-11 16:30
 * @description: 专题图、报告文件下载服务
 */

@Slf4j
@Component
public class SeismicMapDownloadService {

    @Resource
    private ThirdPartyCommonApi thirdPartyCommonApi;

    @Resource
    private AssessmentBatchServiceImpl assessmentBatchService;

    @Resource
    private AssessmentOutputServiceImpl assessmentOutputService;

    @Resource
    private SeismicAssessmentProcessesService assessmentProcessesService;

    // 调用第三方接口获取图片数据、并且解析出JSON数据
    public List<ResultEventGetMapVO> getMapData(String event, String eqqueueId) {

        try{
            // 构建第三方 专题图 接口请求对象
            EqEventGetMapDTO getMapDTO = EqEventGetMapDTO.builder()
                    .event(event)
                    .eqqueueId(eqqueueId)
                    .build();

            // 1.获取评估 专题图 的结果
            String eventMaps = thirdPartyCommonApi.getSeismicEventGetMapByGet(getMapDTO);

            // 专题图 结果
            ResultEventGetMapDTO resultEventGetMapDTO = JsonParser.parseJson(eventMaps, ResultEventGetMapDTO.class);
            List<ResultEventGetMapVO> eventGetMapDTOData = resultEventGetMapDTO.getData();

            if (eventGetMapDTOData.size() != MessageConstants.RESULT_ZERO) {

                // 创建一个队列，存储已保存的数据的标识
                Set<String> savedEventIds = getSavedEventMap(eventGetMapDTOData);

                // 筛选出未保存的专题图
                List<ResultEventGetMapVO> unsavedData = eventGetMapDTOData.stream()
                        .filter(data -> !savedEventIds.contains(data.getId())) // 根据 id 判断是否已保存
                        .collect(Collectors.toList());

                // 如果有未保存的数据，则调用 saveMap 保存
                if (!unsavedData.isEmpty()) {

                    saveWithDownloadMap(unsavedData, event);  // 保存到己方数据库并且下载

                    log.info("保存专题图成功");

                    return unsavedData;     //下载一张专题图就立马返回一张专题图

                } else {

                    log.info("所有专题图已保存，跳过保存操作");

                }
            }
        } catch (Exception e) {
            updateEventState(event, eqqueueId, 4);    // 修改状态评估异常停止...
            e.printStackTrace();
        }

        return new ArrayList<>();
    }


    /**
     * @param event     地震 Id
     * @param eqqueueId 地震批次 Id
     * @author: xiaodemos
     * @date: 2025/1/20 14:54
     * @description: 获取震中区的专题图并进行下载
     * @return: 专题图列表
     */
    public List<ResultEventGetReportVO> getReportsData(String event, String eqqueueId) {

        try {

            // 构建第三方 灾情报告 接口请求对象
            EqEventGetReportDTO getReportDTO = EqEventGetReportDTO.builder()
                    .event(event)
                    .eqqueueId(eqqueueId)
                    .build();

            // 获取评估 灾情报告 的结果
            String eventReports = thirdPartyCommonApi.getSeismicEventGetReportByGET(getReportDTO);

            // 灾情报告 结果
            ResultEventGetReportDTO resultEventGetReportDTO = JsonParser.parseJson(eventReports, ResultEventGetReportDTO.class);
            List<ResultEventGetReportVO> eventGetReportDTOData = resultEventGetReportDTO.getData();

            if (eventGetReportDTOData.size() != MessageConstants.RESULT_ZERO) {

                // 创建一个队列，存储已保存的数据的标识
                Set<String> savedEventIds = getSavedEventReport(eventGetReportDTOData);

                // 筛选出未保存的专题图
                List<ResultEventGetReportVO> unsavedData = eventGetReportDTOData.stream()
                        .filter(data -> !savedEventIds.contains(data.getId())) // 根据 Id 判断是否已保存
                        .collect(Collectors.toList());
                // 如果有未保存的数据，则调用 saveMap 保存
                if (!unsavedData.isEmpty()) {

                    saveWithDownloadReport(unsavedData, event);  // 保存到己方数据库

                    log.info("保存灾情报告成功");

                    return unsavedData;     //下载一张专题图就立马返回一张专题图

                } else {

                    log.info("所有灾情报告已保存，跳过保存操作");

                }
            }

        } catch (Exception e) {

            updateEventState(event, eqqueueId, 4);    // 修改状态评估异常停止...

            e.printStackTrace();

        }

        return new ArrayList<>();
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
     * @param eventGetMap 专题图数据
     * @param eqid        地震id
     * @author: xiaodemos
     * @date: 2024/12/3 23:51
     * @description: 保存专题图数据到数据库并下载文件到本地
     */
    public void saveWithDownloadMap(List<ResultEventGetMapVO> eventGetMap, String eqid) {

        List<AssessmentOutput> saveList = new ArrayList<>();
        for (ResultEventGetMapVO res : eventGetMap) {
            AssessmentOutput assessmentOutput = AssessmentOutput.builder()
                    .id(res.getId())
                    .eqqueueId(res.getEqqueueId())
                    .eqid(eqid)
                    .code(res.getCode())
                    .proTime(res.getProTime())
                    .fileType(res.getFileType())
                    .fileName(res.getFileName())
                    .fileExtension(res.getFileExtension())
                    .fileSize(res.getFileSize())
                    .sourceFile(res.getSourceFile())
                    .localSourceFile(Constants.PROMOTION_INVOKE_URL_HEAD + res.getSourceFile())
                    .remark(res.getRemark())
                    .size(res.getSize())
                    .type("1")
                    .size(res.getSize())
                    .build();

            saveList.add(assessmentOutput);

            try {
                log.info("--------------专题图准备开始下载--------------{}", res.getSourceFile());
                System.out.println();
                FileUtils.downloadFile(res.getSourceFile(), Constants.PROMOTION_DOWNLOAD_PATH);

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        assessmentOutputService.saveBatch(saveList);

    }

    /**
     * @param eventGetReport 灾情报告数据
     * @param eqid           地震id
     * @author: xiaodemos
     * @date: 2024/12/3 23:51
     * @description: 保存灾情报告数据到数据库并下载文件到本地
     */
    public void saveWithDownloadReport(List<ResultEventGetReportVO> eventGetReport, String eqid) {

        List<AssessmentOutput> saveList = new ArrayList<>();
        for (ResultEventGetReportVO res : eventGetReport) {
            AssessmentOutput assessmentOutput = AssessmentOutput.builder()
                    .id(res.getId())
                    .eqqueueId(res.getEqqueueId())
                    .eqid(eqid)
                    .code(res.getCode())
                    .proTime(res.getProTime())
                    .fileType(res.getFileType())
                    .fileName(res.getFileName())
                    .fileExtension(res.getFileExtension())
                    .fileSize(res.getFileSize())
                    .sourceFile(res.getSourceFile())
                    .localSourceFile(Constants.PROMOTION_INVOKE_URL_HEAD + res.getSourceFile())
                    .remark(res.getRemark())
                    .type("2")
                    .size(res.getSize())
                    .build();

            // BeanUtils.copyProperties(res, assessmentOutput);

            saveList.add(assessmentOutput);

            try {

                log.info("--------------灾情报告准备开始下载--------------{}", res.getSourceFile());

                FileUtils.downloadFile(res.getSourceFile(), Constants.PROMOTION_DOWNLOAD_PATH);

            } catch (IOException e) {
                e.printStackTrace();

                // 如果非必须就不要抛出异常，能下载多少就下载多少。

                // throw new FileDownloadException(MessageConstants.FILE_DOWNLOAD_ERROR);
            }
        }

        assessmentOutputService.saveBatch(saveList);

    }

    /**
     * @param eventGetMapDTOData 专题图数据
     * @author: xiaodemos
     * @date: 2025/1/20 16:05
     * @description: 筛选出未保存下载的专题图数据
     * @return: 返回一个为下载的 Set 数据集合
     */
    private Set<String> getSavedEventMap(List<ResultEventGetMapVO> eventGetMapDTOData) {
        Set<String> savedEventIds = new HashSet<>();

        // 根据 eventGetMapDTOData 的内容查找已经保存的专题图
        for (ResultEventGetMapVO data : eventGetMapDTOData) {
            String dataId = data.getId();  // 获取专题图的唯一标识

            if (isEventAlreadySaved(dataId)) {
                savedEventIds.add(dataId);  // 将已保存的标识加入集合
            }
        }

        return savedEventIds;
    }

    private Set<String> getSavedEventReport(List<ResultEventGetReportVO> eventGetReportDTOData) {
        // 假设通过 eventGetMapDTOData 获取数据库中的已保存数据
        Set<String> savedEventIds = new HashSet<>();

        // 根据 eventGetMapDTOData 的内容查找已经保存的灾情报告
        for (ResultEventGetReportVO data : eventGetReportDTOData) {
            String dataId = data.getId();  // 获取灾情报告的名称或唯一标识

            if (isEventAlreadySaved(dataId)) {

                savedEventIds.add(dataId);  // 将已保存的标识加入队列
            }
        }

        return savedEventIds;
    }

    /**
     * @param event 事件编码
     * @author: xiaodemos
     * @date: 2025/1/21 16:37
     * @description: 查询数据库中是否存在数据
     * @return: 如果存在返回true，不存在返回false
     */
    private boolean isEventAlreadySaved(String event) {

        // 返回查询结果为true表示已保存，false表示未保存
        return assessmentOutputService.isEventSaved(event);
    }

    /**
     * @param eqId 地震事件编码
     * @author: xiaodemos
     * @date: 2025/1/21 17:40
     * @description: 获取评估结果进度条
     * @return: 返回一个当前地震评估的进度百分比
     */
    public Double getEventProgress(String eqId) {

        AssessmentBatch processes = assessmentProcessesService.getSeismicAssessmentProcesses(eqId);
        return processes.getProgress();
    }

}
