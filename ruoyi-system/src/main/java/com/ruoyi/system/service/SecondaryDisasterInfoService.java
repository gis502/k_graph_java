package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.BarrierLakeSituation;
import com.ruoyi.system.domain.entity.SecondaryDisasterInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SecondaryDisasterInfoService extends IService<SecondaryDisasterInfo> {

    List<SecondaryDisasterInfo> importExcelSecondaryDisasterInfo(MultipartFile file, String userName, String eqId) throws IOException;

    List<SecondaryDisasterInfo> SecondaryDisasterInfoByEqId(String eqid);

    IPage<SecondaryDisasterInfo> searchData(RequestBTO requestBTO);
}
