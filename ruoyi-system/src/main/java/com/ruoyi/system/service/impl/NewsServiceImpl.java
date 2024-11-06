package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.bto.QueryBTO;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.News;
import com.ruoyi.system.mapper.NewsMapper;
import com.ruoyi.system.service.NewsService;

import javax.transaction.Transactional;

@Service
@Slf4j
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService{


    @Autowired
    private NewsMapper newsMapper;
    public List<News> getByEqid(String eqid) {
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("earthquake_id", eqid);  // 查询条件为 eqid
        return newsMapper.selectList(queryWrapper);
    }

    public List<News> getAllNews() {

        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
//                .isNotNull(News::getUrl)
//                .isNotNull(News::getTitle)
//                .isNotNull(News::getPublishTime)
//                .isNotNull(News::getContent)
//                .isNotNull(News::getImage)
//                .isNotNull(News::getSourceName)
                .orderBy(true,false, News::getPublishTime);


        return newsMapper.selectList(queryWrapper);
    }


}
