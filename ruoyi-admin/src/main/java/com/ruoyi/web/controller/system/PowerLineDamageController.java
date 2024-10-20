package com.ruoyi.web.controller.system;
import com.ruoyi.system.service.PowerLineDamageService;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
* 震后生成-灾害现场动态信息-生命线修复信息-生命线工程破坏类-输、配电线路破坏点(power_line_damage)表控制层

*/
@RestController
@RequestMapping("/power_line_damage")
public class PowerLineDamageController {

    @Resource
    private PowerLineDamageService powerLineDamageService;


    }

