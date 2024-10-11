package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.AftershockInformation;
import com.ruoyi.system.service.strategy.DataExportStrategy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.HousingSituation;
import com.ruoyi.system.mapper.HousingSituationMapper;
import com.ruoyi.system.service.HousingSituationService;

@Service
public class HousingSituationServiceImpl
        extends ServiceImpl<HousingSituationMapper, HousingSituation>
        implements HousingSituationService, DataExportStrategy {
    /**
     * @param requestBTO
     * @return
     */
    @Override
    public IPage<HousingSituation> getPage(RequestBTO requestBTO) {
        String requestParam = requestBTO.getRequestParams();
        Page<HousingSituation> housingSituation = new Page<>(requestBTO.getCurrentPage(), requestBTO.getPageSize());
        LambdaQueryWrapper<HousingSituation> wrapper = Wrappers.lambdaQuery(HousingSituation.class)
                .like(HousingSituation::getEarthquakeName, requestParam)
                .or()
                .apply("CAST(earthquake_time AS TEXT) LIKE {0}", "%" + requestParam + "%")
                .or()
                .like(HousingSituation::getAffectedAreaName, requestParam)
                .or()
                .apply("CAST(submission_deadline AS TEXT) LIKE {0}", "%" + requestParam + "%")
                .or()
                .apply("CAST(currently_damaged AS TEXT) LIKE {0}", "%" + requestParam + "%")
                .or()
                .apply("CAST(currently_disabled AS TEXT) LIKE {0}", "%" + requestParam + "%")
                .or()
                .apply("CAST(currently_restricted AS TEXT) LIKE {0}", "%" + requestParam + "%")
                .or()
                .apply("CAST(currently_available AS TEXT) LIKE {0}", "%" + requestParam + "%");

        return this.page(housingSituation, wrapper);
    }

    /**
     * @param requestBTO
     * @return
     */
    @Override
    public List<HousingSituation> exportExcelGetData(RequestBTO requestBTO) {
        String[] ids = requestBTO.getIds();
        List<HousingSituation> list;
        if (ids == null || ids.length == 0) {
            list = this.list().stream()
                    .sorted(Comparator.comparing(HousingSituation::getSystemInsertTime, Comparator.nullsLast(Comparator.naturalOrder()))
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
