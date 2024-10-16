package com.ruoyi.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.AftershockInformation;
import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.listener.AftershockInformationListener;
import com.ruoyi.system.listener.TransferSettlementInfoListener;
import com.ruoyi.system.mapper.EarthquakeListMapper;
import com.ruoyi.system.service.strategy.DataExportStrategy;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.mapper.TransferSettlementInfoMapper;
import com.ruoyi.system.domain.entity.TransferSettlementInfo;
import com.ruoyi.system.service.TransferSettlementInfoService;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TransferSettlementInfoServiceImpl
        extends ServiceImpl<TransferSettlementInfoMapper, TransferSettlementInfo>
        implements TransferSettlementInfoService, DataExportStrategy {

    @Resource
    private TransferSettlementInfoMapper transferSettlementInfoMapper;

    @Resource
    private EarthquakeListMapper earthquakesListMapper;
    @Override
    public IPage<TransferSettlementInfo> getPage(RequestBTO requestBTO) {
        Page<TransferSettlementInfo>
                transferSettlementInfo =
                new Page<>(requestBTO.getCurrentPage(), requestBTO.getPageSize());
        String requestParam = requestBTO.getRequestParams();
        LambdaQueryWrapper<TransferSettlementInfo> lambdaQueryWrapper = new LambdaQueryWrapper<TransferSettlementInfo>()
                .like(TransferSettlementInfo::getEarthquakeName, requestParam)
                .or()
                .like(TransferSettlementInfo::getTransferId, requestParam)
                .or()
                .like(TransferSettlementInfo::getEarthquakeId, requestParam)
                .or()
                .like(TransferSettlementInfo::getEarthquakeAreaName, requestParam)
                .or()
                .apply("CAST(emergency_shelters AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(temporary_shelters AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(newly_transferred AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(cumulative_transferred AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(centralized_settlement AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(distributed_settlement AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(reporting_deadline AS TEXT) LIKE {0}", "%" + requestParam + "%")
                .or()
                .apply("CAST(system_inserttime AS TEXT) LIKE {0}", "%" + requestParam + "%")
                .or()
                .apply("CAST(earthquake_time AS TEXT) LIKE {0}", "%" + requestParam + "%");
        return this.page(transferSettlementInfo, lambdaQueryWrapper);
    }

    @Override
    public List<TransferSettlementInfo> exportExcelGetData(RequestBTO requestBTO) {
        String [] ids = requestBTO.getIds();
        List<TransferSettlementInfo> list;
        if (ids == null || ids.length == 0) {
            list = this.list().stream()
                    .sorted(Comparator.comparing(TransferSettlementInfo::getSystemInserttime, Comparator.nullsLast(Comparator.naturalOrder()))
                            .reversed()).collect(Collectors.toList());
        } else {
            list = this.listByIds(Arrays.asList(ids));
        }
        return list;
    }

    @SneakyThrows
    @Override
    public List<TransferSettlementInfo> importExcelTransferSettlementInfo(MultipartFile file, String userName, String eqId) {
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
        TransferSettlementInfoListener listener = new TransferSettlementInfoListener(baseMapper, actualRows, userName);
        // 读取Excel文件，从第4行开始
        EasyExcel.read(inputStream,TransferSettlementInfo.class, listener).headRowNumber(Integer.valueOf(2)).sheet().doRead();
        // 获取解析后的数据
        List<TransferSettlementInfo> list = listener.getList();
        // 将解析后的数据保存到数据库
        // 遍历解析后的数据，根据地震时间与地震名称查找eqList表中的earthquakeId
        for (TransferSettlementInfo data : list) {
            // 根据地震时间与地震名称查询 earthquakeId
            List<EarthquakeList> earthquakeIdByTimeAndPosition = earthquakesListMapper.findEarthquakeIdByTimeAndPosition(eqId);
            System.out.println("earthquakeIdByTimeAndPosition: " + earthquakeIdByTimeAndPosition);
            // 设置 earthquakeId
            data.setEarthquakeId(earthquakeIdByTimeAndPosition.get(0).getEqid().toString());
            data.setEarthquakeTime(earthquakeIdByTimeAndPosition.get(0).getOccurrenceTime());
            data.setEarthquakeName(earthquakeIdByTimeAndPosition.get(0).getEarthquakeName());
            data.setMagnitude(earthquakeIdByTimeAndPosition.get(0).getMagnitude());
            data.setReportingDeadline(data.getReportingDeadline());
            data.setSystemInserttime(LocalDateTime.now());
        }
        //集合拷贝
//        List<YaanAftershockStatistics> listDOs = BeanUtil.copyToList(list, YaanAftershockStatistics.class);
        saveBatch(list);
        return list;

    }

    @Override
    public List<TransferSettlementInfo> getTotal(String eqid){
        return transferSettlementInfoMapper.getTotal(eqid);
    }

    @Override
    public List<TransferSettlementInfo> getTransferInfo(String eqid){
        return transferSettlementInfoMapper.getTransferInfo(eqid);
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
}
