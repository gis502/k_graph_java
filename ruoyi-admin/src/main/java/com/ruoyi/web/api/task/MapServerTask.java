package com.ruoyi.web.api.task;

import com.ruoyi.system.domain.entity.AssessmentBatch;
import com.ruoyi.system.domain.entity.EqList;
import com.ruoyi.system.domain.vo.ResultEventGetMapVO;
import com.ruoyi.system.service.impl.AssessmentBatchServiceImpl;
import com.ruoyi.system.service.impl.EqListServiceImpl;
import com.ruoyi.web.api.service.SeismicMapDownloadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MapServerTask {

    @Resource
    private SeismicMapDownloadService mapDownloadService;
    @Resource
    private EqListServiceImpl eqListService;
    @Resource
    private AssessmentBatchServiceImpl assessmentBatchService;

    private ScheduledExecutorService scheduler;
    private Instant startTime;

    // 任务参数
    private String eqId;
    private String eqqueueId;

    /**
     * 启动定时任务
     *
     * @param eqId 参数1
     * @param eqqueueId 参数2
     */
    public void startTask(String eqId, String eqqueueId) {
        if (scheduler != null && !scheduler.isShutdown()) {
            log.warn("定时任务已经在运行中");
            return;
        }

        // 设置任务参数
        this.eqId = eqId;
        this.eqqueueId = eqqueueId;

        scheduler = Executors.newScheduledThreadPool(1); // 创建线程池
        startTime = Instant.now(); // 记录任务开始时间
        log.info("定时任务启动, 开始时间: {}, 参数1: {}, 参数2: {}", startTime, eqId, eqqueueId);

        // 每隔 10 秒执行一次任务
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // 使用任务参数
                log.info("当前任务参数 -> 参数1: {}, 参数2: {}", eqId, eqqueueId);

                // 从数据库获取最新的 eqid 和 eqqueueid
                EqList recentSeismicData = eqListService.findRecentSeismicData();
                log.info("获取最新的eqid、eqqueueid -> {}, {}", eqId, eqqueueId);

                // 是否已经评估完成过
                AssessmentBatch assessmentBatch = assessmentBatchService.selectBatchProgressByEqId(eqId,eqqueueId);
                log.info("获取评估进度 -> {}", assessmentBatch.getProgress());

                // 检查是否超时（20 分钟）、是否评估状态为已完成、是否评估进度为 100%
                if (Duration.between(startTime, Instant.now()).toMinutes() >= 20
                        || assessmentBatch.getState() == 1) {
                    log.info("任务完成或超时，停止执行");
                    stopTask(); // 停止任务
                    return;
                }

                // 调用第三方接口获取图片数据
                List<ResultEventGetMapVO> imageData = mapDownloadService.getMapData(eqId, eqqueueId);
                log.info("获取第三方接口图片数据 -> {}", imageData);

                // 获取评估进度
                Double progress = mapDownloadService.getEventProgress(eqId,eqqueueId);

                if (imageData != null) {
                    // 如果进度达到 100%，停止任务
                    if (progress >= 100) {
                        updateEventState(eqId,eqqueueId,1);
                        log.info("评估进度达到 100%，停止任务");
                        stopTask(); // 停止任务
                        return;
                    }
                    // 推送图片信息给前端
                    // WebSocketMapServer.sendInfo(imageData.toString(), "MapData");
                    for (ResultEventGetMapVO imageDatum : imageData) {
                        log.info("专题图评估结果 -> {}, {}", imageDatum.getFileName(), imageDatum.getSourceFile());
                    }
                }
            } catch (Exception e) {
                log.error("定时任务执行出错: {}", e.getMessage(), e);
                stopTask(); // 停止任务
            }
        }, 0, 10, TimeUnit.SECONDS); // 初始延迟 0 秒，每隔 10 秒执行一次
    }

    /**
     * 停止定时任务
     */
    public void stopTask() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown(); // 关闭线程池
            log.info("定时任务已停止");
        }
    }

    public void updateEventState(String eqId, String eqqueueId, int state) {
        assessmentBatchService.updateBatchState(eqId, eqqueueId, state);
    }

}
