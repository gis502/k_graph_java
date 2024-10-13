package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.CasualtyReport;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CasualtyReportService extends IService<CasualtyReport>{

    List<CasualtyReport> importExcelCasualtyReport(MultipartFile file, String userName, String eqId) throws IOException;

    CasualtyReport getCasualtiesStatsById(String eqid);

    List<CasualtyReport> getTotal(String eqid);
//    CasualtyReport getTotal(String eqid);

    List<CasualtyReport> getCasualty(String eqid);
}
