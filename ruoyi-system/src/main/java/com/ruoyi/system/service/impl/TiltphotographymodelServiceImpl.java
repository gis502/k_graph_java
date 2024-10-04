package com.ruoyi.system.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.mapper.TiltphotographymodelMapper;
import com.ruoyi.system.domain.entity.Tiltphotographymodel;
import com.ruoyi.system.service.TiltphotographymodelService;
@Service
public class TiltphotographymodelServiceImpl extends ServiceImpl<TiltphotographymodelMapper, Tiltphotographymodel> implements TiltphotographymodelService{

    @Resource
    private TiltphotographymodelMapper tiltphotographymodelMapper;

    @Override
    public List<Tiltphotographymodel> selectAllModel() {
        return tiltphotographymodelMapper.selectAllModel();
    }
}
