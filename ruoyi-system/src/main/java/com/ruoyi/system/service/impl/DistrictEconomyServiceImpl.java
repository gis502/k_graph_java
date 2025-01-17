package com.ruoyi.system.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.DistrictEconomy;
import com.ruoyi.system.mapper.DistrictEconomyMapper;
import com.ruoyi.system.service.DistrictEconomyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DistrictEconomyServiceImpl extends ServiceImpl<DistrictEconomyMapper, DistrictEconomy> implements DistrictEconomyService {
    @Autowired
    private DistrictEconomyMapper districtEconomyMapper;
    @Override
    public List<DistrictEconomy> getDistrictEconomy() {
        return districtEconomyMapper.getDistrictEconomy();
    }
}
