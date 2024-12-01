package com.ruoyi.web.controller.system;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.AfterSeismicInformation;
import com.ruoyi.system.domain.entity.AftershockInformation;
import com.ruoyi.system.domain.entity.Tiltphotographymodel;
import com.ruoyi.system.service.AfterSeismicInformationService;
import com.ruoyi.system.service.impl.AfterSeismicInformationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* 震后生成-地震灾情信息-震后受灾区域和人数统计（用户上传数据）(afterseismicInformation)表控制层
*
* @author xxxxx
*/
@Slf4j
@RestController
@RequestMapping("/afterSeismicInformation")
public class AfterSeismicInformationController {
/**
* 服务对象
*/
@Resource
private AfterSeismicInformationService afterseismicInformationService;


    @GetMapping("/getonedata")
    public AjaxResult getOneData(@RequestParam String eqid) {

        // 构建查询条件，假设 eqid 对应的是 EarthquakeIdentifier
        LambdaQueryWrapper<AfterSeismicInformation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AfterSeismicInformation::getEarthquakeIdentifier, eqid);

        // 获取查询结果，假设只有一条记录
        List<AfterSeismicInformation> resultList = afterseismicInformationService.list(wrapper);

        // 判断查询结果是否为空
        if (resultList == null) {
            // 如果没有找到数据，返回失败或空结果
            return AjaxResult.success(null);
        }
        // 返回该字段的值
        return AjaxResult.success(resultList);
    }



}
