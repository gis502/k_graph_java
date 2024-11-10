package com.ruoyi.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.AftershockInformation;
import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.listener.CasualtyReportListener;
import com.ruoyi.system.mapper.EarthquakeListMapper;
import com.ruoyi.system.service.strategy.DataExportStrategy;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
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

    // 策略类中的 deleteData 方法定义，支持批量删除
    @Override
    public String deleteData(List<Map<String, Object>> requestBTO) {
        // 假设所有的 ids 都在每个 Map 中的 "uuid" 键下，提取所有的 ids
        List<String> ids = new ArrayList<>();

        // 遍历 requestBTO 列表，提取每个 Map 中的 "uuid" 键的值
        for (Map<String, Object> entry : requestBTO) {
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

    @Override
    public IPage<CasualtyReport> searchData(RequestBTO requestBTO) {
        Page<CasualtyReport> casualtyReportPage = new Page<>(requestBTO.getCurrentPage(),requestBTO.getPageSize());

        String requestParams = requestBTO.getRequestParams();
        String eqId = requestBTO.getQueryEqId();
        LambdaQueryWrapper<CasualtyReport> queryWrapper = Wrappers.lambdaQuery(CasualtyReport.class)
                .eq(CasualtyReport::getEarthquakeIdentifier, eqId)
                .like(CasualtyReport::getEarthquakeName, requestParams)
                .or().like(CasualtyReport::getEarthquakeIdentifier, eqId)
                .apply("to_char(earthquake_time,'YYYY-MM-DD HH24:MI:SS') LIKE {0}","%"+ requestParams + "%")
                .or().like(CasualtyReport::getEarthquakeIdentifier, eqId)
                .apply("CAST(magnitude AS TEXT) LIKE {0}", requestParams="%" + requestParams + "%")
                .or().like(CasualtyReport::getEarthquakeIdentifier, eqId)
                .like(CasualtyReport::getAffectedAreaName, requestParams)
                .or().like(CasualtyReport::getEarthquakeIdentifier, eqId)
                .apply("to_char(submission_deadline,'YYYY-MM-DD HH24:MI:SS') LIKE {0}","%"+ requestParams + "%");

        return baseMapper.selectPage(casualtyReportPage, queryWrapper);
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
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
// 获取总行数，略过前2行表头和后2行表尾
        int totalRows = sheet.getPhysicalNumberOfRows();
        int startRow = 2;  // 从第3行开始读取数据（略过前2行）
        int endRow = totalRows - 2;  // 不读取最后2行

        int actualRows = 0;
// 遍历中间的数据行
        for (int i = startRow; i < endRow; i++) {
            Row row = sheet.getRow(i);

            if (row != null && !isRowEmpty(row)) {
                actualRows++;  // 只计入非空行
            }
        }
        inputStream.close();
        // 重新获取 InputStream
        inputStream = file.getInputStream();
        CasualtyReportListener listener = new CasualtyReportListener(baseMapper, actualRows, userName);
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

    @Override
    public List<CasualtyReport> getTotal(String eqid) {
        return casualtyReportMapper.getTotal(eqid);
    }

    @Override
    public List<CasualtyReport> getCasualty(String eqid) {
        return casualtyReportMapper.getCasualty(eqid);
    }
    // 判断某行是否为空
    private boolean isRowEmpty(Row row) {
        for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;  // 只要有一个单元格不为空，这行就不算空行
            }
        }
        return true;  // 所有单元格都为空，算作空行
    }

}
