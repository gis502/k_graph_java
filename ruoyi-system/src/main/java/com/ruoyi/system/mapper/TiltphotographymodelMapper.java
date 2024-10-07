package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.Tiltphotographymodel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TiltphotographymodelMapper extends BaseMapper<Tiltphotographymodel> {
    List<Tiltphotographymodel> selectAllModel();

    int addModel(Tiltphotographymodel model);

    int deleteModel(String modelid);

    int updataModel(Tiltphotographymodel model);

    //    更新没有高程下的tz和rz
    int updataModelNoElevation(Tiltphotographymodel model);

    //    更新有高程下的tze和rze
    int updataModelElevation(Tiltphotographymodel model);
}
