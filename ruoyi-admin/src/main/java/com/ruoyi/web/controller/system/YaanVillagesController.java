package com.ruoyi.web.controller.system;
import com.ruoyi.system.service.impl.YaanVillagesServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
* (yaan_villages)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/yaan_villages")
public class YaanVillagesController {
/**
* 服务对象
*/
@Resource
private YaanVillagesServiceImpl yaanVillagesServiceImpl;



}
