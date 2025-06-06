package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.dto.EqEventTriggerDTO;
import com.ruoyi.system.domain.dto.TriggerDTO;
import com.ruoyi.system.domain.entity.EqList;

import java.time.LocalDateTime;
import java.util.List;

public interface IEqListService extends IService<EqList> {
    List<String> getExcelUploadEqList();

    String findLastNomalEventTime();

    void addNewEq(EqEventTriggerDTO eqEventTriggerDTO);

    void trigger(TriggerDTO triggerDTO) throws InterruptedException;
    void autoTrigger() throws InterruptedException;
}
