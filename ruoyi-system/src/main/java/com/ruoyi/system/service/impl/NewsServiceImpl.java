package com.ruoyi.system.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.News;
import com.ruoyi.system.mapper.NewsMapper;
import com.ruoyi.system.service.NewsService;
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService{

}
