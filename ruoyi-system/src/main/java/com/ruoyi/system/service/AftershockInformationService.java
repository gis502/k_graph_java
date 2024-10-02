package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.AftershockInformation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


import java.util.Map;

public interface AftershockInformationService extends IService<AftershockInformation>{

    /**
     * 最新余震情况
     * @param eqid
     * @return
     */

    List<AftershockInformation> importExcelAftershockInformation(MultipartFile file, String userName, String eqName) throws IOException;

    Map<String, Object> getLatestAftershockMagnitude(String eqid);
}
