package com.ruoyi.web.controller.system;


import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.system.domain.bto.QueryBTO;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.dto.NewsDTO;
import com.ruoyi.system.domain.entity.News;
import com.ruoyi.system.service.NewsService;
import com.ruoyi.system.service.impl.NewsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsServiceImpl newsService;

    @PostMapping("/getNewsList")
    public AjaxResult getNewsList() {
        return AjaxResult.success(newsService.getAllNews());
    }

    @PostMapping("/save")
    public AjaxResult save(@RequestBody NewsDTO newsDto) {

        if (newsDto == null) {
            return AjaxResult.error(MessageConstants.PARAMS_ISNULL);
        }
        log.info("传入的参数为：{}", newsDto);

        News news = News.builder()
                .earthquakeId("test-id-" + UUID.randomUUID().toString())
                .build();

        log.info("待上传的参数：{}", news.getSourceLogo(), news.getImage());

        BeanUtils.copyProperties(newsDto, news);

        return AjaxResult.success(newsService.save(news));
    }

    @DeleteMapping("/removeById")
    public AjaxResult removeById(@RequestParam("id") String id) {

        if (id.equals("")) {
            return AjaxResult.error(MessageConstants.PARAMS_ISNULL);
        }
        return AjaxResult.success(newsService.removeById(id));
    }


    @PutMapping("/update")
    public AjaxResult update(@RequestBody NewsDTO newsDto) {

        if (newsDto == null) {
            return AjaxResult.error(MessageConstants.PARAMS_ISNULL);
        }

        log.info("传入的参数为：{}", newsDto);

        News news = News.builder()
                .earthquakeId("test-update-id-" + UUID.randomUUID().toString())
                .build();

        BeanUtils.copyProperties(newsDto, news);

        return AjaxResult.success(newsService.updateById(news));
    }


    @PostMapping("/searchData")
    public  AjaxResult searchData(@RequestBody RequestBTO requestBTO) {

        if (requestBTO == null){
            return AjaxResult.error(MessageConstants.PARAMS_ISNULL);
        }

        log.info("传入的参数为：{}", requestBTO);

        return AjaxResult.success(newsService.searchData(requestBTO));

    }
}



