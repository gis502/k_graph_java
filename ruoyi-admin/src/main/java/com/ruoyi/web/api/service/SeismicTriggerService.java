package com.ruoyi.web.api.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.common.exception.AsyncExecuteException;
import com.ruoyi.common.exception.DataSaveException;
import com.ruoyi.common.exception.ParamsIsEmptyException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.dto.EqEventGetMapDTO;
import com.ruoyi.system.domain.dto.EqEventGetReportDTO;
import com.ruoyi.system.domain.dto.EqEventGetYxcDTO;
import com.ruoyi.system.domain.dto.EqEventTriggerDTO;
import com.ruoyi.system.domain.entity.AssessmentOutput;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.entity.AssessmentBatch;
import com.ruoyi.system.domain.entity.EqList;
import com.ruoyi.system.domain.vo.ResultEventGetPageVO;
import com.ruoyi.system.domain.vo.ResultEventGetResultTownVO;
import com.ruoyi.system.domain.vo.ResultSeismicEventGetResultVO;
import com.ruoyi.system.service.impl.AssessmentBatchServiceImpl;
import com.ruoyi.system.service.impl.EqListServiceImpl;
import com.ruoyi.web.api.ThirdPartyCommonApi;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import java.util.List;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author: xiaodemos
 * @date: 2024-11-26 2:45
 * @description: 地震触发业务逻辑
 */

@Slf4j
@Service
@Transactional
public class SeismicTriggerService {
    @Resource
    private ThirdPartyCommonApi thirdPartyCommonApi;
    @Resource
    private EqListServiceImpl eqListService;
    @Resource
    private AssessmentBatchServiceImpl assessmentBatchService;

    /**
     * @param params 手动触发的地震事件参数
     * @author: xiaodemos
     * @date: 2024/11/26 2:53
     * @description: 地震事件触发时，将进行地震影响场、烈度圈、乡镇级、经济建筑人员伤亡的灾损评估。
     * 异步的将评估结果保存到数据库，并且下载灾情报告和专题图到本地，路径存储到数据库中。
     * 触发的地震数据将同步到双方的数据库中。
     */
    public void seismicEventTrigger(EqEventTriggerDTO params) {
        // 1.把前端上传的数据保存到对方的数据库中
        String eqqueueId = thirdPartyCommonApi.getSeismicTriggerByPost(params);
        eqqueueId = parseJsonToEqQueueId(eqqueueId);

        // 2.如果返回的结果是一个字符串的话 表明数据已经插入成功，如果不是，则事务回滚
        if (StringUtils.isEmpty(eqqueueId)) {
            throw new ParamsIsEmptyException(MessageConstants.RETURN_PARAMS_IS_EMPTY);
        }

        // 3.插入到对方数据库成功后把数据插入到我们自己的数据库中，需要把插入的数据重新在对方数据库查询一边再插入到我们的数据库中
        getWithSave(params, eqqueueId);

        // 4.进行地震影响场的灾损评估,灾损评估需要等待10 - 15 min，所以这里需要使用异步调用。
        // 执行异步之前 需要把我们的数据提交到数据库中，把当前的地震数据状态改为正在进行评估中
        //TODO 把上传的数据插入到对应表的数据库中
        CompletableFuture<String> stringCompletableFutureByGetYxc = fetchSeismicEventGetYxc(params, eqqueueId);
        try {
            // 等待异步任务完成并获取返回结果
            String fileJsonstring = stringCompletableFutureByGetYxc.get();
            String filePath = parseJsonToFileField(fileJsonstring);
            // TODO 将地震影响场的灾损评估结果保存到数据库（这里是返回的file文件路径，需要把这个文件下载到本地，路径保存到数据库）
            // 这里将对提前保存的数据进行状态的修改，并插入新的灾损评估数据到数据库中
            System.out.println("地震影响场的灾损评估 -> fileJsonstring : " + fileJsonstring);
            System.out.println("地震影响场的灾损评估 -> filePath : " + filePath);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new AsyncExecuteException(MessageConstants.YXC_ASYNC_EXECUTE_ERROR);
        }

        // 5.进行经济建筑人员伤亡评估,灾损评估需要等待10 - 15 min，所以这里需要使用异步调用。
        // 执行异步之前 需要把我们的数据提交到数据库中，把当前的地震数据状态改为正在进行评估中
        //TODO 把上传的数据插入到对应表的数据库中
        CompletableFuture<String> stringCompletableFutureByGetEventResult = fetchSeismicEventResult(params, eqqueueId);
        try {
            // 等待异步任务完成并获取返回结果
            String seismicEventGetResult = stringCompletableFutureByGetEventResult.get();
            ResultSeismicEventGetResultVO resultSeismicEventGetResultVO = parseJsonToJsonMap(seismicEventGetResult);
            // TODO 将经济等灾损评估结果保存到数据库
            // 这里将对提前保存的数据进行状态的修改，并插入新的灾损评估数据到数据库中
            System.out.println("经济建筑人员伤亡评估 -> seismicEventGetResult : " + seismicEventGetResult);
            System.out.println("经济建筑人员伤亡评估 -> resultSeismicEventGetResultVO : " + resultSeismicEventGetResultVO);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new AsyncExecuteException(MessageConstants.JJR_ASYNC_EXECUTE_ERROR);
        }

        // 7.进行乡镇级评估,灾损评估需要等待10 - 15 min，所以这里需要使用异步调用。
        CompletableFuture<String> stringCompletableFutureByEventResultTown = fetchSeismicEventResultTown(params, eqqueueId);
        try {
            // 等待异步任务完成并获取返回结果
            String seismicEventResultTown = stringCompletableFutureByEventResultTown.get();
            ResultEventGetResultTownDTO resultEventGetResultTownDTO = parseJsonToResultEventGetResultTown(seismicEventResultTown);
            List<ResultEventGetResultTownVO> eventGetResultTownDTOData = resultEventGetResultTownDTO.getData();
            // TODO 将乡镇级评估结果保存到数据库
            System.out.println("乡镇级评估 -> resultEventGetResultTownDTO : " + resultEventGetResultTownDTO);
            System.out.println("乡镇级评估 -> eventGetResultTownDTOData : " + eventGetResultTownDTOData);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new AsyncExecuteException(MessageConstants.XZ_ASYNC_EXECUTE_ERROR);
        }

        // TODO 姜爽的接口使用异步的方式

        // 6.如果15分钟后没有拿到灾损评估结果，则进行事务回滚。

        // 7.拿到后评估结果后，保存到我们的数据库中。图片或者地址需要下载下来保存到本地

        // 8.额外写一个接口需要反复的读取我们自己数据库中的评估数据。

        // 9.拿到数据后立刻返回

    }

    /**
     * @param eqqueueId 查询触发的那条地震
     * @author: xiaodemos
     * @date: 2024/11/26 17:07
     * @description: 需要把字段转换成保存数据到我们的数据库中
     */
    public void getWithSave(EqEventTriggerDTO params, String eqqueueId) {
        // 这个eqqueueid可能存在多个批次，所以需要最新的那一个批次保存到本地，批次应该插入到多对多的那张表中
        AssessmentBatch batch = AssessmentBatch.builder().eqqueueId(eqqueueId).eqId(params.getEvent()).batch(1).state("0").type("1").build();

        boolean flag = assessmentBatchService.save(batch);

        if (!flag) {
            throw new DataSaveException(MessageConstants.DATA_SAVE_FAILED);
        }

        log.info("触发的数据已经同步到批次表中 -> : ok");

        EqEventGetPageDTO dto = EqEventGetPageDTO.builder().event(params.getEvent()).build();

        String seismicEvent = thirdPartyCommonApi.getSeismicEventByGet(dto);

        log.info("解析的json字符串 seismicEvent -> : {}", seismicEvent);

        //转换为JSONObject
        ResultEventGetPageDTO parsed = parseJsonToObject(seismicEvent);
        ResultEventGetPageVO resultEventGetPageVO = parsed.getData().getRows().get(0);

        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(params.getLongitude(), params.getLatitude()));

        // TODO 修改数据库字段与dto保持一致可以优化这段代码
        EqList eqList = EqList.builder()
                .eqId(resultEventGetPageVO.getEvent())
                .earthquakeName(resultEventGetPageVO.getEqName())
                .earthquakeFullName(resultEventGetPageVO.getEqFullName())
                .eqAddr(resultEventGetPageVO.getEqAddr()).geom(point)
                .magnitude(resultEventGetPageVO.getEqMagnitude())
                .depth(resultEventGetPageVO.getEqDepth().toString())
                //.occurrenceTime(params.getEqTime())     //这里是上传dto时保存的地震时间
                .eqType(resultEventGetPageVO.getEqType()).source(resultEventGetPageVO.getSource())
                .eqAddrCode(resultEventGetPageVO.getEqAddrCode())
                .townCode(resultEventGetPageVO.getTownCode())
                .pac("").type("").build();
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
     * @date: 2024/11/27 2:11
     * @description: 异步执行经济建筑人员伤亡的灾损评估方法
     * @return: 返回经济建筑人员伤亡的灾损评估结果
     */
    public CompletableFuture<String> fetchSeismicEventResult(EqEventTriggerDTO params, String eqqueueId) {

        EqEventGetResultDTO eqEventGetResultDTO = EqEventGetResultDTO.builder()
                .event(params.getEvent())
                .eqqueueId(eqqueueId)
                .type("sw,jj,jz")
                .build();

        return CompletableFuture.supplyAsync(() -> {
            return thirdPartyCommonApi.getSeismicEventGetResultByGet(eqEventGetResultDTO);
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
     * @param jsonString 第三方接口取到的json数据
     * @author: xiaodemos
     * @date: 2024/11/26 22:50
     * @description: 需要转换成ResultEventGetPageDTO对象进行存储
     * @return: 返回一个ResultEventGetPageDTO对象
     */
    public ResultEventGetPageDTO parseJsonToObject(String jsonString) {
        // 创建ObjectMapper实例
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 反序列化JSON字符串为Object对象
            ResultEventGetPageDTO dto = objectMapper.readValue(jsonString, ResultEventGetPageDTO.class);
            return dto;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param jsonString 第三方接口取到的json数据
     * @author: xiaodemos
     * @date: 2024/11/27 1:10
     * @description: 取到json字符串中的file字段 （可以优化到一个方法中，每次解析的时候传入一个flag值来进行整体解析或者是按key值解析）
     * @return: 返回file字段中的文件路径
     */
    public String parseJsonToFileField(String jsonString) {
        // 创建ObjectMapper实例
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 解析JSON字符串为JsonNode对象
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // 获取 "data" 对象下的 "file" 字段
            JsonNode dataNode = rootNode.path("data");  // 获取data节点
            String filePath = dataNode.path("file").asText();  // 获取file字段并转换为String

            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param jsonString 第三方接口取到的json数据
     * @author: xiaodemos
     * @date: 2024/11/27 1:35
     * @description: 对json数据的map格式对象进行解析
     * @return: 返回经济建筑人员伤亡的Dto数据
     */
    public ResultSeismicEventGetResultVO parseJsonToJsonMap(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 将JSON字符串反序列化为ResultSeismicEventGetResultDTO对象
            ResultSeismicEventGetResultDTO dto = objectMapper.readValue(jsonString, ResultSeismicEventGetResultDTO.class);
            return dto.getData();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param jsonString 第三方接口取到的json数据
     * @author: xiaodemos
     * @date: 2024/11/27 2:55
     * @description: 需要转换成ResultEventGetResultTownDTO对象进行存储
     * @return: 返回一个ResultEventGetResultTownDTO对象
     */
    public ResultEventGetResultTownDTO parseJsonToResultEventGetResultTown(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 将JSON字符串反序列化为ResultEventGetResultTownDTO对象
            ResultEventGetResultTownDTO dto = objectMapper.readValue(jsonString, ResultEventGetResultTownDTO.class);
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param jsonString 第三方接口取到的json数据
     * @author: xiaodemos
     * @date: 2024/11/27 6:12
     * @description: 对字符串进行解析，获取eqqueueid
     * @return: 返回 eqqueueid
     */
    public String parseJsonToEqQueueId(String jsonString) {
        // 创建ObjectMapper实例
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 解析JSON字符串为JsonNode对象
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // 获取 "data" 对象下的 "eqqueueId" 字段
            JsonNode dataNode = rootNode.path("data");  // 获取data节点

            String filePath = dataNode.path("eqqueueId").asText();  // 获取file字段并转换为String

            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 获取灾情报告并存到本地
    public List<AssessmentOutput> getReport(EqEventGetReportDTO params) {

        String seismicEventGetReportByGET = thirdPartyCommonApi.getSeismicEventGetReportByGET(params);

        // 将从第三方接口获取到的String数据转换成map,再拿出data,再将data里的数据转换成List,最终得到Report_result
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(seismicEventGetReportByGET, Map.class);

            if (responseMap != null && responseMap.get("code").equals(200)) {
                String dataJson = objectMapper.writeValueAsString(responseMap.get("data"));
                List<AssessmentOutput> Report_result = objectMapper.readValue(dataJson, new TypeReference<List<AssessmentOutput>>() {});

                // 调用文件下载方法
                downloadFiles(Report_result);  // 下载文件并存储到本地
                return Report_result;
            } else {
                throw new RuntimeException("API 返回错误信息: " + responseMap.get("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("解析 seismicEventGetReportByGET 时出错: " + e.getMessage());
        }
    }

    /**
     * 处理文件下载逻辑
     *
     * @param assessmentOutputs 包含文件信息的 AssessmentOutput 列表
     */
    private void downloadFiles(List<AssessmentOutput> assessmentOutputs) {
        for (AssessmentOutput output : assessmentOutputs) {
            String sourceFile = output.getSourceFile();  // 获取文件路径
            String eqqueueId = output.getEqqueueId();   // 获取地震队列ID

            // 生成目标文件保存路径
            String saveDir = "D:\\雅安灾损文件\\" + eqqueueId + "\\灾情报告";
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();  // 如果目录不存在则创建
            }

            // 拼接完整的文件URL
            String fileUrl = "http://tq-test.xixily.com:10340" + sourceFile;

            // 获取文件名（从 sourceFile 中提取文件名）
            String fileName = new File(sourceFile).getName();

            // 目标文件路径
            File targetFile = new File(saveDir, fileName);

            // 调用方法下载文件
            downloadFileFromUrl(fileUrl, targetFile);
        }
    }

    /**
     * 从 URL 下载文件并保存到指定路径
     *
     * @param fileUrl 文件的 URL
     * @param targetFile 目标文件
     */
    private void downloadFileFromUrl(String fileUrl, File targetFile) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            // 获取输入流并保存到文件
            try (InputStream inputStream = connection.getInputStream();
                 OutputStream outputStream = new FileOutputStream(targetFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("文件已下载并保存: " + targetFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("下载文件失败: " + fileUrl);
        }
    }





    /**
     * 获取专题图并存到本地
     * @param params
     * @return
     */

    public List<AssessmentOutput> getMap(EqEventGetMapDTO params) {

        String seismicEventGetMapByGET = thirdPartyCommonApi.getSeismicEventGetGetMapByGet(params);

        // 将从第三方接口获取到的String数据转换成map,再拿出data,再将data里的数据转换成List,最终得到Map_result
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(seismicEventGetMapByGET, Map.class);

            if (responseMap != null && responseMap.get("code").equals(200)) {
                String dataJson = objectMapper.writeValueAsString(responseMap.get("data"));
                List<AssessmentOutput> Map_result = objectMapper.readValue(dataJson, new TypeReference<List<AssessmentOutput>>() {});

                // 调用文件下载方法
                downloadMapFiles(Map_result);  // 下载文件并存储到本地
                return Map_result;
            } else {
                throw new RuntimeException("API 返回错误信息: " + responseMap.get("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("解析 seismicEventGetMapByGET 时出错: " + e.getMessage());
        }
    }

    /**
     * 处理专题图文件下载逻辑
     *
     * @param assessmentOutputs 包含文件信息的 AssessmentOutput 列表
     */
    private void downloadMapFiles(List<AssessmentOutput> assessmentOutputs) {
        for (AssessmentOutput output : assessmentOutputs) {
            String sourceFile = output.getSourceFile();  // 获取文件路径
            String eqqueueId = output.getEqqueueId();   // 获取地震队列ID

            // 生成目标文件保存路径
            String saveDir = "D:\\雅安灾损文件\\" + eqqueueId + "\\专题图";
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();  // 如果目录不存在则创建
            }

            // 拼接完整的文件URL
            String fileUrl = "http://tq-test.xixily.com:10340" + sourceFile;

            // 获取文件名（从 sourceFile 中提取文件名）
            String fileName = new File(sourceFile).getName();

            // 目标文件路径
            File targetFile = new File(saveDir, fileName);

            // 调用方法下载文件
            downloadFileFromUrl(fileUrl, targetFile);
        }
    }






    /**
     * 获取地震影响场并存到本地
     */
    public List<AssessmentOutput> getYxc(EqEventGetYxcDTO params) {

        String seismicEventGetImpactFieldByGET = thirdPartyCommonApi.getSeismicEventGetYxcByGet(params);

        // 将从第三方接口获取到的String数据转换成map,再拿出data,再将data里的数据转换成List,最终得到Yxc_result
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(seismicEventGetImpactFieldByGET, Map.class);

            if (responseMap != null && responseMap.get("code").equals(200)) {
                String dataJson = objectMapper.writeValueAsString(responseMap.get("data"));
                List<AssessmentOutput> Yxc_result = objectMapper.readValue(dataJson, new TypeReference<List<AssessmentOutput>>() {});

                // 调用文件下载方法
                downloadImpactFieldFiles(Yxc_result);  // 下载文件并存储到本地
                return Yxc_result;
            } else {
                throw new RuntimeException("API 返回错误信息: " + responseMap.get("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("解析 seismicEventGetImpactFieldByGET 时出错: " + e.getMessage());
        }
    }

    /**
     * 处理地震影响场文件下载逻辑
     *
     * @param assessmentOutputs 包含文件信息的 AssessmentOutput 列表
     */
    private void downloadImpactFieldFiles(List<AssessmentOutput> assessmentOutputs) {
        for (AssessmentOutput output : assessmentOutputs) {
            String sourceFile = output.getSourceFile();  // 获取文件路径
            String eqqueueId = output.getEqqueueId();   // 获取地震队列ID

            // 生成目标文件保存路径
            String saveDir = "D:\\雅安灾损文件\\" + eqqueueId + "\\地震影响场";
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();  // 如果目录不存在则创建
            }

            // 拼接完整的文件URL
            String fileUrl = "http://tq-test.xixily.com:10340" + sourceFile;

            // 获取文件名（从 sourceFile 中提取文件名）
            String fileName = new File(sourceFile).getName();

            // 目标文件路径
            File targetFile = new File(saveDir, fileName);

            // 检查目标文件是否已存在
            if (targetFile.exists()) {
                System.out.println("文件已存在，跳过下载: " + targetFile.getAbsolutePath());
                continue;
            }

            // 调用方法下载文件
            downloadFileFromUrl(fileUrl, targetFile);
        }
    }

}



