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

        EqCeicListDTO ceicListDto = EqCeicListDTO.builder()
                .count(50)
                .pac("51")
                .build();
        String seismicListQuickReport = thirdPartyCommonApi.getSeismicListByGet(ceicListDto);

        ResultAutoTriggerDTO resultAutoTriggerDTO = JsonParser.parseJson(seismicListQuickReport, ResultAutoTriggerDTO.class);

        // 获取正式地震数据
        List<ResultAutoTriggerVO> dtoData = resultAutoTriggerDTO.getData();

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
        // 保存 eqlists 中所有 eqid 的集合，用于快速查找
        Set<String> eqlistIds = new HashSet<>();
        for (ResultEqListDTO eqlist : eqlists) {
            eqlistIds.add(eqlist.getEqid());  // 保存 eqid
        }

        // 用于存储结果集合
        Map<String, ResultAutoTriggerVO> resMap = new HashMap<>();

        // 遍历 dtolists，检查每个元素的 eqid 是否在 eqlistIds 中
        for (ResultAutoTriggerVO data : dtolists) {
            // 如果 eqid 不在 eqlists 中，则直接加入结果集
            if (!eqlistIds.contains(data.getEqid())) {
                resMap.put(data.getEqid(), data);
            } else {
                // 如果 eqid 已经存在，在已有数据基础上进行处理
                ResultAutoTriggerVO existingData = resMap.get(data.getEqid());

                // 如果不存在，直接插入当前数据
                if (existingData == null) {
                    resMap.put(data.getEqid(), data);
                } else {
                    // 如果存在且 type 是 'CC'，优先保存 'CC' 类型的记录
                    if ("CC".equals(data.getType())) {
                        resMap.put(data.getEqid(), data);
                    }
                }
            }
        }

        // 将最终的结果转换为 Set 并返回
        return new HashSet<>(resMap.values());
    }

}
