package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.dto.EqEventDTO;
import com.ruoyi.system.domain.entity.AssessmentBatch;
import com.ruoyi.system.domain.entity.AssessmentOutput;
import com.ruoyi.system.domain.entity.AssessmentResult;
import com.ruoyi.system.mapper.AssessmentBatchMapper;
import com.ruoyi.system.mapper.AssessmentResultMapper;
import com.ruoyi.system.service.IAssessmentBatchService;
import com.ruoyi.system.service.IAssessmentResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: xiaodemos
 * @date: 2024-12-02 19:48
 * @description: 乡镇级评估结果实现类
 */

@Service
@Slf4j
public class AssessmentResultServiceImpl extends ServiceImpl<AssessmentResultMapper, AssessmentResult> implements IAssessmentResultService {

    @Resource
    private AssessmentResultMapper assessmentResultMapper;

    /**
     * @param event 地震事件编码
     * @author: xiaodemos
     * @date: 2024/12/10 10:14
     * @description: 对灾情结果数据进行逻辑删除
     * @return: 返回删除的状态
     */
    public Boolean deletedTownResultData(String event) {

        LambdaQueryWrapper<AssessmentResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssessmentResult::getEqId, event);

        int flag = assessmentResultMapper.update(AssessmentResult
                .builder()
                .isDeleted(1)
                .build(), wrapper);

        return flag > 0 ? true : false;
    }

    /**
     * @param dto
     * @author: xiaodemos
     * @date: 2024/12/12 18:21
     * @description: 批量查询灾情结果数据
     * @return: 返回灾情结果数据列表
     */
    public List<AssessmentResult> eqEventSpecialData(EqEventDTO dto) {

        LambdaQueryWrapper<AssessmentResult> wrapper = Wrappers
                .lambdaQuery(AssessmentResult.class)
                .eq(AssessmentResult::getIsDeleted, 0)
                .like(AssessmentResult::getEqId, dto.getEqId())
                .or().like(AssessmentResult::getEqqueueId, dto.getEqqueueId());

        List<AssessmentResult> assessmentResults = assessmentResultMapper.selectList(wrapper);

        return assessmentResults;
    }

}
