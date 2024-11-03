package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.CharityOrganizationDonations;
import com.ruoyi.system.domain.entity.GovernmentDepartmentDonations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface GovernmentDepartmentDonationsService extends IService<GovernmentDepartmentDonations> {


    List<GovernmentDepartmentDonations> importExcelGovernmentDepartmentDonations(MultipartFile file, String userName, String eqId) throws IOException;


    List<GovernmentDepartmentDonations> GovernmentDepartmentDonationsByEqId(String eqid);
}
