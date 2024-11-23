package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.Meetings;
import com.ruoyi.system.domain.entity.RoadDamage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface RoadDamageService extends IService<RoadDamage>{

    List<RoadDamage> getRoadRepairsByEqid(String eqid);

    List<RoadDamage> importExcelRoadDamage(MultipartFile file, String userName, String eqId) throws IOException;

    IPage<RoadDamage> searchData(RequestBTO requestBTO);

    List<RoadDamage> fromrepair(String eqid, LocalDateTime time);
}