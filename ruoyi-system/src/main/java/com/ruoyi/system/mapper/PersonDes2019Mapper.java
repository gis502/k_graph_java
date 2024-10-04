package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.PersonDes2019;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PersonDes2019Mapper extends BaseMapper<PersonDes2019> {
    Integer getCountinCirle (String circlestr);

    Double getavgdesinCirle(String circlestr);
//    Double getSumDensityinCirle(String circlestr);

}