package com.ruoyi.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.listener.AftershockInformationListener;
import com.ruoyi.system.mapper.EarthquakeListMapper;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.AftershockInformation;
import com.ruoyi.system.mapper.AftershockInformationMapper;
import com.ruoyi.system.service.AftershockInformationService;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AftershockInformationServiceImpl extends ServiceImpl<AftershockInformationMapper, AftershockInformation> implements AftershockInformationService{


    @Resource
    private EarthquakeListMapper earthquakesListMapper;
    @Override
    public List<AftershockInformation> importExcel(MultipartFile file, String userName, String eqId) throws IOException {
        InputStream inputStream = file.getInputStream();
        // 读取总行数（略过表头）
        int totalRows = WorkbookFactory.create(inputStream).getSheetAt(0).getPhysicalNumberOfRows() - 4;
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
}
