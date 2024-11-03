package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.GovernmentDepartmentDonations;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GovernmentDepartmentDonationsMapper extends BaseMapper<GovernmentDepartmentDonations> {

    List<GovernmentDepartmentDonations> GovernmentDepartmentDonationsEqId(String eqid);
}
