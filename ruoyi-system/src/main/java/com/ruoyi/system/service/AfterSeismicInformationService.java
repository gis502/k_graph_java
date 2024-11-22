package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.AfterSeismicInformation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AfterSeismicInformationService extends IService<AfterSeismicInformation> {

    List<AfterSeismicInformation> importExcelAfterSeismicInformation(MultipartFile file, String userName, String eqId) throws IOException;

    IPage<AfterSeismicInformation> searchData(RequestBTO requestBTO);

}
