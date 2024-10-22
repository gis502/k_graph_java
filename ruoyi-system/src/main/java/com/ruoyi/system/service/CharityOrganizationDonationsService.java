package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.BarrierLakeSituation;
import com.ruoyi.system.domain.entity.CharityOrganizationDonations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CharityOrganizationDonationsService extends IService<CharityOrganizationDonations> {

    List<CharityOrganizationDonations> importExcelCharityOrganizationDonations(MultipartFile file, String userName, String eqId) throws IOException;


}
