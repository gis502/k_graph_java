package com.ruoyi.web.controller.system;
import com.ruoyi.system.service.impl.SeismicIntensityCircleServiceImpl;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

/**
* (seismic_intensity_circle)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/seismic_intensity_circle")
public class SeismicIntensityCircleController {
/**
* 服务对象
*/
    @Autowired
    private SeismicIntensityCircleServiceImpl seismicIntensityCircleServiceImpl;

 

}
