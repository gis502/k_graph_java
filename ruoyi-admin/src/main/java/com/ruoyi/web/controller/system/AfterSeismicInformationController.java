package com.ruoyi.web.controller.system;
import com.ruoyi.system.service.impl.AfterSeismicInformationServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
* 震后生成-地震灾情信息-震后受灾区域和人数统计（用户上传数据）(afterseismicInformation)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/afterSeismicInformation")
public class AfterSeismicInformationController {
/**
* 服务对象
*/
@Resource
private AfterSeismicInformationServiceImpl afterseismicInformationServiceImpl;



}
