package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.EmergencyResponseInfo;
import com.ruoyi.system.mapper.EmergencyResponseInfoMapper;
import com.ruoyi.system.service.EmergencyResponseInfoService;
@Service
public class EmergencyResponseInfoServiceImpl extends ServiceImpl<EmergencyResponseInfoMapper, EmergencyResponseInfo> implements EmergencyResponseInfoService{
    @Autowired
    private EmergencyResponseInfoMapper emergencyResponseInfoMapper;
    public List<EmergencyResponseInfo> getByEqid(String eqid) {
        // 根据 eqid 查询数据库
        QueryWrapper<EmergencyResponseInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("eqid", eqid).orderByDesc("response_time");;  // 查询条件为 eqid
        System.out.println(emergencyResponseInfoMapper.selectList(queryWrapper));
//        queryWrapper.
        return emergencyResponseInfoMapper.selectList(queryWrapper);
    }
}
