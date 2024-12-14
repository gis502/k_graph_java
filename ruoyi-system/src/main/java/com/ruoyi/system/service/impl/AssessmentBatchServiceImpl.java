package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.dto.EqEventReassessmentDTO;
import com.ruoyi.system.domain.entity.AssessmentBatch;
import com.ruoyi.system.mapper.AssessmentBatchMapper;
import com.ruoyi.system.service.IAssessmentBatchService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * @author: xiaodemos
 * @date: 2024-11-26 17:50
 * @description: 地震批次实现类
 */

@Service
public class AssessmentBatchServiceImpl extends ServiceImpl<AssessmentBatchMapper, AssessmentBatch> implements IAssessmentBatchService {

    @Resource
    private AssessmentBatchMapper assessmentBatchMapper;


    /**
     * @param eqId      事件编码
     * @param eqqueueId 批次编码
     * @author: xiaodemos
     * @date: 2024/12/14 16:01
     * @description: 根据Id查询事件编码的评估批次进度
     * @return: 返回评估进度
     */
    public AssessmentBatch selectBatchProgressByEqId(String eqId, String eqqueueId) {

        LambdaQueryWrapper<AssessmentBatch> wrapper = Wrappers
                .lambdaQuery(AssessmentBatch.class)
                .eq(AssessmentBatch::getEqId, eqId)
                .eq(AssessmentBatch::getEqqueueId, eqqueueId)
                .eq(AssessmentBatch::getIsDeleted, 0);

        return assessmentBatchMapper.selectOne(wrapper);
    }

    /**
     * @param eqId      事件编码
     * @param eqqueueId 批次编码
     * @param progress  评估进度
     * @author: xiaodemos
     * @date: 2024/12/14 15:54
     * @description: 更新批次表中的进度
     */
    public void updateBatchProgress(String eqId, String eqqueueId, Double progress) {

        AssessmentBatch batch = AssessmentBatch.builder()
                .eqId(eqId)
                .eqqueueId(eqqueueId)
                .progress(progress)
                .build();

        assessmentBatchMapper.update(batch,
                new LambdaQueryWrapper<>(batch)
                        .eq(AssessmentBatch::getEqId, eqId)
        );
    }

    /**
     * @param state 评估状态
     * @author: xiaodemos
     * @date: 2024/12/3 0:04
     * @description: 对地震批次状态进行更新
     * @return: 对数据变更是否成功
     */
    public boolean updateBatchState(String eqId, String eqqueueId, int state) {

        AssessmentBatch batch = AssessmentBatch.builder()
                .eqId(eqId)
                .eqqueueId(eqqueueId)
                .state(state)
                .build();

        int updated = assessmentBatchMapper.update(batch,
                new LambdaQueryWrapper<>(batch)
                        .eq(AssessmentBatch::getEqId, eqId)
        );

        return updated > 0 ? true : false;
    }

    /**
     * @param params 更新的地震数据
     * @author: xiaodemos
     * @date: 2024/12/7 19:14
     * @description: 返回一个批次号用于版本的更新
     * @return: 返回当前未更新地数据的批次号
     */
    public Integer gainBatchVersion(EqEventReassessmentDTO params) {

        LambdaQueryWrapper<AssessmentBatch> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssessmentBatch::getEqId, params.getEvent());
        AssessmentBatch assessmentBatch = assessmentBatchMapper.selectOne(wrapper);

        return assessmentBatch.getBatch();
    }

    /**
     * @param event 地震事件编码
     * @author: xiaodemos
     * @date: 2024/12/10 9:47
     * @description: 对批次表的数据进行逻辑删除
     * @return: 返回删除的状态
     */
    public Boolean deletedBatchData(String event) {

        LambdaQueryWrapper<AssessmentBatch> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssessmentBatch::getEqId, event);

        int flag = assessmentBatchMapper.update(AssessmentBatch
                .builder()
                .isDeleted(1)
                .build(), wrapper);

        return flag > 0 ? true : false;
    }

}
