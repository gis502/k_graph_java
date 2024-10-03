package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.Tiltphotographymodel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TiltphotographymodelService extends IService<Tiltphotographymodel>{


    List<Tiltphotographymodel> selectAllModel();

}
