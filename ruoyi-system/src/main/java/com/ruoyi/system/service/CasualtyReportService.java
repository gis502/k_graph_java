package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.AftershockInformation;
import com.ruoyi.system.domain.entity.CasualtyReport;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CasualtyReportService extends IService<CasualtyReport>{

    List<CasualtyReport> importExcelCasualtyReport(MultipartFile file, String userName, String eqId) throws IOException;

    CasualtyReport getCasualtiesStatsById(String eqid);

    List<CasualtyReport> getTotal(String eqid);
//    CasualtyReport getTotal(String eqid);

    List<CasualtyReport> getCasualty(String eqid);
    List<CasualtyReport> getAllRecordInfo(String eqid);

    IPage<CasualtyReport> searchData(RequestBTO requestBTO);

    List<Map<String, Object>> fromCasualty(String eqid, LocalDateTime time);
}


