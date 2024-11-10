package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.AfterSeismicInformation;
import com.ruoyi.system.domain.entity.News;
import com.baomidou.mybatisplus.extension.service.IService;
public interface NewsService extends IService<News>{


    IPage<News> searchData(RequestBTO requestBTO);

}
