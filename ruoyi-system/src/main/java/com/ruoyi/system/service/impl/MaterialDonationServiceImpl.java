package com.ruoyi.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.CharityOrganizationDonations;
import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.domain.entity.MaterialDonation;
import com.ruoyi.system.domain.entity.SupplySituation;
import com.ruoyi.system.listener.MaterialDonationListener;
import com.ruoyi.system.mapper.EarthquakeListMapper;
import com.ruoyi.system.mapper.MaterialDonationMapper;
import com.ruoyi.system.service.MaterialDonationService;
import com.ruoyi.system.service.strategy.DataExportStrategy;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MaterialDonationServiceImpl extends
        ServiceImpl<MaterialDonationMapper, MaterialDonation>
        implements MaterialDonationService, DataExportStrategy {
    @Resource
    private EarthquakeListMapper earthquakesListMapper;

    @Resource
    private MaterialDonationMapper materialDonationMapper;

    @Override
    public List<MaterialDonation> importExcelMaterialDonation(MultipartFile file, String userName, String eqId) throws IOException, IOException {
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
        MaterialDonationListener listener = new MaterialDonationListener(baseMapper, actualRows, userName);
        // 读取Excel文件，从第4行开始
        EasyExcel.read(inputStream, MaterialDonation.class, listener).headRowNumber(Integer.valueOf(2)).sheet().doRead();
        // 获取解析后的数据
        List<MaterialDonation> list = listener.getList();
        // 将解析后的数据保存到数据库
        // 遍历解析后的数据，根据地震时间与地震名称查找eqList表中的earthquakeId
        for (MaterialDonation data : list) {
            // 根据地震时间与地震名称查询 earthquakeId
            List<EarthquakeList> earthquakeIdByTimeAndPosition = earthquakesListMapper.findEarthquakeIdByTimeAndPosition(eqId);
            System.out.println("earthquakeIdByTimeAndPosition: " + earthquakeIdByTimeAndPosition);
            // 设置 earthquakeId
            data.setEarthquakeId(earthquakeIdByTimeAndPosition.get(0).getEqid().toString());
            data.setEarthquakeTime(earthquakeIdByTimeAndPosition.get(0).getOccurrenceTime());
            data.setEarthquakeName(earthquakeIdByTimeAndPosition.get(0).getEarthquakeName());
//            data.setMagnitude(earthquakeIdByTimeAndPosition.get(0).getMagnitude());
            data.setReportDeadline(data.getReportDeadline());
        }
        //集合拷贝
        saveBatch(list);
        return list;
    }

    @Override
    public IPage getPage(RequestBTO requestBTO) {
        Page<MaterialDonation> barrierLakeSituationPage = new Page<>(requestBTO.getCurrentPage(), requestBTO.getPageSize());
        String requestParam = requestBTO.getRequestParams();
        LambdaQueryWrapper<MaterialDonation> queryWrapper =
                Wrappers.lambdaQuery(MaterialDonation.class)
                        .like(MaterialDonation::getEarthquakeId, requestParam);
        return this.page(barrierLakeSituationPage, queryWrapper);
    }

    @Override
    public List<?> exportExcelGetData(RequestBTO requestBTO) {
        String[] ids = requestBTO.getIds();
        List<MaterialDonation> list;
        if (ids == null || ids.length == 0) {
            list = this.list().stream()
                    .sorted(Comparator.comparing(MaterialDonation::getSystemInsertTime, Comparator.nullsLast(Comparator.naturalOrder()))
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
    public IPage<MaterialDonation> searchData(RequestBTO requestBTO) {

        Page<MaterialDonation> materialDonationPage = new Page<>(requestBTO.getCurrentPage(),requestBTO.getPageSize());

        String requestParams = requestBTO.getRequestParams();
        LambdaQueryWrapper<MaterialDonation> queryWrapper = Wrappers.lambdaQuery(MaterialDonation.class)

                .or().like(MaterialDonation::getEarthquakeName, requestParams) // 地震名称
                .or().apply("to_char(earthquake_time,'YYY-MM-DD HH24:MI:SS') LIKE{0}","%"+ requestParams + "%")
                .or().like(MaterialDonation::getEarthquakeAreaName, requestParams) // 震区（县/区）
                .or().apply("to_char(submission_deadline,'YYY-MM-DD HH24:MI:SS') LIKE{0}","%"+ requestParams + "%")
                .or().like(MaterialDonation::getMaterialDonationCount, requestParams) // 捐赠物资(万件)
                .or().like(MaterialDonation::getDrugsDonationCount, requestParams); // 药品（箱）

        return baseMapper.selectPage(materialDonationPage, queryWrapper);
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
    public List<MaterialDonation> MaterialDonationByEqId(String eqid) {
        return materialDonationMapper.MaterialDonationEqId(eqid);
    }
}
