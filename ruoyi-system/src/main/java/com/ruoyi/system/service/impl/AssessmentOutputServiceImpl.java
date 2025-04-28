package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.exception.base.BaseException;
import com.ruoyi.system.domain.dto.AssessmentOutputDTO;
import com.ruoyi.system.domain.dto.EqEventDTO;
import com.ruoyi.system.domain.entity.AssessmentOutput;
import com.ruoyi.system.mapper.AssessmentOutputMapper;
import com.ruoyi.system.service.IAssessmentOutputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Resource
    private RestTemplate restTemplate;

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

    @Override
    public String gainMap(String eqId, String eqqueueId) {

        // 抛出异常
        if (eqId == null && eqqueueId == null) {
            throw new BaseException("eqId 不能为空");
        }

        // 获取授权
        String authUrl = "http://localhost:8080/api/open/auth";
        // 设置请求头
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Content-Type", "application/json");
        // 设置请求体
        JSONObject authBody = new JSONObject();
        authBody.put("username", "admin");
        authBody.put("password", "admin123");
        HttpEntity<JSONObject> authEntity = new HttpEntity<>(authBody, authHeaders);
        // 发送请求
        ResponseEntity<String> authResponse = restTemplate.exchange(authUrl, HttpMethod.POST, authEntity, String.class);
        // 抛出异常
        if (authResponse.getStatusCode() != HttpStatus.OK) {
            throw new BaseException("获取灾损评估接口授权失败");
        }

        // 获取token
        String authResponseBody = authResponse.getBody();
        JSONObject data = JSONObject.parseObject(authResponseBody);
        String token = data.getString("token");

        // 请求 专题图 接口
        String outputUrl = "http://localhost:8080/api/open/eq/getMap";
        HttpHeaders baseHeaders = new HttpHeaders();
        baseHeaders.set("Content-Type", "application/json");
        baseHeaders.set("Authorization", "Bearer " + token);

        // 设置请求体
        JSONObject outputBody = new JSONObject();
        outputBody.put("eqId", eqId);
        outputBody.put("eqqueueId", eqqueueId);

        HttpEntity<JSONObject> outputEntity = new HttpEntity<>(outputBody, baseHeaders);
        // 发送请求
        ResponseEntity<String> outputResponse = restTemplate.exchange(outputUrl, HttpMethod.POST, outputEntity, String.class);

        return outputResponse.getBody();

    }

    @Override
    public String gainOutMap(String eqId, String eqqueueId) {
        try {
            List<Map<String, Object>> list = new ArrayList<>();

            Map<String, Object> map1 = new HashMap<>();
            map1.put("theme", "震后道路损毁情况");
            map1.put("imgUrl", "C:/Users/Smile/Desktop/profile/震区地震动峰值加速度区划图.jpg");

            Map<String, Object> map2 = new HashMap<>();
            map2.put("theme", "震后建筑物倒塌分析");
            map2.put("imgUrl", "C:/Users/Smile/Desktop/profile/震区交通图.jpg");

            list.add(map1);
            list.add(map2);

            // 用 Jackson 转成 JSON 字符串
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            e.printStackTrace();
            return "[]"; // 出错就返回一个空数组
        }
    }
}
