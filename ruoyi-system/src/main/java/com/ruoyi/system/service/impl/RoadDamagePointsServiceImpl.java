package com.ruoyi.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.domain.entity.PowerSupplyInformation;
import com.ruoyi.system.listener.PowerSupplyInformationListener;
import com.ruoyi.system.mapper.EarthquakeListMapper;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.mapper.RoadDamagePointsMapper;
import com.ruoyi.system.domain.entity.RoadDamagePoints;
import com.ruoyi.system.service.RoadDamagePointsService;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RoadDamagePointsServiceImpl extends ServiceImpl<RoadDamagePointsMapper, RoadDamagePoints> implements RoadDamagePointsService{

}
