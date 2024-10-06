package com.ruoyi.system.service.impl;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.mapper.TiltphotographymodelMapper;
import com.ruoyi.system.domain.entity.Tiltphotographymodel;
import com.ruoyi.system.service.TiltphotographymodelService;

@Service
public class TiltphotographymodelServiceImpl extends ServiceImpl<TiltphotographymodelMapper, Tiltphotographymodel> implements TiltphotographymodelService {

    @Resource
    private TiltphotographymodelMapper tiltphotographymodelMapper;

    @Override
    public List<Tiltphotographymodel> selectAllModel() {
        return tiltphotographymodelMapper.selectAllModel();
    }

    @Override
    public int addModel(Tiltphotographymodel model) {
        return tiltphotographymodelMapper.addModel(model);
    }

    @Override
    public int deleteModel(String modelid) {
        return tiltphotographymodelMapper.deleteModel(modelid);
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

}
