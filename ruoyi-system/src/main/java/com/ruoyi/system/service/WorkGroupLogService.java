package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.AfterSeismicInformation;
import com.ruoyi.system.domain.entity.WorkGroupLog;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface WorkGroupLogService extends IService<WorkGroupLog> {

    List<WorkGroupLog> importExcelWorkGroupLog(MultipartFile file, String userName, String eqId) throws IOException;

    IPage<WorkGroupLog> searchData(RequestBTO requestBTO);


    List<WorkGroupLog> getWordCloudLib(String eqId);


}
