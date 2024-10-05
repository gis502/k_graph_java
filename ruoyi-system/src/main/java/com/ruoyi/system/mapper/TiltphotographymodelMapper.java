package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.Tiltphotographymodel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TiltphotographymodelMapper extends BaseMapper<Tiltphotographymodel> {
    List<Tiltphotographymodel> selectAllModel();
}
