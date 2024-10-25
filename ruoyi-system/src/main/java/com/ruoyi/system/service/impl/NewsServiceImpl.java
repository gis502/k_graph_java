package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.system.domain.entity.RescueTeam;
import com.ruoyi.system.mapper.RescueTeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.News;
import com.ruoyi.system.mapper.NewsMapper;
import com.ruoyi.system.service.NewsService;
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService{
    @Autowired
    private NewsMapper newsMapper;
    public List<News> getByEqid(String eqid) {
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("earthquake_id", eqid);  // 查询条件为 eqid
        return newsMapper.selectList(queryWrapper);
    }
}
