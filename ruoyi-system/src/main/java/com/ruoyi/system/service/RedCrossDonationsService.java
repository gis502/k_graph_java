package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.CharityOrganizationDonations;
import com.ruoyi.system.domain.entity.RedCrossDonations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface RedCrossDonationsService extends IService<RedCrossDonations> {

    List<RedCrossDonations> importExcelRedCrossDonations(MultipartFile file, String userName, String eqId) throws IOException;


    List<RedCrossDonations> RedCrossDonationsByEqId(String eqid);

    IPage<RedCrossDonations> searchData(RequestBTO requestBTO);

    List<RedCrossDonations> fromRedCrossDonations(String eqid, LocalDateTime time);
}
