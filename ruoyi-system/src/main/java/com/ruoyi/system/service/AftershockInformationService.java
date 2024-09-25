package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.AftershockInformation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AftershockInformationService extends IService<AftershockInformation>{
    List<AftershockInformation> importExcelAftershockInformation(MultipartFile file, String userName, String eqName) throws IOException;

}
