package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.CharityOrganizationDonations;
import com.ruoyi.system.domain.entity.GovernmentDepartmentDonations;
import com.ruoyi.system.domain.entity.MaterialDonation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface GovernmentDepartmentDonationsService extends IService<GovernmentDepartmentDonations> {


    List<GovernmentDepartmentDonations> importExcelGovernmentDepartmentDonations(MultipartFile file, String userName, String eqId) throws IOException;


    List<GovernmentDepartmentDonations> GovernmentDepartmentDonationsByEqId(String eqid);

    IPage<GovernmentDepartmentDonations> searchData(RequestBTO requestBTO);

    List<GovernmentDepartmentDonations> fromGovernmentDepartmentDonations(String eqid, LocalDateTime time);
}
