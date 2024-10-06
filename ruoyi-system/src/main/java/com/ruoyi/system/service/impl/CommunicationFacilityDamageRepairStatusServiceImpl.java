package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.CommunicationFacilityDamageRepairStatus;
import com.ruoyi.system.mapper.CommunicationFacilityDamageRepairStatusMapper;
import com.ruoyi.system.service.CommunicationFacilityDamageRepairStatusService;
import com.ruoyi.system.service.strategy.DataExportStrategy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Service
public class CommunicationFacilityDamageRepairStatusServiceImpl
        extends ServiceImpl<CommunicationFacilityDamageRepairStatusMapper, CommunicationFacilityDamageRepairStatus>
        implements CommunicationFacilityDamageRepairStatusService , DataExportStrategy {
    /**
     * @param requestBTO
     * @return
     */
    @Override
    public IPage<CommunicationFacilityDamageRepairStatus> getPage(RequestBTO requestBTO) {
        Page<CommunicationFacilityDamageRepairStatus> communicationFacilityDamageRepairStatus =
                new Page<>(requestBTO.getCurrentPage(), requestBTO.getPageSize());
        String requestParam = requestBTO.getRequestParams();
        // 构造查询条件
        LambdaQueryWrapper<CommunicationFacilityDamageRepairStatus> queryWrapper = Wrappers.lambdaQuery(CommunicationFacilityDamageRepairStatus.class)
                .like(CommunicationFacilityDamageRepairStatus::getEarthquakeName, requestParam)
                .or()
                .like(CommunicationFacilityDamageRepairStatus::getEarthquakeZoneName, requestParam)
                .or()
                .apply("CAST(total_disabled_base_stations AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(restored_base_stations AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(current_disabled_base_stations AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(total_damaged_cable_length AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(repaired_cable_length AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(current_pending_repair_cable_length AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(current_interrupted_villages_count AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(current_interrupted_impact_count AS TEXT) = {0}", requestParam);
        return this.page(communicationFacilityDamageRepairStatus,queryWrapper);
    }

    /**
     * @param requestBTO
     * @return
     */
    @Override
    public List<CommunicationFacilityDamageRepairStatus> exportExcelGetData(RequestBTO requestBTO) {
        String [] ids = requestBTO.getIds();
        List<CommunicationFacilityDamageRepairStatus> list;
        if (ids == null || ids.length == 0) {
            list = this.list().stream()
                    .sorted(Comparator.comparing(CommunicationFacilityDamageRepairStatus::getSystemInsertionTime, Comparator.nullsLast(Comparator.naturalOrder()))
                            .reversed()).collect(Collectors.toList());
        } else {
            list = this.listByIds(Arrays.asList(ids));
        }
        return list;
    }


    /**
     * @param idsList
     * @return
     */
    @Override
    public String deleteData(List<Map<String, Object>> idsList) {
        // 假设所有的 ids 都在每个 Map 中的 "uuid" 键下，提取所有的 ids
        List<String> ids = new ArrayList<>();

        // 遍历 requestBTO 列表，提取每个 Map 中的 "uuid" 键的值
        for (Map<String, Object> entry : idsList) {
            if (entry.containsKey("uuid")) {
                // 获取 "uuid" 并转换为 String 类型
                String uuid = (String) entry.get("uuid");
                ids.add(uuid);
            }
        }

        // 判断是否有 ids
        if (ids.isEmpty()) {
            return "没有提供要删除的 UUID 列表";
        }

        // 使用 removeByIds 方法批量删除
        this.removeByIds(ids);

        return "删除成功";
    }
}
