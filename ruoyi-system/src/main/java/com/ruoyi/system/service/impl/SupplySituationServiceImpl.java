package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.HousingSituation;
import com.ruoyi.system.service.strategy.DataExportStrategy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.SupplySituation;
import com.ruoyi.system.mapper.SupplySituationMapper;
import com.ruoyi.system.service.SupplySituationService;

@Service
public class SupplySituationServiceImpl
        extends ServiceImpl<SupplySituationMapper, SupplySituation>
        implements SupplySituationService, DataExportStrategy {
    /**
     * @param requestBTO
     * @return
     */
    @Override
    public IPage<SupplySituation> getPage(RequestBTO requestBTO) {
        String requestParam = requestBTO.getRequestParams();
        Page<SupplySituation> supplySituation = new Page<>(requestBTO.getCurrentPage(), requestBTO.getPageSize());
        LambdaQueryWrapper<SupplySituation> wrapper = Wrappers.lambdaQuery(SupplySituation.class)
                .like(SupplySituation::getEarthquakeName, requestParam)
                .or()
                .apply("CAST(earthquake_time AS TEXT) LIKE {0}", "%" + requestParam + "%")
                .or()
                .like(SupplySituation::getEarthquakeAreaName, requestParam)
                .or()
                .apply("CAST(report_deadline AS TEXT) LIKE {0}", "%" + requestParam + "%")
                .or()
                .apply("CAST(water_supply_points AS TEXT) LIKE {0}", "%" + requestParam + "%")
                .or()
                .apply("CAST(centralized_water_project_damage AS TEXT) LIKE {0}", "%" + requestParam + "%");

        return this.page(supplySituation, wrapper);
    }

    /**
     * @param requestBTO
     * @return
     */
    @Override
    public List<SupplySituation> exportExcelGetData(RequestBTO requestBTO) {
        String[] ids = requestBTO.getIds();
        List<SupplySituation> list;
        if (ids == null || ids.length == 0) {
            list = this.list().stream()
                    .sorted(Comparator.comparing(SupplySituation::getSystemInsertTime, Comparator.nullsLast(Comparator.naturalOrder()))
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
