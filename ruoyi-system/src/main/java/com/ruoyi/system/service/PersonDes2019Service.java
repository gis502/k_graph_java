package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.PersonDes2019;
import com.baomidou.mybatisplus.extension.service.IService;
public interface PersonDes2019Service extends IService<PersonDes2019>{
    public int getCountinCirle(String circlestr);
    public double getavgdesinCirle(String circlestr);
}
