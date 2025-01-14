package com.ruoyi.web.api.service;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.dto.EqCeicListDTO;
import com.ruoyi.system.domain.dto.ResultAutoTriggerDTO;
import com.ruoyi.system.domain.dto.ResultEqListDTO;
import com.ruoyi.system.domain.dto.ResultEventGetResultTownDTO;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    /**
     * @author: xiaodemos
     * @date: 2025/1/11 16:43
     * @description: 自动触发地震，每十分钟进行正式地震数据的同步
     */
    @Scheduled(fixedRate = 180000)  // 3分钟同步一次数据
    public void autoGainSeismicData() {

        log.info("开始进行同步正式地震数据...");

        // 从 eqlist 中查询最近的一场正式地震时间
        String lastNomalEventTime = eqListService.findLastNomalEventTime();

        EqCeicListDTO ceicListDto = EqCeicListDTO.builder()
                .createTime(lastNomalEventTime)
                .count(50)
                .pac("51")
                .build();

        String seismicListQuickReport = thirdPartyCommonApi.getSeismicListByGet(ceicListDto);

        ResultAutoTriggerDTO resultAutoTriggerDTO = JsonParser.parseJson(seismicListQuickReport, ResultAutoTriggerDTO.class);

        // 获取正式地震数据
        List<ResultAutoTriggerVO> dtoData = resultAutoTriggerDTO.getData();

        // 空数据不操作
        if(dtoData.size() == 0) {

            return ;
        }

        // 获取eqlist数据
        List<ResultEqListDTO> eqLists = eqListService.eqEventGetList();

        // 比较两者数据且合并数据
        Set<ResultAutoTriggerVO> vos = compareAndMerge(dtoData, eqLists);

        vos.forEach(data -> {
            log.info("同步正式地震数据：" + "eqid：" + data.getEqid() + "震中地点：" + data.getPlace());
        });

        for (ResultAutoTriggerVO datum : vos) {

            // 转换经纬度坐标
            GeometryFactory geometryFactory = new GeometryFactory();
            Point point = geometryFactory.createPoint(new Coordinate(datum.getLon(), datum.getLat()));

            EqList eqList = EqList.builder()
                    .eqid(datum.getEqid())
                    .eqqueueId("")
                    .earthquakeName(datum.getPlace())
                    .earthquakeFullName(
                            datum.getEqtime() + datum.getPlace() + StringUtils.substring(
                                    datum.getName(),
                                    StringUtils.length(datum.getName()) - 6))
                    .eqAddr(datum.getPlace())
                    .geom(point)
                    .intensity("")
                    .magnitude(datum.getM().toString())
                    .depth(datum.getDepth().toString())
                    .occurrenceTime(
                            LocalDateTime.parse(
                                    datum.getEqtime(),
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                            ))
                    .eqType("Z")    // 正式地震
                    .source("0")    // 自动触发
                    .eqAddrCode("")
                    .townCode("")
                    .pac(datum.getPac())
                    .type(datum.getType())
                    .isDeleted(0)
                    .build();

            eqListService.save(eqList);
        }
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
        // 1.对正式地震的结果集进行对比处理 挑选出可以插入的数据

        // 类型优先级映射
        Map<String, Integer> typePriority = new HashMap<>();
        typePriority.put("CC", 1);
        typePriority.put("SC", 2);
        typePriority.put("CD", 3);
        typePriority.put("CA", 4);
        typePriority.put("AU", 5);
        typePriority.put("YN", 6);
        typePriority.put("CQ", 7);

        // 使用一个 Map 来存储每个 (eqid + name) 的数据
        Map<String, ResultAutoTriggerVO> resultMap = new HashMap<>();

        for (ResultAutoTriggerVO data : dtolists) {
            // 生成唯一的 key: eqid + name 作为组合键
            String key =  data.getName();

            // 如果 resultMap 中没有该 key，或者当前数据的优先级更高，则更新
            if (!resultMap.containsKey(key) || typePriority.get(data.getType()) < typePriority.get(resultMap.get(key).getType())) {
                resultMap.put(key, data);
            }
        }

        HashSet<ResultAutoTriggerVO> normalSeismic = new HashSet<>(resultMap.values());

        // 2.对比正式库中有无这条数据

        // 存储需要插入的记录
        Set<ResultAutoTriggerVO> recordsToInsert = new HashSet<>();

        // 保存 eqlists 中所有 eqid 的集合，用于快速查找
        Set<String> eqlistIds = new HashSet<>();
        for (ResultEqListDTO eqlist : eqlists) {
            eqlistIds.add(eqlist.getEqid());  // 保存 eqid
        }

        for (ResultAutoTriggerVO data : normalSeismic) {
            if (!eqlistIds.contains(data.getEqid())) {
                // 如果不存在于 eqlistIds 中，则将这条记录保存到 recordsToInsert
                recordsToInsert.add(data);
            }
        }

        return recordsToInsert;

    }

}
