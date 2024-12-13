package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.bto.QueryParams;
import com.ruoyi.system.domain.dto.EqEventDTO;
import com.ruoyi.system.domain.entity.EqList;
import com.ruoyi.system.mapper.EqListMapper;
import com.ruoyi.system.service.IEqListService;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: xiaodemos
 * @date: 2024-11-26 15:36
 * @description: 实现类
 */

@Service
@Slf4j
public class EqListServiceImpl extends ServiceImpl<EqListMapper, EqList> implements IEqListService {


    @Resource
    private EqListMapper eqListMapper;

    /**
     * @param event 地震事件编码
     * @author: xiaodemos
     * @date: 2024/12/10 9:47
     * @description: 对批次表的数据进行逻辑删除
     * @return: 返回删除的状态
     */
    public Boolean deletedEqListData(String event) {

        LambdaQueryWrapper<EqList> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EqList::getEqId, event);

        int flag = eqListMapper.update(EqList
                .builder()
                .isDeleted(1)
                .build(), wrapper);

        return flag > 0 ? true : false;
    }

    /**
     * @author: xiaodemos
     * @date: 2024/12/10 20:54
     * @description: 返回所有eqlist中的数据
     * @return: 返回所有eqlist中的数据
     */
    public List<EqList> eqEventGetList(QueryParams queryParams) {

        Page<EqList> pages = new Page<>(queryParams.getPageNum(), queryParams.getPageSize());

        LambdaQueryWrapper<EqList> wrapper = Wrappers
                .lambdaQuery(EqList.class)
                .eq(EqList::getIsDeleted, 0);

        Page<EqList> listPage = eqListMapper.selectPage(pages, wrapper);

        log.info("请求参数：{}",listPage);


        List<EqList> records = listPage.getRecords();

        return records;
    }

    /**
     * @param dto 查询参数
     * @author: xiaodemos
     * @date: 2024/12/12 17:28
     * @description: 根据事件编码查询地震的详情信息
     * @return:
     */
    public EqList eqEventGetDetailsInfo(EqEventDTO dto) {

        LambdaQueryWrapper wrapper = Wrappers
                .lambdaQuery(EqList.class)
                .like(EqList::getEqId, dto.getEqId())
                .or().like(EqList::getEqqueueId, dto.getEqqueueId())
                .ge(EqList::getMagnitude, 4) //大于四级的地震
                .eq(EqList::getIsDeleted, 0);

        EqList eq = eqListMapper.selectOne(wrapper);

        return eq;
    }

}
