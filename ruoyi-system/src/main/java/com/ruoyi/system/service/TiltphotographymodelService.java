package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.Tiltphotographymodel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TiltphotographymodelService extends IService<Tiltphotographymodel> {


    List<Tiltphotographymodel> selectAllModel();

    int addModel(Tiltphotographymodel model);

    int deleteModel(String modelid);

    int updataModel(Tiltphotographymodel model);

    //    更新没有高程下的tz和rz
    int updataModelNoElevation(Tiltphotographymodel model);

    //    更新有高程下的tze和rze
    int updataModelElevation(Tiltphotographymodel model);
}
