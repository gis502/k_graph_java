package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.LargeSpecialRescueEquipment;
import com.ruoyi.system.domain.entity.PowerSupplyInformation;
import com.ruoyi.system.domain.entity.PublicOpinion;
import com.ruoyi.system.domain.entity.RedCrossDonations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface PublicOpinionService extends IService<PublicOpinion> {

    List<PublicOpinion> importExcelPublicOpinion(MultipartFile file, String userName, String eqId) throws IOException;

    List<PublicOpinion> getpublicopinion(String eqid);

    IPage<PublicOpinion> searchData(RequestBTO requestBTO);

    List<PublicOpinion> fromPublicOpinion(String eqid, LocalDateTime time);
}
