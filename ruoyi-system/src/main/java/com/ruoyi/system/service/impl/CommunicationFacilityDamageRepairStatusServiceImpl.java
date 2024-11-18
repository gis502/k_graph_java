package com.ruoyi.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.CommunicationFacilityDamageRepairStatus;
import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.domain.entity.TrafficControlSections;
import com.ruoyi.system.listener.CommunicationFacilityDamageRepairStatusListener;
import com.ruoyi.system.mapper.CommunicationFacilityDamageRepairStatusMapper;
import com.ruoyi.system.mapper.EarthquakeListMapper;
import com.ruoyi.system.service.CommunicationFacilityDamageRepairStatusService;
import com.ruoyi.system.service.strategy.DataExportStrategy;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
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
                .like(CommunicationFacilityDamageRepairStatus::getEarthquakeId,requestParam)
                .or()
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

    @Override
    public IPage<CommunicationFacilityDamageRepairStatus> searchData(RequestBTO requestBTO) {

        Page<CommunicationFacilityDamageRepairStatus> communicationFacilityDamageRepairStatusPage = new Page<>(requestBTO.getCurrentPage(),requestBTO.getPageSize());

        String requestParams = requestBTO.getRequestParams();
        String eqId = requestBTO.getQueryEqId();
        LambdaQueryWrapper<CommunicationFacilityDamageRepairStatus> queryWrapper = Wrappers.lambdaQuery(CommunicationFacilityDamageRepairStatus.class)

                .eq(CommunicationFacilityDamageRepairStatus::getEarthquakeId, eqId)
                .like(CommunicationFacilityDamageRepairStatus::getEarthquakeName, requestParams) // 地震名称
                .or().like(CommunicationFacilityDamageRepairStatus::getEarthquakeId, eqId)
                .apply("to_char(earthquake_time,'YYYY-MM-DD HH24:MI:SS') LIKE {0}","%"+ requestParams + "%")
                .or().like(CommunicationFacilityDamageRepairStatus::getEarthquakeId, eqId)
                .like(CommunicationFacilityDamageRepairStatus::getEarthquakeZoneName, requestParams) // 震区（县/区）
                .or().like(CommunicationFacilityDamageRepairStatus::getEarthquakeId, eqId)
                .apply("to_char(reporting_deadline,'YYYY-MM-DD HH24:MI:SS') LIKE {0}","%"+ requestParams + "%")
                .or().like(CommunicationFacilityDamageRepairStatus::getEarthquakeId, eqId)
                .like(CommunicationFacilityDamageRepairStatus::getCurrentInterruptedVillagesCount, requestParams); // 目前通信中断村

        return baseMapper.selectPage(communicationFacilityDamageRepairStatusPage, queryWrapper);
    }

    @Override
    public List<CommunicationFacilityDamageRepairStatus> fromCommunicationFacilityDamageRepairStatus(String eqid, LocalDateTime time) {
        List<CommunicationFacilityDamageRepairStatus> damageRepairStatusList = communicationFacilityDamageRepairStatusMapper.fromCommunicationFacilityDamageRepairStatus(eqid, time);
        return damageRepairStatusList;
    }


    @Resource
    private EarthquakeListMapper earthquakesListMapper;
    @Override
    public List<CommunicationFacilityDamageRepairStatus> importExcelCommunicationFacilityDamageRepairStatus(MultipartFile file, String userName, String eqId) throws IOException {
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
        CommunicationFacilityDamageRepairStatusListener listener = new CommunicationFacilityDamageRepairStatusListener(baseMapper, actualRows, userName);
        // 读取Excel文件，从第4行开始
        EasyExcel.read(inputStream, CommunicationFacilityDamageRepairStatus.class, listener).headRowNumber(Integer.valueOf(2)).sheet().doRead();
        // 获取解析后的数据
        List<CommunicationFacilityDamageRepairStatus> list = listener.getList();
        // 将解析后的数据保存到数据库
        // 遍历解析后的数据，根据地震时间与地震名称查找eqList表中的earthquakeId
        for (CommunicationFacilityDamageRepairStatus data : list) {
            // 根据地震时间与地震名称查询 earthquakeId
            List<EarthquakeList> earthquakeIdByTimeAndPosition = earthquakesListMapper.findEarthquakeIdByTimeAndPosition(eqId);
            System.out.println("earthquakeIdByTimeAndPosition: " + earthquakeIdByTimeAndPosition);
            // 设置 earthquakeId
            data.setEarthquakeId(earthquakeIdByTimeAndPosition.get(0).getEqid().toString());
            data.setEarthquakeTime(earthquakeIdByTimeAndPosition.get(0).getOccurrenceTime());
            data.setEarthquakeName(earthquakeIdByTimeAndPosition.get(0).getEarthquakeName());
//            data.(earthquakeIdByTimeAndPosition.get(0).getMagnitude());
            data.setReportingDeadline(data.getReportingDeadline());
            data.setSystemInsertionTime(LocalDateTime.now());
        }
        //集合拷贝
//        List<YaanAftershockStatistics> listDOs = BeanUtil.copyToList(list, YaanAftershockStatistics.class);
        saveBatch(list);
        return list;
    }


    // 判断某行是否为空
    private boolean isRowEmpty(Row row) {
        for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    @Resource
    public CommunicationFacilityDamageRepairStatusMapper communicationFacilityDamageRepairStatusMapper;
    @Override
    public List<CommunicationFacilityDamageRepairStatus> facility(String eqid) {
        return communicationFacilityDamageRepairStatusMapper.facility(eqid);
    }
}
