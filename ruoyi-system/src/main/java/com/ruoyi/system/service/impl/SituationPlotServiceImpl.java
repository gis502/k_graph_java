package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.system.config.MapperConfig;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private MapperConfig mapperConfig;

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
        System.out.println("plotType: " + plotType);
        // 根据plotType获取对应的Mapper类型
        String mapperType = plotTypeToMapperType.get(plotType);
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
    public void updatePlotDetails(String startTime, String endTime,String plotType, String plotId, Object details) {
        // 打印更新前的 plotId 和 details 信息
        //        System.out.println("Updating plotId: " + plotId);
        //        System.out.println("Updating details: " + details);

        // 更新 plot 信息（如果有相关字段需要更新的话）
        SituationPlot situationPlot = new SituationPlot();
        situationPlot.setStartTime(LocalDateTime.parse(startTime));
        situationPlot.setEndTime(LocalDateTime.parse(endTime));

        UpdateWrapper<SituationPlot> plotUpdateWrapper = new UpdateWrapper<>();
        plotUpdateWrapper.eq("plot_id",plotId);
        situationPlotMapper.update(situationPlot,plotUpdateWrapper);
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

    @Override
    public List<Object> getExcelPlotInfo(List<String> plotTypes, List<String> plotIds) {
        List<Object> combinedResults = new ArrayList<>();

        for (int i = 0; i < plotIds.size(); i++) {
            String plotId = plotIds.get(i);
            String plotType = plotTypes.get(i);

            // 获取对应的 Mapper 类型
            String mapperType = plotTypeToMapperType.get(plotType.toLowerCase());

            if (mapperType != null) {
                try {
                    // 查询 situation_plot 表中的信息
                    QueryWrapper<SituationPlot> plotWrapper = new QueryWrapper<>();
                    plotWrapper.eq("plot_id", plotId);

                    SituationPlot plotInfo = situationPlotMapper.selectOne(plotWrapper);
                    BaseMapper<?> mapper = mapperRegistry.get(mapperType + "Mapper");

                    if (mapper != null) {
                        // 获取 Mapper 的泛型实体类型
                        Class<?> entityType = getEntityClass(mapper);

                        QueryWrapper<Object> wrapper = new QueryWrapper<>();
                        wrapper.eq("plot_id", plotId);

                        // 执行查询
                        Object result = ((BaseMapper<Object>) mapper).selectOne(wrapper);

                        // 整合结果
                        Map<String, Object> combinedResult = new HashMap<>();
                        combinedResult.put("plotInfo", plotInfo);
                        combinedResult.put("plotTypeInfo", result);

                        combinedResults.add(combinedResult);
                    } else {
                        throw new IllegalArgumentException("Unknown mapper type: " + mapperType);
                    }
                } catch (Exception e) {
                    System.err.println("Error processing query for plotId " + plotId + ": " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                throw new IllegalArgumentException("Unknown plot type: " + plotType);
            }
        }

        return combinedResults;
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
                    if (genericInterface instanceof ParameterizedType ) {
                        ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
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

    @Override
    public void savePlotDataAndProperties(List<SituationPlot> plotDataList, List<String> plotProperty) {
        GeometryFactory geometryFactory = new GeometryFactory();

        String eqid = plotDataList.get(0).getEarthquakeId();

        // 处理 plotDataList 部分
        for (SituationPlot plotData : plotDataList) {
            plotData.setCreationTime(LocalDateTime.now());

            // 根据 drawtype 的值进行转换
            switch (plotData.getDrawtype()) {
                case "点":
                    plotData.setDrawtype("point");
                    break;
                case "线":
                    plotData.setDrawtype("polyline");
                    break;
                case "面":
                    plotData.setDrawtype("polygon");
                    break;
                default:
                    break;
            }

            // 组合 longitude 和 latitude 成为 geom
            double longitude = plotData.getLongitude();
            double latitude = plotData.getLatitude();
            if (longitude != 0.0 && latitude != 0.0) {
                Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
                plotData.setGeom(point);
            }

            plotData.setLongitude(null);
            plotData.setLatitude(null);
        }

        // 从数据库获取目标 geom、start_time 和 end_time 列表
        List<SituationPlot> targetPlots = situationPlotMapper.getCheckPlot(eqid);

        // 遍历 plotDataList 并筛选出与 targetPlots 中任一条数据匹配的项
        List<SituationPlot> filteredPlotDataList = plotDataList.stream()
                .filter(plotData -> targetPlots.stream().noneMatch(targetPlot ->
                        plotData.getGeom() != null
                                && plotData.getGeom().equalsExact(targetPlot.getGeom())
                                && plotData.getStartTime().equals(targetPlot.getStartTime())
                                && plotData.getEndTime().equals(targetPlot.getEndTime())))
                .collect(Collectors.toList());

        // 根据filteredPlotDataList列表，找出plotProperty中plotId相同的数据
        List<String> filteredPlotProperty = filteredPlotDataList.stream()
                .flatMap(plotData -> plotProperty.stream()
                        .filter(property -> property.contains("plotId=" + plotData.getPlotId())))
                .collect(Collectors.toList());

        // 插入不匹配的标绘数据
        situationPlotMapper.insertSituationPlots(filteredPlotDataList);

        // 处理 plotProperty 部分
        Map<String, BaseMapper<?>> mapperRegistry = mapperConfig.mapperRegistry();

        for (String property : filteredPlotProperty) {
            String entityName = property.substring(0, property.indexOf("("));
            String fieldsString = property.substring(property.indexOf("(") + 1, property.lastIndexOf(")"));
            String[] fields = fieldsString.split(", ");

            try {
                Class<?> clazz = Class.forName("com.ruoyi.system.domain.entity." + capitalize(entityName));
                Object entityInstance = clazz.getDeclaredConstructor().newInstance();

                for (String field : fields) {
                    String[] keyValue = field.split("=");
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();

                    Field fieldToSet = clazz.getDeclaredField(key);
                    fieldToSet.setAccessible(true);

                    if (fieldToSet.getType() == Double.class) {
                        fieldToSet.set(entityInstance, Double.parseDouble(value));
                    } else if (fieldToSet.getType() == Integer.class) {
                        if (value.contains(".")) {
                            fieldToSet.set(entityInstance, (int) Math.round(Double.parseDouble(value)));
                        } else {
                            fieldToSet.set(entityInstance, Integer.parseInt(value));
                        }
                    } else {
                        fieldToSet.set(entityInstance, value);
                    }
                }

                String mapperKey = entityName.toLowerCase();
                BaseMapper<?> mapper = mapperRegistry.get(mapperKey);

                if (mapper != null) {
                    ((BaseMapper<Object>) mapper).insert(entityInstance);
                    System.out.println("Inserted entity: " + entityInstance);
                } else {
                    System.out.println("No mapper found for key: " + mapperKey);
                }

            } catch (Exception e) {
                System.err.println("Error processing property: " + e.getMessage());
                e.printStackTrace();
            }
        }

//        System.out.println("Registered mappers: " + mapperRegistry.keySet());
    }

    // 将字符串首字母大写的方法
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
