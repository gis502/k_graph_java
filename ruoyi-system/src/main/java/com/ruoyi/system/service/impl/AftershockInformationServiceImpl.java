package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.AftershockInformation;
import com.ruoyi.system.mapper.AftershockInformationMapper;
import com.ruoyi.system.service.AftershockInformationService;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.system.service.strategy.DataExportStrategy;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.entity.AftershockInformation;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class AftershockInformationServiceImpl extends
        ServiceImpl<AftershockInformationMapper, AftershockInformation>
        implements AftershockInformationService, DataExportStrategy {
    @Override
    public IPage<AftershockInformation> getPage(RequestBTO requestBTO) {
        Page<AftershockInformation>
                aftershockInformation =
                new Page<>(requestBTO.getCurrentPage(), requestBTO.getPageSize());
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


        return null;
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
