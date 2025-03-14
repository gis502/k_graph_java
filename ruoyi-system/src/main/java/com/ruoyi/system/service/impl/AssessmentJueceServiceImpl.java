package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.system.domain.dto.EqEventDTO;
import com.ruoyi.system.domain.dto.EqEventTriggerDTO;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.AssessmentJuece;
import com.ruoyi.system.mapper.AssessmentJueceMapper;
import com.ruoyi.system.service.AssessmentJueceService;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AssessmentJueceServiceImpl extends ServiceImpl<AssessmentJueceMapper, AssessmentJuece> implements AssessmentJueceService{


    @Resource
    private AssessmentJueceMapper assessmentJueceMapper;
    @Override
    public void saveAssessmentJuece(EqEventTriggerDTO params, String sourceFile, String fileName) {
        AssessmentJuece assessment = new AssessmentJuece();
        assessment.setId(UUID.randomUUID().toString()); // 生成 UUID
        assessment.setEqid(params.getEvent()); // eqid 对应 event
        assessment.setEqqueueId(params.getEvent() + "01"); // eqqueue_id 对应 event+01
        assessment.setProTime(Timestamp.valueOf(LocalDateTime.parse(params.getEqTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).toLocalDateTime());
        assessment.setSourceFile(sourceFile); // source_file 对应 sourceFile
        assessment.setFileName(fileName);

        this.save(assessment); // MyBatis-Plus 提供的保存方法
    }

    @Override
    public List<AssessmentJuece> eqEventOutputJueCeData(EqEventDTO dto) {
        LambdaQueryWrapper<AssessmentJuece> wrapper = Wrappers
                .lambdaQuery(AssessmentJuece.class)
                .eq(AssessmentJuece::getEqid, dto.getEqid())
                .eq(AssessmentJuece::getEqqueueId, dto.getEqqueueId()); ;

        List<AssessmentJuece> outputList =assessmentJueceMapper.selectList(wrapper);

        return outputList;
    }
}
