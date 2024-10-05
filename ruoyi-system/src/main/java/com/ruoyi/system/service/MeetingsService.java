package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.Meetings;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MeetingsService extends IService<Meetings>{

    List<Meetings> importExcelMeetings(MultipartFile file, String userName, String eqId);
}
