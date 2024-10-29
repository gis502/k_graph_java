package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.uuid.UUID;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.mapper.TiltphotographymodelMapper;
import com.ruoyi.system.domain.entity.Tiltphotographymodel;
import com.ruoyi.system.service.TiltphotographymodelService;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class TiltphotographymodelServiceImpl extends ServiceImpl<TiltphotographymodelMapper, Tiltphotographymodel> implements TiltphotographymodelService {

    @Resource
    private TiltphotographymodelMapper tiltphotographymodelMapper;

    @Override
    public List<Tiltphotographymodel> selectAllModel() {
        List<Tiltphotographymodel> models = tiltphotographymodelMapper.selectAllModel();
        // 打印每个模型的大小
//        System.out.println("获取的数据: " + models);

        return models;
    }


    @Override
    public int addModel(Tiltphotographymodel model) {
        model.generateUuidIfNotPresent();
        return tiltphotographymodelMapper.addModel(model);

    }

    @Override
    public int deleteModel(String uuid) {
        return tiltphotographymodelMapper.deleteModel(uuid);
    }


    @Override
    public int updataModel(Tiltphotographymodel model) {
        return tiltphotographymodelMapper.updataModel(model);
    }

    @Override
    // 更新没有高程下的tz和rz
    public int updataModelNoElevation(Tiltphotographymodel model) {
        return tiltphotographymodelMapper.updataModelNoElevation(model);
    }

    @Override
    // 更新有高程下的tze和rze
    public int updataModelElevation(Tiltphotographymodel model) {
        return tiltphotographymodelMapper.updataModelElevation(model);
    }

    @Override
    public int getModelTotalCount() {
        Long count = tiltphotographymodelMapper.getTotalCount(); // 获取总数，类型为 Long
        // 示例：打印从数据库获取的数据
        return count != null ? count.intValue() : 0; // 转换为 int，处理 null 的情况

    }

    @Override
    public int updataModels(Tiltphotographymodel model) {
        return tiltphotographymodelMapper.updataModels(model);
    }


}
