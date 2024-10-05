package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.ruoyi.system.domain.entity.ListTable;
import com.ruoyi.system.service.ListTableService;

import com.ruoyi.web.controller.common.RequestParams;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * (list_table)表控制层
 *
 * @author xxxxx
 */
@RestController
@RequestMapping("/list_table")
public class ListTableController {
    /**
     * 服务对象
     */
    @Resource
    private ListTableService listTableService;

    @RequestMapping(value = "getListTable", method = {RequestMethod.POST, RequestMethod.GET})
    public Page<ListTable> getListTable(@RequestBody RequestParams requestParams) {
        Page<ListTable> page = new Page<>(requestParams.getPageNum(), requestParams.getPageSize());
        LambdaQueryWrapper<ListTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(ListTable::getTableName, requestParams.getQueryValue())
                .or().like(ListTable::getTableComment, requestParams.getQueryValue());
        return listTableService.page(page, queryWrapper);
    }

}
