package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.CasualtyReport;
import com.ruoyi.system.service.strategy.DataExportStrategy;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.mapper.MeetingsMapper;
import com.ruoyi.system.domain.entity.Meetings;
import com.ruoyi.system.service.MeetingsService;
@Service
public class MeetingsServiceImpl
        extends ServiceImpl<MeetingsMapper, Meetings>
        implements MeetingsService, DataExportStrategy {
    @Override
    public IPage<Meetings> getPage(RequestBTO requestBTO) {
        Page<Meetings> meetingsPage = new Page<>(requestBTO.getCurrentPage(), requestBTO.getPageSize());
        String requestParam = requestBTO.getRequestParams();

        LambdaQueryWrapper<Meetings> lambdaQueryWrapper = new LambdaQueryWrapper<Meetings>()
                .like(Meetings::getEarthquakeId, requestParam)
                .or()
                .like(Meetings::getEarthquakeAreaName, requestParam)
                .or()
                .like(Meetings::getEarthquakeName, requestParam)
                .or()
                .apply("CAST(meeting_count AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(activity_count AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(brief_report_count AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(notice_count AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(meeting_minutes_count AS TEXT) = {0}", requestParam);

       return this.page(meetingsPage, lambdaQueryWrapper);
    }

    @Override
    public List<Meetings> exportExcelGetData(RequestBTO requestBTO) {
        String [] ids = requestBTO.getIds();
        List<Meetings> list;
        if (ids == null || ids.length == 0) {
            list = this.list().stream()
                    .sorted(Comparator.comparing(Meetings::getSystemInsertTime, Comparator.nullsLast(Comparator.naturalOrder()))
                            .reversed()).collect(Collectors.toList());
        } else {
            list = this.listByIds(Arrays.asList(ids));
        }
        return list;
    }

}
