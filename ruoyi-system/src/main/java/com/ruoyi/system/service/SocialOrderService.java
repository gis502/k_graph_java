package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.CharityOrganizationDonations;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.AfterSeismicInformation;
import com.ruoyi.system.domain.entity.PublicOpinion;
import com.ruoyi.system.domain.entity.SocialOrder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface SocialOrderService extends IService<SocialOrder> {
    List<SocialOrder> importExcelSocialOrder(MultipartFile file, String userName, String eqId) throws IOException;

    List<SocialOrder> getsocialoption(String eqid);

    IPage<SocialOrder> searchData(RequestBTO requestBTO);

    List<SocialOrder> fromSocialOrder(String eqid, LocalDateTime time);
}
