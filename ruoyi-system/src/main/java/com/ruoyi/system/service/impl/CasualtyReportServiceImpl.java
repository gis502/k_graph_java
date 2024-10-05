package com.ruoyi.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.listener.CasualtyReportListener;
import com.ruoyi.system.mapper.EarthquakeListMapper;
import com.ruoyi.system.service.strategy.DataExportStrategy;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.mapper.CasualtyReportMapper;
import com.ruoyi.system.domain.entity.CasualtyReport;
import com.ruoyi.system.service.CasualtyReportService;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Service
public class CasualtyReportServiceImpl
        extends ServiceImpl<CasualtyReportMapper, CasualtyReport>
        implements CasualtyReportService, DataExportStrategy {


    @Resource
    private EarthquakeListMapper earthquakesListMapper;

    @Resource
    private CasualtyReportMapper casualtyReportMapper;

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


    @Override
    public List<CasualtyReport> importExcelCasualtyReport(MultipartFile file, String userName, String eqId) throws IOException {
        InputStream inputStream = file.getInputStream();
        // 读取总行数（略过表头）
        int totalRows = WorkbookFactory.create(inputStream).getSheetAt(0).getPhysicalNumberOfRows() - 4;
        inputStream.close();
        // 重新获取 InputStream
        inputStream = file.getInputStream();
        CasualtyReportListener listener = new CasualtyReportListener(baseMapper, totalRows, userName);
        // 读取Excel文件，从第4行开始
        EasyExcel.read(inputStream,CasualtyReport.class, listener).headRowNumber(Integer.valueOf(2)).sheet().doRead();
        // 获取解析后的数据
        List<CasualtyReport> list = listener.getList();
        // 将解析后的数据保存到数据库
        // 遍历解析后的数据，根据地震时间与地震名称查找eqList表中的earthquakeId
        for (CasualtyReport data : list) {
            // 根据地震时间与地震名称查询 earthquakeId
            List<EarthquakeList> earthquakeIdByTimeAndPosition = earthquakesListMapper.findEarthquakeIdByTimeAndPosition(eqId);
            System.out.println("earthquakeIdByTimeAndPosition: " + earthquakeIdByTimeAndPosition);
            // 设置 earthquakeId
            data.setEarthquakeIdentifier(earthquakeIdByTimeAndPosition.get(0).getEqid().toString());
            data.setEarthquakeTime(earthquakeIdByTimeAndPosition.get(0).getOccurrenceTime());
            data.setEarthquakeName(earthquakeIdByTimeAndPosition.get(0).getEarthquakeName());
            data.setMagnitude(earthquakeIdByTimeAndPosition.get(0).getMagnitude());
            data.setSubmissionDeadline(data.getSubmissionDeadline());
            data.setSystemInsertTime(LocalDateTime.now());
        }
        //集合拷贝
//        List<YaanAftershockStatistics> listDOs = BeanUtil.copyToList(list, YaanAftershockStatistics.class);
        saveBatch(list);
        return list;
    }

    @Override
    public CasualtyReport getCasualtiesStatsById(String eqid) {

        return casualtyReportMapper.getCasualtiesStatsById(eqid);
    }
}
