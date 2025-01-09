package com.ruoyi.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.listener.AftershockInformationListener;
import com.ruoyi.system.listener.PowerSupplyInformationListener;
import com.ruoyi.system.mapper.EarthquakeListMapper;
import com.ruoyi.system.mapper.EqListMapper;
import com.ruoyi.system.service.strategy.DataExportStrategy;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.mapper.PowerSupplyInformationMapper;
import com.ruoyi.system.service.PowerSupplyInformationService;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PowerSupplyInformationServiceImpl
        extends ServiceImpl<PowerSupplyInformationMapper, PowerSupplyInformation>
        implements PowerSupplyInformationService, DataExportStrategy {

    @Resource
    private EarthquakeListMapper earthquakesListMapper;

    @Resource
    private PowerSupplyInformationMapper powerSupplyInformationMapper;

    @Resource
    private EqListMapper eqListMapper;


    /**
     * @param requestBTO
     * @return
     */
    @Override
    public IPage<PowerSupplyInformation> getPage(RequestBTO requestBTO) {
        Page<PowerSupplyInformation> powerSupplyInformation = new Page<>(requestBTO.getCurrentPage(), requestBTO.getPageSize());
        String searchParam = requestBTO.getRequestParams();
        LambdaQueryWrapper<PowerSupplyInformation> queryWrapper = Wrappers.lambdaQuery(PowerSupplyInformation.class)
                .like(PowerSupplyInformation::getEarthquakeId, searchParam)
                .or()
                .like(PowerSupplyInformation::getEarthquakeName, searchParam)
                .or()
                .like(PowerSupplyInformation::getAffectedArea, searchParam)
                .or()
                .apply("cast(total_out_of_service_substations as text) like {0}", searchParam)
                .or()
                .apply("cast(restored_substations as text) like {0}", searchParam)
                .or()
                .apply("cast(to_be_repaired_substations as text) like {0}", searchParam)
                .or()
                .apply("cast(total_trip_circuits as text) like {0}", searchParam)
                .or()
                .apply("cast(restored_circuits as text) like {0}", searchParam)
                .or()
                .apply("cast(to_be_restored_circuits as text) like {0}", searchParam)
                .or()
                .apply("cast(total_blackout_users as text) like {0}", searchParam)
                .or()
                .apply("cast(restored_power_users as text) like {0}", searchParam)
                .or()
                .like(PowerSupplyInformation::getCurrentlyBlackedOutVillages, searchParam)
                .or()
                .apply("cast(emergency_power_users as text) like {0}", searchParam);


        return this.page(powerSupplyInformation, queryWrapper);
    }

    /**
     * @param requestBTO
     * @return
     */
    @Override
    public List<PowerSupplyInformation> exportExcelGetData(RequestBTO requestBTO) {
        String[] ids = requestBTO.getIds();
        List<PowerSupplyInformation> list;
        if (ids == null || ids.length == 0) {
            list = this.list().stream()
                    .sorted(Comparator.comparing(PowerSupplyInformation::getSystemInsertTime, Comparator.nullsLast(Comparator.naturalOrder()))
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
    public IPage<PowerSupplyInformation> searchData(RequestBTO requestBTO) {

        Page<PowerSupplyInformation> powerSupplyInformationPage = new Page<>(requestBTO.getCurrentPage(), requestBTO.getPageSize());

        String requestParams = requestBTO.getRequestParams();
        String eqId = requestBTO.getQueryEqId();
        LambdaQueryWrapper<PowerSupplyInformation> queryWrapper = Wrappers.lambdaQuery(PowerSupplyInformation.class);

        if (MessageConstants.CONDITION_SEARCH.equals(requestBTO.getCondition())) {

            queryWrapper.eq(PowerSupplyInformation::getEarthquakeId, eqId)
                    .like(PowerSupplyInformation::getEarthquakeName, requestParams) // 地震名称
                    .or().like(PowerSupplyInformation::getEarthquakeId, eqId)
                    .apply("to_char(earthquake_time,'YYYY-MM-DD HH24:MI:SS') LIKE {0}", "%" + requestParams + "%")
                    .or().like(PowerSupplyInformation::getEarthquakeId, eqId)
                    .like(PowerSupplyInformation::getAffectedArea, requestParams) // 震区（县/区）\
                    .or().like(PowerSupplyInformation::getEarthquakeId, eqId)
                    .apply("to_char(reporting_deadline,'YYYY-MM-DD HH24:MI:SS') LIKE {0}", "%" + requestParams + "%")
                    .or().like(PowerSupplyInformation::getEarthquakeId, eqId)
                    .like(PowerSupplyInformation::getCurrentlyBlackedOutVillages, requestParams); // 目前主网供电中断村
        }

        if (requestBTO.getCondition().equals(MessageConstants.CONDITION_FILTER)) {

            // 按名称模糊查询
            if (requestBTO.getFormVO().getEarthquakeAreaName() != null && !requestBTO.getFormVO().getEarthquakeAreaName().isEmpty()) {
                queryWrapper.like(PowerSupplyInformation::getAffectedArea, requestBTO.getFormVO().getEarthquakeAreaName())
                        .eq(PowerSupplyInformation::getEarthquakeId, eqId);
            }

            // 筛选 occurrence_time，前端传递了 startTime 和 endTime 时使用
            if (requestBTO.getFormVO().getOccurrenceTime() != null && !requestBTO.getFormVO().getOccurrenceTime().isEmpty()) {

                String[] dates = requestBTO.getFormVO().getOccurrenceTime().split("至");

                LocalDateTime startDate = LocalDateTime.parse(dates[0], DateTimeFormatter.ISO_DATE_TIME);
                LocalDateTime endDate = LocalDateTime.parse(dates[1], DateTimeFormatter.ISO_DATE_TIME);

                queryWrapper.between(PowerSupplyInformation::getReportingDeadline, startDate, endDate)
                        .eq(PowerSupplyInformation::getEarthquakeId, eqId);
            }
        }

        return baseMapper.selectPage(powerSupplyInformationPage, queryWrapper);
    }

    @Override
    public List<PowerSupplyInformation> fromPowerSupplyInformation(String eqid, LocalDateTime time) {
        List<PowerSupplyInformation> powerSupplyInformationList = powerSupplyInformationMapper.fromPowerSupplyInformation(eqid, time);
        return powerSupplyInformationList;
    }


    @Override
    public List<PowerSupplyInformation> importExcelPowerSupplyInformation(MultipartFile file, String userName, String eqId) throws IOException {
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
        PowerSupplyInformationListener listener = new PowerSupplyInformationListener(baseMapper, actualRows, userName);
        // 读取Excel文件，从第4行开始
        EasyExcel.read(inputStream, PowerSupplyInformation.class, listener).headRowNumber(Integer.valueOf(2)).sheet().doRead();
        // 获取解析后的数据
        List<PowerSupplyInformation> list = listener.getList();
        // 将解析后的数据保存到数据库
        // 遍历解析后的数据，根据地震时间与地震名称查找eqList表中的earthquakeId
        for (PowerSupplyInformation data : list) {
            // 根据地震时间与地震名称查询 earthquakeId
            QueryWrapper<EqList> eqListQueryWrapper = new QueryWrapper<>();
            List<EqList> earthquakeIdByTimeAndPosition = eqListMapper.selectList(eqListQueryWrapper);
//            List<EarthquakeList> earthquakeIdByTimeAndPosition = earthquakesListMapper.findEarthquakeIdByTimeAndPosition(eqId);
            System.out.println("earthquakeIdByTimeAndPosition1: " + earthquakeIdByTimeAndPosition);
            // 设置 earthquakeId
            data.setEarthquakeId(earthquakeIdByTimeAndPosition.get(0).getEqid());
            data.setEarthquakeTime(earthquakeIdByTimeAndPosition.get(0).getOccurrenceTime());
            data.setEarthquakeName(earthquakeIdByTimeAndPosition.get(0).getEarthquakeName());
//            data.setMagnitude(earthquakeIdByTimeAndPosition.get(0).getMagnitude());
            data.setReportingDeadline(data.getReportingDeadline());
            data.setSystemInsertTime(LocalDateTime.now());
        }
        //集合拷贝
        saveBatch(list);
        return list;
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

    @Override
    public List<PowerSupplyInformation> getPowerSupply(String eqid) {
        return powerSupplyInformationMapper.getPowerSupply(eqid);
    }
}
