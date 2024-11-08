package com.ruoyi.system.service.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.bto.RequestBTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.News;
import com.ruoyi.system.mapper.NewsMapper;
import com.ruoyi.system.service.NewsService;

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

    public IPage<News> searchData(RequestBTO requestBTO) {

        Page<News> newsPage = new Page<>(requestBTO.getCurrentPage(),requestBTO.getPageSize());

        String requestParams = requestBTO.getRequestParams();
        LambdaQueryWrapper<News> queryWrapper = Wrappers.lambdaQuery(News.class)
                .like(News::getTitle, requestParams)
                .or().like(News::getSourceName, requestParams)
                .or().like(News::getUrl, requestParams)
                .or().like(News::getContent, requestParams)
                .or().apply("to_char(publish_time, 'YYYY-MM-DD HH24:MI:SS') LIKE {0}", "%" + requestParams + "%");

        return baseMapper.selectPage(newsPage, queryWrapper);
    }


}
