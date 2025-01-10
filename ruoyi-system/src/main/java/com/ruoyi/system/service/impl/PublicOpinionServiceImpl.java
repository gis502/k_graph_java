package com.ruoyi.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.listener.BarrierLakeSituationListener;
import com.ruoyi.system.listener.PublicOpinionListener;
import com.ruoyi.system.mapper.EarthquakeListMapper;
import com.ruoyi.system.mapper.EqListMapper;
import com.ruoyi.system.mapper.PublicOpinionMapper;
import com.ruoyi.system.service.PublicOpinionService;
import com.ruoyi.system.service.strategy.DataExportStrategy;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class PublicOpinionServiceImpl
        extends ServiceImpl<PublicOpinionMapper, PublicOpinion>
        implements PublicOpinionService, DataExportStrategy {



    @Resource
    private EarthquakeListMapper earthquakesListMapper;

    @Resource
    private PublicOpinionMapper publicOpinionMapper;

    @Resource
    private EqListMapper eqListMapper;

    @Override
    public List<PublicOpinion> importExcelPublicOpinion(MultipartFile file, String userName, String eqId) throws IOException {
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
        PublicOpinionListener listener = new PublicOpinionListener(baseMapper, actualRows, userName);
        // 读取Excel文件，从第4行开始
        EasyExcel.read(inputStream, PublicOpinion.class, listener).headRowNumber(Integer.valueOf(2)).sheet().doRead();
        // 获取解析后的数据
        List<PublicOpinion> list = listener.getList();
        // 将解析后的数据保存到数据库
        // 遍历解析后的数据，根据地震时间与地震名称查找eqList表中的earthquakeId
        for (PublicOpinion data : list) {
            // 根据地震时间与地震名称查询 earthquakeId
            QueryWrapper<EqList> eqListQueryWrapper = new QueryWrapper<>();
            eqListQueryWrapper.eq("eqid", eqId);
            List<EqList> earthquakeIdByTimeAndPosition = eqListMapper.selectList(eqListQueryWrapper);
//            List<EarthquakeList> earthquakeIdByTimeAndPosition = earthquakesListMapper.findEarthquakeIdByTimeAndPosition(eqId);
            System.out.println("earthquakeIdByTimeAndPosition: " + earthquakeIdByTimeAndPosition);
            // 设置 earthquakeId
            data.setEarthquakeId(earthquakeIdByTimeAndPosition.get(0).getEqid());
            data.setEarthquakeTime(earthquakeIdByTimeAndPosition.get(0).getOccurrenceTime());
            data.setEarthquakeName(earthquakeIdByTimeAndPosition.get(0).getEarthquakeName());
//            data.setMagnitude(earthquakeIdByTimeAndPosition.get(0).getMagnitude());
            data.setSubmissionDeadline(data.getSubmissionDeadline());
        }
        //集合拷贝
        saveBatch(list);
        return list;
    }

    @Override
    public IPage getPage(RequestBTO requestBTO) {
        Page<PublicOpinion> publicOpinionPage = new Page<>(requestBTO.getCurrentPage(), requestBTO.getPageSize());
        String requestParam = requestBTO.getRequestParams();
        LambdaQueryWrapper<PublicOpinion> queryWrapper =
                Wrappers.lambdaQuery(PublicOpinion.class)
                        .like(PublicOpinion::getEarthquakeId, requestParam);
        return this.page(publicOpinionPage, queryWrapper);
    }

    @Override
    public List<?> exportExcelGetData(RequestBTO requestBTO) {
        String [] ids = requestBTO.getIds();
        List<PublicOpinion> list;
        if (ids == null || ids.length == 0) {
            list = this.list().stream()
                    .sorted(Comparator.comparing(PublicOpinion::getSystemInsertTime, Comparator.nullsLast(Comparator.naturalOrder()))
                            .reversed()).collect(Collectors.toList());
        } else {
            list = this.listByIds(Arrays.asList(ids));
        }
        return list;
    }

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
    public IPage<PublicOpinion> searchData(RequestBTO requestBTO) {

        Page<PublicOpinion> publicOpinionPage = new Page<>(requestBTO.getCurrentPage(),requestBTO.getPageSize());

        String requestParams = requestBTO.getRequestParams();
        String eqId = requestBTO.getQueryEqId();
        LambdaQueryWrapper<PublicOpinion> queryWrapper = Wrappers.lambdaQuery(PublicOpinion.class);

        if (MessageConstants.CONDITION_SEARCH.equals(requestBTO.getCondition())) {

            queryWrapper.eq(PublicOpinion::getEarthquakeId, eqId)
                    .like(PublicOpinion::getEarthquakeName, requestParams) // 地震名称
                    .or().like(PublicOpinion::getEarthquakeId, eqId)
                    .apply("to_char(earthquake_time,'YYYY-MM-DD HH24:MI:SS') LIKE {0}", "%" + requestParams + "%")
                    .or().like(PublicOpinion::getEarthquakeId, eqId)
                    .like(PublicOpinion::getEarthquakeZoneName, requestParams) // 震区（县/区）
                    .or().like(PublicOpinion::getEarthquakeId, eqId)
                    .apply("to_char(submission_deadline,'YYYY-MM-DD HH24:MI:SS') LIKE {0}", "%" + requestParams + "%");
        }

        if (requestBTO.getCondition().equals(MessageConstants.CONDITION_FILTER)) {

            // 按名称模糊查询
            if (requestBTO.getFormVO().getEarthquakeAreaName() != null && !requestBTO.getFormVO().getEarthquakeAreaName().isEmpty()) {
                queryWrapper.like(PublicOpinion::getEarthquakeZoneName, requestBTO.getFormVO().getEarthquakeAreaName())
                        .eq(PublicOpinion::getEarthquakeId, eqId);
            }

            // 筛选 occurrence_time，前端传递了 startTime 和 endTime 时使用
            if (requestBTO.getFormVO().getOccurrenceTime() != null && !requestBTO.getFormVO().getOccurrenceTime().isEmpty()) {

                String[] dates = requestBTO.getFormVO().getOccurrenceTime().split("至");

                LocalDateTime startDate = LocalDateTime.parse(dates[0], DateTimeFormatter.ISO_DATE_TIME);
                LocalDateTime endDate = LocalDateTime.parse(dates[1], DateTimeFormatter.ISO_DATE_TIME);

                queryWrapper.between(PublicOpinion::getSubmissionDeadline, startDate, endDate)
                        .eq(PublicOpinion::getEarthquakeId, eqId);
            }
        }

        return baseMapper.selectPage(publicOpinionPage, queryWrapper);
    }

    @Override
    public List<PublicOpinion> fromPublicOpinion(String eqid, LocalDateTime time) {
        List<PublicOpinion> publicOpinionList = publicOpinionMapper.fromPublicOpinion(eqid, time);
        return publicOpinionList;
    }


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
    public List<PublicOpinion> getpublicopinion(String eqid) {
        return publicOpinionMapper.getpublicopinion(eqid);
    }
}

