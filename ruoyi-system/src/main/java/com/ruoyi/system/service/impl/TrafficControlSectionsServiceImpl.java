package com.ruoyi.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.domain.entity.Meetings;
import com.ruoyi.system.listener.MeetingsListener;
import com.ruoyi.system.listener.trafficControlSectionsListener;
import com.ruoyi.system.mapper.EarthquakeListMapper;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.TrafficControlSections;
import com.ruoyi.system.mapper.TrafficControlSectionsMapper;
import com.ruoyi.system.service.TrafficControlSectionsService;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TrafficControlSectionsServiceImpl extends ServiceImpl<TrafficControlSectionsMapper, TrafficControlSections> implements TrafficControlSectionsService{

    @Autowired
    private EarthquakeListMapper earthquakesListMapper;
    @Override
    public List<TrafficControlSections> importExcelTrafficControlSections(MultipartFile file, String userName, String eqId) throws IOException {
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
        trafficControlSectionsListener listener = new trafficControlSectionsListener(baseMapper, actualRows, userName);
        // 读取Excel文件，从第3行开始
        EasyExcel.read(inputStream, TrafficControlSections.class, listener).headRowNumber(Integer.valueOf(2)).sheet().doRead();
        // 获取解析后的数据
        List<TrafficControlSections> list = listener.getList();
        System.out.println("list: " + list);
        // 将解析后的数据保存到数据库
        // 遍历解析后的数据，根据地震时间与地震名称查找eqList表中的earthquakeId
        for (TrafficControlSections data : list) {
            // 根据地震时间与地震名称查询 earthquakeId
            List<EarthquakeList> earthquakeIdByTimeAndPosition = earthquakesListMapper.findEarthquakeIdByTimeAndPosition(eqId);
            System.out.println("earthquakeIdByTimeAndPosition: " + earthquakeIdByTimeAndPosition);
            // 设置 earthquakeId
            data.setEarthquakeId(earthquakeIdByTimeAndPosition.get(0).getEqid().toString());
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
}
