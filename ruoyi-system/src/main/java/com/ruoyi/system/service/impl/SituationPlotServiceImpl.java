package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.mapper.SituationPlotMapper;
import com.ruoyi.system.domain.entity.SituationPlot;
import com.ruoyi.system.service.SituationPlotService;

@Service
public class SituationPlotServiceImpl extends ServiceImpl<SituationPlotMapper, SituationPlot> implements SituationPlotService {
    @Autowired
    private SituationPlotMapper situationPlotMapper;
    @Autowired
    private Map<String, BaseMapper<?>> mapperRegistry;

    @Autowired
    private Map<String, String> plotTypeToMapperType;

    @Autowired
    public SituationPlotServiceImpl(SituationPlotMapper situationPlotMapper,
                                    Map<String, BaseMapper<?>> mapperRegistry,
                                    Map<String, String> plotTypeToMapperType) {
        this.situationPlotMapper = situationPlotMapper;
        this.mapperRegistry = mapperRegistry;
        this.plotTypeToMapperType = plotTypeToMapperType;
        System.out.println("In Service Constructor - mapperRegistry contents:");
        for (Map.Entry<String, BaseMapper<?>> entry : mapperRegistry.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue().getClass().getName());
        }
    }

    @Override
    public void addPlot(String plotType, Object details) {
        // 打印插入前的 plot 对象，检查是否包含所有字段
        System.out.println("Inserting details: " + details);
        // 根据plotType获取对应的Mapper类型
        String mapperType = plotTypeToMapperType.get(plotType.toLowerCase());
        System.out.println("mapperType！！: " + mapperType);
        if (mapperType != null) {
            System.out.println("mapperRegistry: " + mapperRegistry);
            try {
                BaseMapper<?> mapper = mapperRegistry.get(mapperType + "Mapper");
                System.out.println("mapper: " + mapper);

                if (mapper != null) {
                    // 通过反射获取 Mapper 的泛型实体类型
                    Class<?> entityType = getEntityClass(mapper);
                    System.out.println("entityType: " + entityType);

                    // 使用 ObjectMapper 将 details 转换为对应的实体类型
                    ObjectMapper objectMapper = new ObjectMapper();
                    Object entity = objectMapper.convertValue(details, entityType);
                    System.out.println("entity: " + entity);

                    ((BaseMapper<Object>) mapper).insert(entity);
                } else {
                    throw new IllegalArgumentException("Unknown mapper type: " + mapperType);
                }
            } catch (Exception e) {
                System.err.println("Error processing details: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Unknown plot type: " + plotType);
        }
    }

    @Override
    public void deletePlot(String plotType, String plotId) {
        // 打印删除前的 plotId 和 detailsId
        System.out.println("Deleting plotId: " + plotId);

        // 1. 删除 plot 信息
        situationPlotMapper.delete(new LambdaQueryWrapper<SituationPlot>()
                .eq(SituationPlot::getPlotId, plotId));
        System.out.println("Plot deleted with id: " + plotId);

        // 2. 根据 plotType 获取对应的 Mapper 类型并删除 details
        String mapperType = plotTypeToMapperType.get(plotType.toLowerCase());
        if (mapperType != null) {
            try {
                BaseMapper<?> mapper = mapperRegistry.get(mapperType + "Mapper");
                System.out.println("mapper: " + mapper);

                if (mapper != null) {
                    // 通过反射获取 Mapper 的泛型实体类型
                    Class<?> entityType = getEntityClass(mapper);
                    System.out.println("entityType: " + entityType);

                    // 使用 QueryWrapper 代替 LambdaQueryWrapper 构建删除条件
                    QueryWrapper<Object> wrapper = new QueryWrapper<>();
                    wrapper.eq("plot_id", plotId); // 假设表中有 plot_id 字段

                    // 调用 MyBatis Plus 的 delete 方法进行删除
                    ((BaseMapper<Object>) mapper).delete(wrapper);
                    System.out.println("Details deleted with plotId: " + plotId);
                } else {
                    throw new IllegalArgumentException("Unknown mapper type: " + mapperType);
                }
            } catch (Exception e) {
                System.err.println("Error processing deletion: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Unknown plot type: " + plotType);
        }
    }

    @Override
    public void updatePlotDetails(String plotType, String plotId, Object details) {
        // 打印更新前的 plotId 和 details 信息
        System.out.println("Updating plotId: " + plotId);
        System.out.println("Updating details: " + details);

        // 更新 plot 信息（如果有相关字段需要更新的话）
        // 这里假设 plot 信息的更新是可选的，不需要实际的 plot 对象
        // 如果需要，可以根据实际情况调整

        // 根据 plotType 获取对应的 Mapper 类型并更新 details
        String mapperType = plotTypeToMapperType.get(plotType.toLowerCase());
        if (mapperType != null) {
            try {
                BaseMapper<?> mapper = mapperRegistry.get(mapperType + "Mapper");
                System.out.println("mapper: " + mapper);

                if (mapper != null) {
                    // 通过反射获取 Mapper 的泛型实体类型
                    Class<?> entityType = getEntityClass(mapper);
                    System.out.println("entityType: " + entityType);

                    // 使用 ObjectMapper 将 details 转换为对应的实体类型
                    ObjectMapper objectMapper = new ObjectMapper();
                    Object entity = objectMapper.convertValue(details, entityType);
                    System.out.println("entity: " + entity);

                    // 使用 UpdateWrapper 进行更新
                    UpdateWrapper<Object> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq("plot_id", plotId);

                    ((BaseMapper<Object>) mapper).update(entity, updateWrapper);
                    System.out.println("Details updated with plotId: " + plotId);
                } else {
                    throw new IllegalArgumentException("Unknown mapper type: " + mapperType);
                }
            } catch (Exception e) {
                System.err.println("Error processing update: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Unknown plot type: " + plotType);
        }
    }

    @Override
    public Object getPlotInfos(String plotType, String plotId) {
        // 1. 根据 plotType 获取对应的 Mapper 类型
        String mapperType = plotTypeToMapperType.get(plotType.toLowerCase());
        if (mapperType != null) {
            try {
                // 1. 查询 situation_plot 表中的信息
                QueryWrapper<SituationPlot> plotWrapper = new QueryWrapper<>();
                plotWrapper.eq("plot_id", plotId);  // 使用 plotId 作为查询条件

                // 获取 situation_plot 表中的记录
                SituationPlot plotInfo = situationPlotMapper.selectOne(plotWrapper);
                System.out.println("Plot Query result: " + plotInfo);

                BaseMapper<?> mapper = mapperRegistry.get(mapperType + "Mapper");
                System.out.println("mapper: " + mapper);

                if (mapper != null) {
                    // 通过反射获取 Mapper 的泛型实体类型
                    Class<?> entityType = getEntityClass(mapper);
                    System.out.println("entityType: " + entityType);

                    // 使用 QueryWrapper 构建查询条件
                    QueryWrapper<Object> wrapper = new QueryWrapper<>();
                    wrapper.eq("plot_id", plotId);  // 使用 plotId 作为查询条件

                    // 执行查询
                    Object result = ((BaseMapper<Object>) mapper).selectOne(wrapper);
                    System.out.println("Query result: " + result);


                    // 将 plot 表的信息和 plotType 对应表的信息整合在一起
                    Map<String, Object> combinedResult = new HashMap<>();
                    combinedResult.put("plotInfo", plotInfo);
                    combinedResult.put("plotTypeInfo", result);
                    // 返回查询结果
                    return combinedResult;
                } else {
                    throw new IllegalArgumentException("Unknown mapper type: " + mapperType);
                }
            } catch (Exception e) {
                System.err.println("Error processing query: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        } else {
            throw new IllegalArgumentException("Unknown plot type: " + plotType);
        }
    }

    public static Class<?> getEntityClass(BaseMapper<?> mapper) throws Exception {
        // 获取 mapper 的接口类型
        Class<?>[] interfaces = mapper.getClass().getInterfaces();

        for (Class<?> mapperInterface : interfaces) {
            // 判断是否是 BaseMapper 的实现类
            if (BaseMapper.class.isAssignableFrom(mapperInterface)) {
                // 获取接口上的泛型参数
                Type[] genericInterfaces = mapperInterface.getGenericInterfaces();
                for (Type genericInterface : genericInterfaces) {
                    if (genericInterface instanceof ParameterizedType parameterizedType) {
                        // 返回泛型中的第一个参数，也就是实体类
                        return (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    }
                }
            }
        }
        throw new IllegalArgumentException("Unable to determine entity class for mapper: " + mapper.getClass().getName());
    }

    @Override
    public List<SituationPlot> getPlot(String eqid) {
        return situationPlotMapper.getPlot(eqid);
    }

    @Override
    public List<SituationPlot> getSituationPlotsByEqId(String eqid) {

        QueryWrapper<SituationPlot> queryWrapper = new QueryWrapper<>();

        // 构建查询条件 earthquake_id = {eqid}
        queryWrapper.eq("earthquake_id", eqid);

        // 关联 SituationPlotInfo 表并根据 startTime 升序排序
        queryWrapper.orderByAsc("start_time");

        // 执行查询
        return situationPlotMapper.selectList(queryWrapper);
    }
}
