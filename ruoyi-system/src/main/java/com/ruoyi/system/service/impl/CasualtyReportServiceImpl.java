package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.AftershockInformation;
import com.ruoyi.system.service.strategy.DataExportStrategy;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.mapper.CasualtyReportMapper;
import com.ruoyi.system.domain.entity.CasualtyReport;
import com.ruoyi.system.service.CasualtyReportService;
@Service
public class CasualtyReportServiceImpl
        extends ServiceImpl<CasualtyReportMapper, CasualtyReport>
        implements CasualtyReportService, DataExportStrategy {

    @Override
    public List<CasualtyReport> exportExcelGetData(RequestBTO requestBTO) {
        String [] ids = requestBTO.getIds();
        List<CasualtyReport> list;
        if (ids == null || ids.length == 0) {
            list = this.list().stream()
                    .sorted(Comparator.comparing(CasualtyReport::getSystemInsertTime, Comparator.nullsLast(Comparator.naturalOrder()))
                            .reversed()).collect(Collectors.toList());
        } else {
            list = this.listByIds(Arrays.asList(ids));
        }
        return list;
    }

    @Override
    public IPage<CasualtyReport> getPage(RequestBTO requestBTO) {
        Page<CasualtyReport>
                casualtyReport =
                new Page<>(requestBTO.getCurrentPage(), requestBTO.getPageSize());
        String requestParam = requestBTO.getRequestParams();
        // 构造 LambdaQueryWrapper
        LambdaQueryWrapper<CasualtyReport> lambdaQueryWrapper = new LambdaQueryWrapper<CasualtyReport>()
                .like(CasualtyReport::getEarthquakeIdentifier, requestParam)
                .or()
                .like(CasualtyReport::getEarthquakeName, requestParam)
                .or()
                .like(CasualtyReport::getAffectedAreaName, requestParam)
                .or()
                .apply("CAST(newly_deceased AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(newly_missing AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(newly_injured AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(total_deceased AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(total_missing AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(total_injured AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(system_insert_time AS TEXT) LIKE {0}", "%" + requestParam + "%")
                .or()
                .apply("CAST(submission_deadline AS TEXT) LIKE {0}", "%" + requestParam + "%")
                .or()
                .apply("CAST(earthquake_time AS TEXT) LIKE {0}", "%" + requestParam + "%")
                .or()
                .apply("CAST(affected_population AS TEXT) = {0}", requestParam)
                .or()
                .like(CasualtyReport::getAffectedCountyDistrict, requestParam)
                .or()
                .like(CasualtyReport::getInjuryDegree, requestParam)
                .or()
                .apply("CAST(quantity AS TEXT) = {0}", requestParam)
                .or()
                .like(CasualtyReport::getCause, requestParam)
                .or()
                .like(CasualtyReport::getSiteNature, requestParam)
                .or()
                .apply("CAST(age AS TEXT) = {0}", requestParam)
                .or()
                .like(CasualtyReport::getGender, requestParam)
                .or()
                .like(CasualtyReport::getEthnicity, requestParam)
                .or()
                .like(CasualtyReport::getCasualtyType, requestParam)
                .or()
                .apply("CAST(death_count AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(infection_count AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(poisoning_count AS TEXT) = {0}", requestParam)
                .or()
                .like(CasualtyReport::getTransmissionPath, requestParam)
                .or()
                .apply("CAST(suspected_count AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(critical_count AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(poisoned_count AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(transferred_count AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(direct_economic_loss AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(indirect_economic_loss AS TEXT) = {0}", requestParam)
                .or()
                .like(CasualtyReport::getEventId, requestParam)
                .or()
                .like(CasualtyReport::getMeasuresTaken, requestParam);
        return this.page(casualtyReport, lambdaQueryWrapper);
    }
}
