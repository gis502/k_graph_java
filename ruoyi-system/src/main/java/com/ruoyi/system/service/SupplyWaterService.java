package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.SupplySituation;
import com.ruoyi.system.domain.entity.SupplyWater;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SupplyWaterService extends IService<SupplyWater> {

    List<SupplyWater> importExcelSupplyWater(MultipartFile file, String userName, String eqId) throws IOException;

    List<SupplyWater> getSupplyWaterById(String eqid);

    IPage<SupplyWater> searchData(RequestBTO requestBTO);
}
