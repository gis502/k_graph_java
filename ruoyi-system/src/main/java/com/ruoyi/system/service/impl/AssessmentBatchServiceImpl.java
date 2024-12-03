package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
     * @author:  xiaodemos
     * @date:  2024/12/3 0:04
     * @description: 对地震批次进行更新
     * @param state 评估状态
     * @return: 对数据变更是否成功
     */
//    public boolean updateBatchState(String state){
//
//        AssessmentBatch.builder()
//                .state(state)
//                .build();
//
//        assessmentBatchMapper.update();
//
//        return true;
//    }



}
