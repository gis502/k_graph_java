package com.ruoyi.web.api.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.common.exception.AsyncExecuteException;
import com.ruoyi.common.exception.DataSaveException;
import com.ruoyi.common.exception.ParamsIsEmptyException;
import com.ruoyi.common.utils.StringUtils;
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
import javax.annotation.Resource;
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

}
