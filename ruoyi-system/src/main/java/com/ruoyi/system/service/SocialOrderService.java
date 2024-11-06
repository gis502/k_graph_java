package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.CharityOrganizationDonations;
import com.ruoyi.system.domain.entity.SocialOrder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SocialOrderService extends IService<SocialOrder> {
    List<SocialOrder> importExcelSocialOrder(MultipartFile file, String userName, String eqId) throws IOException;

    List<SocialOrder> getsocialoption(String eqid);
}
