package com.ruoyi.system.service;

import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.system.domain.entity.Tiltphotographymodel;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface TiltphotographymodelService extends IService<Tiltphotographymodel> {


    List<Tiltphotographymodel> selectAllModel();

    int addModel(Tiltphotographymodel model);

    int deleteModel(String uuid);

    int updataModel(Tiltphotographymodel model);

    //    更新没有高程下的tz和rz
    int updataModelNoElevation(Tiltphotographymodel model);

    //    更新有高程下的tze和rze
    int updataModelElevation(Tiltphotographymodel model);

    int getModelTotalCount();

    int updataModels(Tiltphotographymodel model);


}
