package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.YaanJson;
import com.baomidou.mybatisplus.extension.service.IService;
public interface YaanJsonService extends IService<YaanJson>{
    public String getAreaStr(String name);
    public String getintersectionArea(String Area,String Outcir);
    public Double computeIntersectionRatio(String intersectionArea,String Outcir);


}
