package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.AftershockInformation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


import java.util.Map;

public interface AftershockInformationService extends IService<AftershockInformation>{

    /**
     * 最新余震情况
     *
     * @param eqid
     * @return
     */
    Map<String, Object> getLatestAftershockMagnitude(String eqid);
    List<AftershockInformation> importExcelAftershockInformation(MultipartFile file, String userName, String eqName) throws IOException;
    List<Map<String, Object>> getTotal(String eqid);

    List<Map<String, Object>> getAfterShockInformation(String eqid);

    IPage<AftershockInformation> searchData(RequestBTO requestBTO);

    List<Map<String, Object>> fromAftershock(String eqid, LocalDateTime time);
}
