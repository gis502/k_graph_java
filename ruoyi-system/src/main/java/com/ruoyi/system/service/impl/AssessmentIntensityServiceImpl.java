package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.dto.EqEventDTO;
import com.ruoyi.system.domain.entity.AssessmentBatch;
import com.ruoyi.system.domain.entity.AssessmentIntensity;
import com.ruoyi.system.domain.entity.AssessmentResult;
import com.ruoyi.system.mapper.AssessmentIntensityMapper;
import com.ruoyi.system.mapper.AssessmentResultMapper;
import com.ruoyi.system.service.IAssessmentIntensityService;
import com.ruoyi.system.service.IAssessmentResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: xiaodemos
 * @date: 2024-12-02 23:13
 * @description: 地震影响场评估结果实现类
 */
@Service
@Slf4j
public class AssessmentIntensityServiceImpl extends ServiceImpl<AssessmentIntensityMapper, AssessmentIntensity> implements IAssessmentIntensityService {

    @Resource
    private AssessmentIntensityMapper assessmentIntensityMapper;

    /**
     * @param event 地震事件编码
     * @author: xiaodemos
     * @date: 2024/12/10 9:52
     * @description: 对影响场的数据进行逻辑删除
     * @return: 返回删除的状态
     */
    public Boolean deletedIntensityData(String event) {

        LambdaQueryWrapper<AssessmentIntensity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssessmentIntensity::getEqid, event);

        int flag = assessmentIntensityMapper.update(AssessmentIntensity
                .builder()
                .isDeleted(1)
                .build(), wrapper);

        return flag > 0 ? true : false;
    }

    /**
     * @param dto 前端传的参数
     * @author: xiaodemos
     * @date: 2024/12/12 20:45
     * @description: 根据Id查询影响场的一条数据
     * @return: 返回（单体查询）影响场（烈度圈）的数据
     */
    public AssessmentIntensity eqEventIntensityData(EqEventDTO dto) {

        LambdaQueryWrapper<AssessmentIntensity> wrapper = Wrappers
                .lambdaQuery(AssessmentIntensity.class)
                .eq(AssessmentIntensity::getIsDeleted, 0)
                .like(AssessmentIntensity::getEqid, dto.getEqid())
                .or().like(AssessmentIntensity::getEqqueueId, dto.getEqqueueId());

        AssessmentIntensity assessmentIntensity = assessmentIntensityMapper.selectOne(wrapper);

        return assessmentIntensity;
    }

}
