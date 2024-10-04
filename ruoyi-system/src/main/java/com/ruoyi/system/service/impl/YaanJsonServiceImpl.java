package com.ruoyi.system.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.YaanJson;
import com.ruoyi.system.mapper.YaanJsonMapper;
import com.ruoyi.system.service.YaanJsonService;
@Service
public class YaanJsonServiceImpl extends ServiceImpl<YaanJsonMapper, YaanJson> implements YaanJsonService{
    @Autowired
    private YaanJsonMapper yaanJsonMapper;

    @Override
    public String getAreaStr(String name){
        return yaanJsonMapper.getAreaStr(name);
    }
    @Override
    public String getintersectionArea(String Area,String Outcir){
        return yaanJsonMapper.getintersectionArea(Area,Outcir);
    }

    @Override
    public Double computeIntersectionRatio(String intersectionArea,String Outcir){
        return yaanJsonMapper.computeIntersectionRatio(intersectionArea,Outcir);
    }
}
