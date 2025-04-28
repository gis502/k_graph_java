package com.ruoyi.web.api.service;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.dto.EqCeicListDTO;
import com.ruoyi.system.domain.dto.EqEventTriggerDTO;
import com.ruoyi.system.domain.dto.ResultAutoTriggerDTO;
import com.ruoyi.system.domain.dto.ResultEqListDTO;
import com.ruoyi.system.domain.entity.EqList;
import com.ruoyi.system.domain.vo.ResultAutoTriggerVO;
import com.ruoyi.system.service.impl.EqListServiceImpl;
import com.ruoyi.web.api.ThirdPartyCommonApi;
import com.ruoyi.web.core.utils.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: xiaodemos
 * @date: 2024-12-15 18:02
 * @description: 地震自动触发（第三方接口）
 */
@Slf4j
@Component
public class SeismicAutoTriggerService {

    @Resource
    private ThirdPartyCommonApi thirdPartyCommonApi;
    @Resource
    private EqListServiceImpl eqListService;
    @Resource
    private SismiceMergencyAssistanceService sismiceMergencyAssistanceService;
    @Resource
    private SeismicTableTriggerService seismicTableTriggerService;
    @Resource
    private SeismicTriggerService  seismicTriggerService;

    /**
     * @author: xiaodemos
     * @date: 2025/1/11 16:43
     * @description: 自动触发地震，每2分钟进行正式地震数据的同步
     */
//    @Scheduled(fixedRate = 120000)  // 2分钟同步一次数据
    public void autoGainSeismicData() {

        log.info("开始进行同步正式地震数据...");

        // 从 eqlist 中查询最近的一场正式地震时间
        String lastNomalEventTime = eqListService.findLastNomalEventTime();

        EqCeicListDTO ceicListDto = EqCeicListDTO.builder().createTime(lastNomalEventTime).count(50).pac("51").build();

        String seismicListQuickReport = thirdPartyCommonApi.getSeismicListByGet(ceicListDto);

        ResultAutoTriggerDTO resultAutoTriggerDTO = JsonParser.parseJson(seismicListQuickReport, ResultAutoTriggerDTO.class);

        // 获取正式地震数据
        List<ResultAutoTriggerVO> dtoData = resultAutoTriggerDTO.getData();

        // 空数据不操作
        if (dtoData == null || dtoData.isEmpty()) {
            return;
        }

        // 获取eqlist数据
        List<ResultEqListDTO> eqLists = eqListService.eqEventGetList();

        // 比较两者数据且合并数据
        Set<ResultAutoTriggerVO> vos = compareAndMerge(dtoData, eqLists);

        for (ResultAutoTriggerVO datum : vos) {

            // 转换经纬度坐标
            GeometryFactory geometryFactory = new GeometryFactory();
            Point point = geometryFactory.createPoint(new Coordinate(datum.getLon(), datum.getLat()));

            EqList eqList = EqList.builder().eqid(datum.getEqid()).eqqueueId("")
                    .earthquakeName(datum.getPlace())
                    .earthquakeFullName(datum.getEqtime() + datum.getPlace() + StringUtils.substring(datum.getName(), StringUtils.length(datum.getName()) - 6)).eqAddr(datum.getPlace()).geom(point).intensity("").magnitude(datum.getM().toString()).depth(datum.getDepth().toString()).occurrenceTime(LocalDateTime.parse(datum.getEqtime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).eqType("Z")    // 正式地震
                    .source("0")    // 自动触发
                    .eqAddrCode("").townCode("").pac(datum.getPac()).type(datum.getType()).isDeleted(0).build();

            eqListService.save(eqList);

            EqList seismic = eqListService.getById(datum.getEqid());

            // 生成辅助决策报告
            productionAssistanceReport(seismic);
        }

        // 自动评估正式地震数据
        autoAssessmentNormal();
    }

    /**
     * @param dtolists 正式地震数据
     * @param eqlists  数据库表中的数据
     * @author: xiaodemos
     * @date: 2025/1/11 16:41
     * @description: 将接入的正式地震数据与数据库表中的数据进行比较，并合并数据
     * @return: 返回不重复的正式地震数据集合
     */
    private Set<ResultAutoTriggerVO> compareAndMerge(List<ResultAutoTriggerVO> dtolists, List<ResultEqListDTO> eqlists) {
        // 1. 对正式地震的结果集进行对比处理，挑选出可以插入的数据

        // 类型优先级映射
        Map<String, Integer> typePriority = new HashMap<>();
        // 只保留 CC 和 SC，优先级：CC > SC > CD
        typePriority.put("CC", 1);
        typePriority.put("SC", 2);
        typePriority.put("CD", 3);

        // 使用一个 Map 来存储每个 (eqid + name) 的数据
        Map<String, ResultAutoTriggerVO> resultMap = new HashMap<>();

        for (ResultAutoTriggerVO data : dtolists) {
            // 过滤掉非 SC 、 CC 和 CD 类型的记录
            if (!typePriority.containsKey(data.getType())) {
                continue;  // 跳过非 SC 、 CC 和 CD 类型的数据
            }

            // 生成唯一的 key: name 作为组合键
            String key = data.getEqtime() + data.getPlace() + data.getM().toString();

            // 如果 resultMap 中没有该 key，或者当前数据的优先级更高，则更新
            if (!resultMap.containsKey(key) || typePriority.get(data.getType()) < typePriority.get(resultMap.get(key).getType())) {
                resultMap.put(key, data);
            }
        }

        // 最终的正常震情数据集合（只包含 CC 和 SC 类型，并且 CC 类型优先）
        HashSet<ResultAutoTriggerVO> normalSeismic = new HashSet<>(resultMap.values());

        // 2. 对比正式库中是否已经有这条数据

        // 存储需要插入的记录
        Set<ResultAutoTriggerVO> recordsToInsert = new HashSet<>();

        // 保存 eqlists 中所有 eqid 的集合，用于快速查找
        Set<String> eqlistIds = new HashSet<>();
        for (ResultEqListDTO eqlist : eqlists) {
            eqlistIds.add(eqlist.getEqid());  // 保存 eqid
        }

        // 遍历过滤后的震情数据，找出需要插入的记录
        for (ResultAutoTriggerVO data : normalSeismic) {
            if (!eqlistIds.contains(data.getEqid())) {
                // 如果不存在于 eqlistIds 中，则将这条记录保存到 recordsToInsert
                recordsToInsert.add(data);
            }
        }

        return recordsToInsert;
    }

    /**
     * @author: xiaodemos
     * @date: 2025/1/21 17:51
     * @description: 自动评估接入的正式地震，从数据库中查询 eqqueueId 为空的数据来评估，每 10 分钟评估一次
     */
    public void autoAssessmentNormal() {

        // 获取 eqlist 所有数据
        List<ResultEqListDTO> dtos = eqListService.eqEventGetList();
        // 过滤出 eqqueueId 为空的记录
        List<ResultEqListDTO> recordsWithEmptyEqqueueId = dtos.stream()
                .filter(dto -> dto.getEqqueueId() == null || dto.getEqqueueId().isEmpty())  // 判断 eqqueueId 是否为空
                .collect(Collectors.toList());

        for (ResultEqListDTO resultEqListDTO : recordsWithEmptyEqqueueId) {

            EqEventTriggerDTO params = new EqEventTriggerDTO();

            params.setEvent(resultEqListDTO.getEqid());
            params.setEqName(resultEqListDTO.getEarthquakeName());
            params.setEqTime(resultEqListDTO.getOccurrenceTime());
            params.setEqAddr(resultEqListDTO.getEqAddr());
            params.setLongitude(resultEqListDTO.getLongitude());
            params.setLatitude(resultEqListDTO.getLatitude());
            params.setEqMagnitude(Double.valueOf(resultEqListDTO.getMagnitude()));
            params.setEqDepth(Double.valueOf(resultEqListDTO.getDepth()));
            params.setEqType(resultEqListDTO.getType());

            imputation(params);

        }

    }

    /**
     * @param data 正式地震数据
     * @return
     * @author: xiaodemos
     * @date: 2025/1/25 15:34
     * @description: 将接入的正式地震数据的 eqqueueId值 进行补缺
     */
    public void imputation(EqEventTriggerDTO data){
    // public CompletableFuture<Void> imputation(EqEventTriggerDTO data) {

        String eqqueueId = UUID.randomUUID().toString();

         eqListService.updateById(EqList.builder()
                .eqid(data.getEvent())
                .eqqueueId(eqqueueId)
               .build());

        // 1. 先把数据保存到第三方库
        // String eqqueueId = thirdPartyCommonApi.getSeismicTriggerByPost(data);
        // eqqueueId = JsonParser.parseJsonToEqQueueId(eqqueueId);
        // 2. 把得到的 eqqueueid 值与为空的数据进行填补

        // log.info("eqqueueId为---------------------> :{}", eqqueueId);

        // eqListService.updateById(EqList.builder()
        //        .eqid(data.getEvent())
        //        .eqqueueId(eqqueueId)
        //       .build());

        // return CompletableFuture.completedFuture(null);
    }

    /**
     * @author:  xiaodemos
     * @date:  2025/3/1 21:42
     * @description:  生成接入正式地震的辅助决策报告
     * @param seismic 地震信息
     */
    public void productionAssistanceReport(EqList seismic){

        EqEventTriggerDTO dto = new EqEventTriggerDTO();
        dto.setEvent(seismic.getEqid());
        dto.setEqName(seismic.getEarthquakeName());
        // dto.setEqTime(String.valueOf(seismic.getOccurrenceTime()));
        dto.setEqAddr(seismic.getEqAddr());
        dto.setLongitude(seismic.getGeom().getCoordinate().x);
        dto.setLatitude(seismic.getGeom().getCoordinate().y);
        dto.setEqMagnitude(Double.valueOf(seismic.getMagnitude()));
        dto.setEqDepth(Double.valueOf(seismic.getDepth()));

        //异步获取辅助决策报告值班表
        seismicTriggerService.handleAssessmentReportAssessment(dto);

        // 调用 tableFile 方法--异步获取辅助决策报告(一)
        seismicTableTriggerService.tableFile(dto);

        // 调用 file 方法--异步获取辅助决策（二）报告结果
        sismiceMergencyAssistanceService.file(dto);

    }

}

