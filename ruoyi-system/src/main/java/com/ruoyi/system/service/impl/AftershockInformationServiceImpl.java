package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.alibaba.excel.EasyExcel;
import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.system.domain.entity.AftershockInformation;
import com.ruoyi.system.domain.entity.CasualtyReport;
import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.domain.entity.EqList;
import com.ruoyi.system.domain.vo.FormVO;
import com.ruoyi.system.listener.AftershockInformationListener;
import com.ruoyi.system.mapper.EarthquakeListMapper;
import com.ruoyi.system.mapper.EqListMapper;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.mapper.AftershockInformationMapper;
import com.ruoyi.system.service.AftershockInformationService;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.system.service.strategy.DataExportStrategy;

import java.util.Arrays;
import java.util.Comparator;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class AftershockInformationServiceImpl extends
        ServiceImpl<AftershockInformationMapper, AftershockInformation>
        implements AftershockInformationService, DataExportStrategy {
    @Autowired
    private AftershockInformationMapper aftershockInformationMapper;

    @Resource
    private EqListMapper eqListMapper;

    @Override
    public IPage<AftershockInformation> getPage(RequestBTO requestBTO) {
        Page<AftershockInformation> aftershockInformation = new Page<>(requestBTO.getCurrentPage(), requestBTO.getPageSize());
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


        return this.page(aftershockInformation, queryWrapper);
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


    /**
     * 获取最新余震数据
     *
     * @param eqid
     * @return
     */
    @Override
    public Map<String, Object> getLatestAftershockMagnitude(String eqid) {
        Map<String, Object> aftershockData = aftershockInformationMapper.getLatestAftershockData(eqid);
        // 检查数据是否为 null 或空
        if (aftershockData == null || aftershockData.isEmpty()) {
            // 返回默认值
            aftershockData = new HashMap<>();
            aftershockData.put("magnitude_3_0_to_3_9", 0);
            aftershockData.put("magnitude_4_0_to_4_9", 0);
            aftershockData.put("magnitude_5_0_to_5_9", 0);
            aftershockData.put("magnitude_6", 0);
        } else {
            // 格式化 submission_deadline 字段
            Object submissionDeadlineObj = aftershockData.get("submission_deadline");
            if (submissionDeadlineObj != null) {
                String submissionDeadline = submissionDeadlineObj.toString();

                // 使用自定义 DateTimeFormatter 解析日期格式 'yyyy-MM-dd HH:mm:ss.S'
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                LocalDateTime localDateTime = LocalDateTime.parse(submissionDeadline, formatter);

                // 将日期格式化为 'yyyy-MM-dd HH:mm:ss'
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = localDateTime.format(outputFormatter);

                aftershockData.put("submission_deadline", formattedDateTime); // 更新 Map 中的时间格式
            }
        }

        // 使用 System.out.println 替代日志记录
        System.out.println("Aftershock data: " + aftershockData);

        return aftershockData;
    }



    @Resource
    private EarthquakeListMapper earthquakesListMapper;
    @Override
    public List<AftershockInformation> importExcelAftershockInformation(MultipartFile file, String userName, String eqId) throws IOException {
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
        AftershockInformationListener listener = new AftershockInformationListener(baseMapper, totalRows, userName);
        // 读取Excel文件，从第4行开始
        EasyExcel.read(inputStream,AftershockInformation.class, listener).headRowNumber(Integer.valueOf(2)).sheet().doRead();
        // 获取解析后的数据
        List<AftershockInformation> list = listener.getList();
        // 将解析后的数据保存到数据库
        // 遍历解析后的数据，根据地震时间与地震名称查找eqList表中的earthquakeId
        for (AftershockInformation data : list) {
            // 根据地震时间与地震名称查询 earthquakeId
            QueryWrapper<EqList> eqListQueryWrapper = new QueryWrapper<>();
            List<EqList> earthquakeIdByTimeAndPosition = eqListMapper.selectList(eqListQueryWrapper);
//            List<EarthquakeList> earthquakeIdByTimeAndPosition = earthquakesListMapper.findEarthquakeIdByTimeAndPosition(eqId);
            System.out.println("earthquakeIdByTimeAndPosition1: " + earthquakeIdByTimeAndPosition);
            // 设置 earthquakeId
            data.setEarthquakeIdentifier(earthquakeIdByTimeAndPosition.get(0).getEqid());
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
    public List<Map<String, Object>> getTotal(String eqid) {
        List<Map<String, Object>> aftershockDataList = aftershockInformationMapper.getTotal(eqid);
        return aftershockDataList;
    }

    @Override
    public List<Map<String, Object>> getAfterShockInformation(String eqid) {
        List<Map<String, Object>> aftershockDataList = aftershockInformationMapper.getAfterShockInformation(eqid);
        return aftershockDataList;
    }

    @Override
    public IPage<AftershockInformation> searchData(RequestBTO requestBTO) {
        Page<AftershockInformation> aftershockInformationPage =
                new Page<>(requestBTO.getCurrentPage(),requestBTO.getPageSize());
        String requestParams = requestBTO.getRequestParams();
        System.out.println(requestParams);
        String eqId = requestBTO.getQueryEqId();
        LambdaQueryWrapper<AftershockInformation> queryWrapper = Wrappers.lambdaQuery(AftershockInformation.class);
        //模糊搜索
        if (requestBTO.getCondition().equals(MessageConstants.CONDITION_SEARCH)) {
            queryWrapper.like(AftershockInformation::getEarthquakeName, requestParams)
                    .or().like(AftershockInformation::getEarthquakeIdentifier, eqId)
                    .apply("to_char(earthquake_time,'YYYY-MM-DD HH24:MI:SS') LIKE {0}", "%" + requestParams + "%")
                    .or().like(AftershockInformation::getEarthquakeIdentifier, eqId)
                    .apply("CAST(magnitude AS TEXT) LIKE {0}", requestParams = "%" + requestParams + "%")
                    .or().like(AftershockInformation::getEarthquakeIdentifier,eqId)
                    .like(AftershockInformation::getAffectedArea, requestParams)
                    .or().like(AftershockInformation::getEarthquakeIdentifier, eqId)
                    .apply("to_char(submission_deadline,'YYYY-MM-DD HH24:MI:SS') LIKE {0}", "%" + requestParams + "%");
        }
        if (requestBTO.getCondition().equals(MessageConstants.CONDITION_FILTER)) {

                // 筛选，按名称模糊查询
                if (requestBTO.getFormVO().getEarthquakeAreaName() != null && !requestBTO.getFormVO().getEarthquakeAreaName().isEmpty()) {
                    queryWrapper.like(AftershockInformation::getAffectedArea, requestBTO.getFormVO().getEarthquakeAreaName())
                            .eq(AftershockInformation::getEarthquakeIdentifier, eqId);
                }

                // 筛选 occurrence_time，前端传递了 startTime 和 endTime 时使用
                if (requestBTO.getFormVO().getOccurrenceTime() != null && !requestBTO.getFormVO().getOccurrenceTime().isEmpty()) {

                    String[] dates = requestBTO.getFormVO().getOccurrenceTime().split("至");

                    LocalDateTime startDate = LocalDateTime.parse(dates[0], DateTimeFormatter.ISO_DATE_TIME);
                    LocalDateTime endDate = LocalDateTime.parse(dates[1], DateTimeFormatter.ISO_DATE_TIME);

                    queryWrapper.between(AftershockInformation::getSubmissionDeadline, startDate, endDate)
                            .eq(AftershockInformation::getEarthquakeIdentifier, eqId);
                }

        }
        return baseMapper.selectPage(aftershockInformationPage, queryWrapper);
    }

    @Override
    public List<Map<String, Object>> fromAftershock(String eqid, LocalDateTime time) {
        List<Map<String, Object>> fromAftershock= aftershockInformationMapper.fromAftershock(eqid,time);
        return fromAftershock;
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
