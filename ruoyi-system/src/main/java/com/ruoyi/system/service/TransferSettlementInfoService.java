package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.TransferSettlementInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TransferSettlementInfoService extends IService<TransferSettlementInfo>{


    List<TransferSettlementInfo> importExcelTransferSettlementInfo(MultipartFile file, String userName, String eqId);

    List<TransferSettlementInfo> getTotal(String eqid);

    List<TransferSettlementInfo> getTransferInfo(String eqid);
}
