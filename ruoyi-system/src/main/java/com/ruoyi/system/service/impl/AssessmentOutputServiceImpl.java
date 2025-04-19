package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.dto.AssessmentOutputDTO;
import com.ruoyi.system.domain.dto.EqEventDTO;
import com.ruoyi.system.domain.entity.AssessmentIntensity;
import com.ruoyi.system.domain.entity.AssessmentOutput;
import com.ruoyi.system.domain.entity.EqList;
import com.ruoyi.system.mapper.AssessmentOutputMapper;
import com.ruoyi.system.service.IAssessmentBatchService;
import com.ruoyi.system.service.IAssessmentOutputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author: xiaodemos
 * @date: 2024-12-03 17:37
 * @description: 获取专题图、报告结果实现类
 */

@Slf4j
@Service
public class AssessmentOutputServiceImpl extends ServiceImpl<AssessmentOutputMapper, AssessmentOutput> implements IAssessmentOutputService {

    @Resource
    private AssessmentOutputMapper assessmentOutputMapper;


    /**
     * @param event 事件编码
     * @author: xiaodemos
     * @date: 2025/1/21 16:37
     * @description: 查询数据库中是否存在数据
     * @return: 如果存在返回true，不存在返回false
     */
    public boolean isEventSaved(String event) {

        LambdaQueryWrapper<AssessmentOutput> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssessmentOutput::getId, event);
        wrapper.eq(AssessmentOutput::getIsDeleted, 0);

        boolean exists = assessmentOutputMapper.exists(wrapper);

        return exists;
    }

    /**
     * @param event 地震事件编码
     * @author: xiaodemos
     * @date: 2024/12/10 10:10
     * @description: 对产出报告数据进行逻辑删除
     * @return: 返回删除的状态
     */
    public Boolean deletedOutputData(String event) {

        LambdaQueryWrapper<AssessmentOutput> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssessmentOutput::getEqid, event);

        int flag = assessmentOutputMapper.update(AssessmentOutput
                .builder()
                .isDeleted(1)
                .build(), wrapper);

        return flag > 0 ? true : false;
    }

    /**
     * @param dto eqqueueid、eqid 参数
     * @author: xiaodemos
     * @date: 2024/12/12 21:03
     * @description: 批量获取专题图数据
     * @return: 返回专题图数据
     */
    public List<AssessmentOutput> eqEventOutputMapData(EqEventDTO dto) {

        LambdaQueryWrapper<AssessmentOutput> wrapper = Wrappers
                .lambdaQuery(AssessmentOutput.class)
                .eq(AssessmentOutput::getIsDeleted, 0)
                .eq(AssessmentOutput::getType, 1)    //1 表示专题图
                .like(AssessmentOutput::getEqid, dto.getEqid());

        List<AssessmentOutput> outputList = assessmentOutputMapper.selectList(wrapper);

        return outputList;
    }

    /**
     * @param dto eqqueueid、eqid 参数
     * @author: xiaodemos
     * @date: 2024/12/12 21:11
     * @description: 批量查询专题图和灾情报告
     * @return: （批量查询）专题图与灾情报告
     */
    public List<AssessmentOutput> eqEventOutputReportData(EqEventDTO dto) {

        LambdaQueryWrapper<AssessmentOutput> wrapper = Wrappers
                .lambdaQuery(AssessmentOutput.class)
                .eq(AssessmentOutput::getIsDeleted, 0)
                .eq(AssessmentOutput::getType, 2)    //1 表示灾情报告
                .like(AssessmentOutput::getEqid, dto.getEqid());

        List<AssessmentOutput> outputList = assessmentOutputMapper.selectList(wrapper);

        return outputList;
    }

    

    @RabbitListener(queues = "thematic.map")
    public void receive(AssessmentOutputDTO dto) {
        // 打印日志

        log.info("rabbitmq 接收到 {}消息 ...",dto.getFileName());
    }

}
