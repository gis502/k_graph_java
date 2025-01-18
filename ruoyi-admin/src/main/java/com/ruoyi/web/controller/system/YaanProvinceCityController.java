package com.ruoyi.web.controller.system;
import com.ruoyi.system.service.impl.YaanProvinceCityServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
* 全省县区经纬度(yaan_province_city)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/yaan_province_city")
public class YaanProvinceCityController {
/**
* 服务对象
*/
@Resource
private YaanProvinceCityServiceImpl yaanProvinceCityServiceImpl;



}
