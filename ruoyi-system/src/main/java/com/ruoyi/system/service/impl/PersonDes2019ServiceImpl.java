package com.ruoyi.system.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.mapper.PersonDes2019Mapper;
import com.ruoyi.system.domain.entity.PersonDes2019;
import com.ruoyi.system.service.PersonDes2019Service;
@Service
public class PersonDes2019ServiceImpl extends ServiceImpl<PersonDes2019Mapper, PersonDes2019> implements PersonDes2019Service{
    @Autowired
    private PersonDes2019Mapper personDes2019Mapper;
    @Override
    public int getCountinCirle(String circlestr) {
        return personDes2019Mapper.getCountinCirle(circlestr);
    }
    @Override
    public double getavgdesinCirle(String circlestr){
        return personDes2019Mapper.getavgdesinCirle(circlestr);
    }

}
