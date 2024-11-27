package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.dto.EqEventTriggerDTO;
import com.ruoyi.web.api.service.SeismicTriggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author: xiaodemos
 * @date: 2024-11-24 14:15
 * @description: 第三方接口API控制层
 */


@Slf4j
@RestController
@RequestMapping("/tp/api/open")
public class ThirdPartyApiController {

    //TODO 调用业务层来走接口
    @Resource
    private SeismicTriggerService seismicTriggerService;

    @PostMapping("/trigger")
    public AjaxResult eqEventTrigger(@RequestBody EqEventTriggerDTO params ) {

        seismicTriggerService.seismicEventTrigger(params);

        return AjaxResult.success();
    }


}
