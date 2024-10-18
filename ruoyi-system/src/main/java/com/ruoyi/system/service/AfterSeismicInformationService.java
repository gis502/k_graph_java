package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.AfterSeismicInformation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AfterSeismicInformationService extends IService<AfterSeismicInformation>{
    List<AfterSeismicInformation> importAfterSeismicInformation(MultipartFile file, String userName, String eqId) throws IOException;

}
