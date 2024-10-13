package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.mapper.RescueActionCasualtiesMapper;
import com.ruoyi.system.domain.entity.RescueActionCasualties;
import com.ruoyi.system.service.RescueActionCasualtiesService;
@Service
public class RescueActionCasualtiesServiceImpl extends ServiceImpl<RescueActionCasualtiesMapper, RescueActionCasualties> implements RescueActionCasualtiesService{
    @Autowired
    private RescueActionCasualtiesMapper rescueActionCasualtiesMapper;
    public List<RescueActionCasualties> getByEqid(String eqid) {
        // 根据 eqid 查询数据库
        QueryWrapper<RescueActionCasualties> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("eqid", eqid);  // 查询条件为 eqid
        return rescueActionCasualtiesMapper.selectList(queryWrapper);
    }

}
