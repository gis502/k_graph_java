package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.Meetings;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.TransferSettlementInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MeetingsService extends IService<Meetings>{

    List<Meetings> importExcelMeetings(MultipartFile file, String userName, String eqId) throws IOException;

    IPage<Meetings> searchData(RequestBTO requestBTO);
}
