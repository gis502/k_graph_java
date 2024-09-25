package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.AftershockInformation;
import com.ruoyi.system.mapper.AftershockInformationMapper;
import com.ruoyi.system.service.AftershockInformationService;
import com.ruoyi.system.service.strategy.DataExportStrategy;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.entity.AftershockInformation;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class AftershockInformationServiceImpl extends
        ServiceImpl<AftershockInformationMapper, AftershockInformation>
        implements AftershockInformationService, DataExportStrategy {
    @Override
    public IPage<AftershockInformation> getPage(RequestBTO requestBTO) {
        Page<AftershockInformation>
                aftershockInformation =
                new Page<>(requestBTO.getCurrentPage(), requestBTO.getPageSize());
        String requestParam = requestBTO.getRequestParams();
        LambdaQueryWrapper<AftershockInformation> queryWrapper =
                Wrappers.lambdaQuery(AftershockInformation.class)
                        .like(AftershockInformation::getEarthquakeIdentifier, requestParam)
                        .or()
                        .like(AftershockInformation::getEarthquakeName, requestParam)
                        .or()
                        .apply("CAST(total_aftershocks AS TEXT) = {0}", requestParam)
                        .or()
                        .apply("CAST(magnitude_3_3_9 AS TEXT) = {0}", requestParam)
                        .or()
                        .apply("CAST(magnitude_4_4_9 AS TEXT) = {0}", requestParam)
                        .or()
                        .apply("CAST(magnitude_5_5_9 AS TEXT) = {0}", requestParam)
                        .or()
                        .apply("CAST(system_insert_time AS TEXT) LIKE {0}", "%" + requestParam + "%")
                        .or()
                        .apply("CAST(submission_deadline AS TEXT) LIKE {0}", "%" + requestParam + "%")
                        .or()
                        .like(AftershockInformation::getAffectedArea, requestParam)
                        .or()
                        .apply("CAST(earthquake_time AS TEXT) LIKE {0}", "%" + requestParam + "%")
                        .or()
                        .like(AftershockInformation::getAftershockDistribution, requestParam)
                        .or()
                        .apply("CAST(earthquake_intensity AS TEXT) = {0}", requestParam)
                        .or()
                        .like(AftershockInformation::getDuration, requestParam)
                        .or()
                        .like(AftershockInformation::getAffectedRange, requestParam)
                        .or()
                        .like(AftershockInformation::getRelationshipWithMainshock, requestParam);;


        return null;
    }

    @Override
    public List<AftershockInformation> exportExcelGetData(RequestBTO requestBTO) {
        String [] ids = requestBTO.getIds();
        List<AftershockInformation> list;
        if (ids == null || ids.length == 0) {
            list = this.list().stream()
                    .sorted(Comparator.comparing(AftershockInformation::getSystemInsertTime, Comparator.nullsLast(Comparator.naturalOrder()))
                            .reversed()).collect(Collectors.toList());
        } else {
            list = this.listByIds(Arrays.asList(ids));
        }
        return list;
    }

}
