package com.ruoyi.web.api.service;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.common.exception.*;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.system.domain.dto.EqEventGetYxcDTO;
import com.ruoyi.system.domain.dto.EqEventTriggerDTO;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.domain.dto.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.apache.xmlbeans.XmlCursor;

import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.impl.*;
import com.ruoyi.web.api.ThirdPartyCommonApi;
import com.ruoyi.web.core.utils.JsonParser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.WKTReader;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;

import java.io.FileOutputStream;
import java.math.BigInteger;

/**
 * @author: xiaodemos
 * @date: 2024-11-26 2:45
 * @description: 地震触发业务逻辑
 */

@Slf4j
@Service
public class SeismicTriggerService {
    @Resource
    private ThirdPartyCommonApi thirdPartyCommonApi;
    @Resource
    private EqListServiceImpl eqListService;
    @Resource
    private AssessmentResultServiceImpl assessmentResultService;
    @Resource
    private AssessmentBatchServiceImpl assessmentBatchService;
    @Resource
    private AssessmentIntensityServiceImpl assessmentIntensityService;
    @Resource
    private AssessmentOutputServiceImpl assessmentOutputService;
    @Resource
    private SeismicAssessmentProcessesService assessmentProcessesService;

    @Resource
    private YaanProvinceTownMapper yaanProvinceTownMapper;

    @Resource
    private YaanAdministrativeBoundaryMapper yaanAdministrativeBoundaryMapper;

    @Resource
    private YaanVillagesMapper yaanVillagesMapper;

    @Resource
    private YaanCountyTownMapper yaanCountyTownMapper;

    @Resource
    private YaanProvinceCityMapper yaanProvinceCityMapper;


    @Resource
    private SismiceMergencyAssistanceService sismiceMergencyAssistanceService;

    private boolean asyncIntensity = false, asyncTown = false, asyncOutputMap = false, asyncOutputReport = false;

    /**
     * @param params 手动触发的地震事件参数
     * @return 返回各数据保存是否成功的状态码，如果返回false，表示数据保存失败，如果返回true，表示数据保存成功
     * @author: xiaodemos
     * @date: 2024/11/26 2:53
     * @description: 地震事件触发时，将进行地震影响场、烈度圈、乡镇级、经济建筑人员伤亡的灾损评估。
     * 异步的将评估结果保存到数据库，并且下载灾情报告和专题图到本地，路径存储到数据库中。
     * 触发的地震数据将同步到双方的数据库中。
     */
    @Async // 参数改为 EqEventReassessmentDTO params
    public CompletableFuture<Void> seismicEventTrigger(EqEventTriggerDTO params) {
        String eqqueueId = null;
        try {
            // 把前端上传的数据保存到第三方数据库中
            // handleThirdPartySeismicReassessment(params);改为这个
            eqqueueId = handleThirdPartySeismicTrigger(params);
            eqqueueId = JsonParser.parseJsonToEqQueueId(eqqueueId);

            // 如果返回的结果是一个空字符串，表示数据已经插入成功，否则抛出异常，事务回滚
            if (StringUtils.isEmpty(eqqueueId)) {
                throw new ParamsIsEmptyException(MessageConstants.SEISMIC_TRIGGER_ERROR);
            }

            // 数据插入到第三方数据库成功后，插入到本地数据库
            getWithSave(params, eqqueueId);

            //异步获取辅助决策报告结果
            handleAssessmentReportAssessment(params, eqqueueId);
            // 调用 file 方法--异步获取辅助决策（二）报告结果
            sismiceMergencyAssistanceService.file(params, eqqueueId);

            // 异步进行地震影响场灾损评估
            handleSeismicYxcEventAssessment(params, eqqueueId);

            // 异步进行乡镇级评估
            handleTownLevelAssessment(params, eqqueueId);


//            // 异步获取专题图评估结果
            handleSpecializedAssessment(params, eqqueueId);
//
//            // 异步获取灾情报告评估结果
            handleDisasterReportAssessment(params, eqqueueId);





            // 检查四个评估结果的数据是否成功
            retrySaving(params, eqqueueId);

            // 返回每个阶段的保存数据状态
            return CompletableFuture.completedFuture(null);

        } catch (Exception ex) {
            // 如果事务回滚，执行补偿机制，重新保存到第三方接口
            if (eqqueueId != null) {

                retryThirdPartySave(eqqueueId, params); // 调用补偿机制
            }
            throw ex;   // 抛出异常进行回滚
        }
    }

    //接入杜科的辅助决策报告
    @SneakyThrows
    private void handleAssessmentReportAssessment(EqEventTriggerDTO params, String eqqueueId) {
        System.out.println("前端构建文本传的参数" + params);
        String eqName = params.getEqName();
        String eqTime = params.getEqTime();
        String eqAddr = params.getEqAddr();
        Double latitude = params.getLatitude();
        Double longitude = params.getLongitude();
        Double eqMagnitude = params.getEqMagnitude();
        Double eqDepth = params.getEqDepth();

        // 存储所有其他政府乡镇的距离和震中裂度
        List<YaanCountyTown> otherCountyTownDistances = new ArrayList<>();


        // 创建 SimpleDateFormat 对象，用于解析原始时间字符串
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 将输入的时间字符串解析为 Date 对象
        Date parsedDate = inputFormat.parse(eqTime);

        // 创建 SimpleDateFormat 对象，用于格式化为目标格式
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");

        // 格式化 Date 对象为目标格式的字符串
        String formattedTime = outputFormat.format(parsedDate);
        String result = String.format(
                "中国地震台网正式(CC)测定：%s在%s（北纬%.2f度，东经%.2f度）发生%.1f级地震，震源深度%.1f公里。",
                formattedTime, eqAddr, latitude, longitude, eqMagnitude, eqDepth
        );
        System.out.println(result);
        //震中附近乡镇计算
        if (eqName.contains("四川")) {
            //调用数据库yaan_prvince_town表里的各个全省乡镇经纬度geom类型和latitude，longitude 循环计算所有的距离，取靠近上传经纬度最小距离值
            // 查询所有乡镇的经纬度（geom字段）
            List<YaanProvinceTown> townList = yaanProvinceTownMapper.selectList(null);

            // 创建上传经纬度的点
            String wkt = "POINT(" + longitude + " " + latitude + ")";
            WKTReader reader = new WKTReader();
            Point uploadPoint = null;
            try {
                uploadPoint = (Point) reader.read(wkt);
            } catch (Exception e) {
                e.printStackTrace();
            }

            double uploadLat = uploadPoint.getY(); // 上传点的纬度
            double uploadLon = uploadPoint.getX(); // 上传点的经度

            // 初始化最小距离
            double minDistance = Double.MAX_VALUE;
            String nearestTown = "";

            // 遍历所有乡镇，计算与上传经纬度的距离
            for (YaanProvinceTown town : townList) {
                Geometry geom = town.getGeom();
                if (geom != null && geom instanceof Point) {
                    // 获取乡镇的经纬度
                    Point townPoint = (Point) geom;
                    double townLat = townPoint.getY(); // 纬度
                    double townLon = townPoint.getX(); // 经度

                    // 计算震中到乡镇点的距离（单位：米）
                    double distanceToCountyTown = calculateDistance(uploadLat, uploadLon, townLat, townLon);
//                    otherCountyTownDistances.add(new YaanCountyTown(countyTown.getCountyTownName(), distanceToCountyTown, intensity));

                    // 计算两点之间的距离（单位：米）
                    if (distanceToCountyTown < minDistance) {
                        minDistance = distanceToCountyTown;
                        nearestTown = town.getFullProvinceTowmName();  // 获取最近的乡镇名称
                    }
                }
            }
            // 生成返回的附近乡镇结果字符串
            String fuJinTownResult = "震中距判断：震中位于" + nearestTown + "附近，";
            System.out.println(fuJinTownResult);
            //*****************************************************

            if (eqAddr.contains("雅安市")) {
                //显示文字，震中距离雅安市边界约distance公里，就将上传的经纬度latitude, longitude和雅安市行政边界表yaan_administrative_boundary计算每个震中距离找出最小值

                // 计算震中距离雅安市边界的最小距离

                // 查询雅安市行政边界的所有点或多边形（geom字段）
                List<YaanAdministrativeBoundary> boundaryList = yaanAdministrativeBoundaryMapper.selectList(null);

                // 初始化最小距离
                double minBoundaryDistance = Double.MAX_VALUE;

                // 遍历所有边界点或多边形，计算震中到边界的最小距离
                for (YaanAdministrativeBoundary boundary : boundaryList) {
                    Geometry boundaryGeom = boundary.getGeom();
                    if (boundaryGeom != null) {
                        if (boundaryGeom instanceof Point) {
                            // 点到点的距离
                            Point boundaryPoint = (Point) boundaryGeom;

                            double boundaryLat = boundaryPoint.getY(); // 纬度
                            double boundaryLon = boundaryPoint.getX(); // 经度

                            // 计算震中到乡镇点的距离（单位：米）
                            double distanceToCountyTown = calculateDistance(uploadLat, uploadLon, boundaryLat, boundaryLon);
                            // 更新最小距离
                            if (distanceToCountyTown < minBoundaryDistance) {
                                minBoundaryDistance = distanceToCountyTown;
                            }
                        }
                    }
                }

                // 计算后的最小距离（单位：公里）
                double minDistanceInKm = minBoundaryDistance;  // 转换为公里
                // 生成返回的行政边界距离结果
                String fuJinBoundaryResult = "距离雅安市边界约 " + String.format("%.1f", minDistanceInKm) + " 公里，";
                System.out.println(fuJinBoundaryResult);

                // 查询雅安市所有乡镇（yaan_villages表）
                List<YaanVillages> villageList = yaanVillagesMapper.selectList(null);

                // 初始化最小距离
                double minVillageDistance = Double.MAX_VALUE;
                String nearestVillage = "";

                // 遍历所有乡镇，计算与上传经纬度的距离
                for (YaanVillages village : villageList) {
                    Geometry geom = village.getGeom();
                    if (geom != null && geom instanceof Point) {
                        // 获取乡镇的经纬度
                        Point villagePoint = (Point) geom;
                        double villageLat = villagePoint.getY(); // 纬度
                        double villageLon = villagePoint.getX(); // 经度

                        // 计算震中到乡镇点的距离（单位：米）
                        double distanceToVillage =  calculateDistance(uploadLat, uploadLon, villageLat, villageLon);

                        // 更新最小距离
                        if (distanceToVillage < minVillageDistance) {
                            minVillageDistance = distanceToVillage;
                            nearestVillage = village.getVillagesName();  // 获取最近的乡镇名称
                        }
                    }
                }

                // 生成返回的乡镇距离结果
                String fuJinVillageResult = "距离雅安市" + nearestVillage + "政府约" + String.format("%.1f", minVillageDistance) + "公里，";
                System.out.println(fuJinVillageResult);

                // 查询雅安市政府相关的乡镇（yaan_county_town表）
                List<YaanCountyTown> countyTownList = yaanCountyTownMapper.selectList(null);

                double minCountyTownDistance = Double.MAX_VALUE;
                String nearestCountyTown = "";

                // 特殊的政府乡镇距离变量
                double yaanCityGovDistance = Double.MAX_VALUE;
                double sichuanProvGovDistance = Double.MAX_VALUE;

                // 遍历所有政府相关的乡镇，计算与上传经纬度的距离
                for (YaanCountyTown countyTown : countyTownList) {
                    Geometry geom = countyTown.getGeom();
                    if (geom != null && geom instanceof Point) {
                        // 获取政府乡镇的经纬度
                        Point countyTownPoint = (Point) geom;
                        // 提取经纬度
                        double countyTownLat = countyTownPoint.getY(); // 纬度
                        double countyTownLon = countyTownPoint.getX(); // 经度

                        // 计算震中到乡镇点的距离（单位：米）
                        double distanceToCountyTown =  calculateDistance(uploadLat, uploadLon, countyTownLat, countyTownLon);

                        // 单独处理雅安市政府和四川省政府的情况
                        if ("雅安市政府".equals(countyTown.getCountyTownName())) {
                            yaanCityGovDistance = distanceToCountyTown; // 转换为公里
                        } else if ("四川省政府".equals(countyTown.getCountyTownName())) {
                            sichuanProvGovDistance = distanceToCountyTown; // 转换为公里
                        } else {
                            // 正常更新最近的政府乡镇距离和名称
                            if (distanceToCountyTown < minCountyTownDistance) {
                                minCountyTownDistance = distanceToCountyTown;
                                nearestCountyTown = countyTown.getCountyTownName(); // 获取最近的政府乡镇名称
                            }
                        }
                    }
                }

                // 生成最终输出文字
                String fuJinCountyTownResult = String.format(
                        "距离雅安市" + nearestCountyTown + "约%.1f公里，距离雅安市政府约%.1f公里，距离四川省政府约%.1f公里。",
                        minCountyTownDistance, yaanCityGovDistance, sichuanProvGovDistance
                );
                // 生成返回的政府乡镇距离结果
                System.out.println(fuJinCountyTownResult);

                // 合并所有字符串
                String combinedResult = result + fuJinTownResult  + fuJinBoundaryResult + fuJinVillageResult + fuJinCountyTownResult;
                System.out.println(combinedResult);


                //*****************************************************

                //初步评估
                // 公式部分

                double intensity;  //震中点最大烈度（烈度衰减长轴计算结果）
                double intensity1; //辖区外地震雅安最大烈度
                double intensity2; //最大烈度（烈度衰减公式长轴计算结果）
                //主要受影响区域
                if (eqMagnitude > 5.5) {
                    // 震级大于5.5时使用这个公式
                    intensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(0.1 + 24)) - 0.4;
                    intensity1 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minDistanceInKm + 24)) - 0.4;
                    intensity2 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10( minVillageDistance + 24)) - 0.4;

                } else {
                    // 震级小于或等于5.5时使用这个公式
                    intensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(0.1 + 24)) - 0.6;
                    intensity1 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minDistanceInKm + 24)) - 0.6;
                    intensity2 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minVillageDistance + 24)) - 0.6;
                }
                int roundedIntensity = (int) Math.round(intensity);
                int roundedIntensity1 = (int) Math.round(intensity1);
                int roundedIntensity2 = (int) Math.round(intensity2);



                // 初始化标志，判断是否所有乡镇和村庄的烈度都与 roundedIntensity 相等
                // 初始化匹配计数
                int countyTownsMatchCount = 0;
                int villagesMatchCount = 0;

                StringBuilder CountyTown = new StringBuilder();
                StringBuilder VillageNames = new StringBuilder();
                double distanceToCountyTown;
                double distanceToVillage;

                // 遍历所有政府相关的乡镇，计算与上传经纬度的距离
                for (YaanCountyTown countyTown : countyTownList) {
                    // 跳过 "雅安市政府" 和 "四川省政府" 乡镇
                    if ("雅安市政府".equals(countyTown.getCountyTownName()) || "四川省政府".equals(countyTown.getCountyTownName())) {
                        continue; // 跳过当前循环，继续下一个乡镇
                    }
                    Geometry geom = countyTown.getGeom();
                    if (geom != null && geom instanceof Point) {
                        Point countyTownPoint = (Point) geom;
                        double countyTownLat = countyTownPoint.getY();
                        double countyTownLon = countyTownPoint.getX();
                        distanceToCountyTown = calculateDistance(uploadLat, uploadLon, countyTownLat, countyTownLon);
                        // 进行烈度计算
                        double CountyTownIntensity;
                        if (eqMagnitude > 5.5) {
                            // 震级大于5.5时使用这个公式
                            CountyTownIntensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(distanceToCountyTown + 24)) - 0.4;
                        } else {
                            // 震级小于或等于5.5时使用这个公式
                            CountyTownIntensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(distanceToCountyTown + 24)) - 0.6;
                        }

                        int CountyTownRoundedIntensity = (int) Math.round(CountyTownIntensity);

                        // 打印调试信息，查看烈度计算是否正确
                        System.out.println("县镇: " + countyTown.getCountyTownName() + ", 计算烈度: " + CountyTownRoundedIntensity + ", roundedIntensity: " + roundedIntensity);

                        // 比较计算出的烈度与 roundedIntensity 是否相等
                        if (CountyTownRoundedIntensity == roundedIntensity) {
                            // 如果烈度相等，将当前乡镇的名称添加到 CountyTown 字符串
                            if (CountyTown.length() > 0) {
                                CountyTown.append("、");
                            }
                            CountyTown.append(countyTown.getCountyTownName()); // 将乡镇名称添加到结果中
                            countyTownsMatchCount++; // 增加匹配计数
                        }
                    }
                }

                // 遍历所有村庄，计算与上传经纬度的距离
                for (YaanVillages village : villageList) {
                    Geometry geom = village.getGeom();
                    if (geom != null && geom instanceof Point) {
                        Point villagePoint = (Point) geom;
                        double villageLat = villagePoint.getY();
                        double villageLon = villagePoint.getX();
                        distanceToVillage = calculateDistance(uploadLat, uploadLon, villageLat, villageLon);
                        // 进行烈度计算
                        double VillageIntensity;
                        if (eqMagnitude > 5.5) {
                            // 震级大于5.5时使用这个公式
                            VillageIntensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(distanceToVillage + 24)) - 0.4;
                        } else {
                            // 震级小于或等于5.5时使用这个公式
                            VillageIntensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(distanceToVillage + 24)) - 0.6;
                        }
                        int VillageRoundedIntensity = (int) Math.round(VillageIntensity);

                        // 打印调试信息，查看烈度计算是否正确
                        System.out.println("村庄: " + village.getVillagesName() + ", 计算烈度: " + VillageRoundedIntensity + ", roundedIntensity: " + roundedIntensity);

                        // 比较计算出的烈度与 roundedIntensity 是否相等
                        if (VillageRoundedIntensity == roundedIntensity) {
                            // 如果烈度相等，将当前村庄的名称添加到 VillageNames 字符串
                            if (VillageNames.length() > 0) {
                                VillageNames.append("、");
                            }
                            VillageNames.append(village.getVillagesName()); // 将村庄名称添加到结果中
                            villagesMatchCount++; // 增加匹配计数
                        }
                    }
                }

// 合并乡镇和村庄的结果
                StringBuilder finalResult = new StringBuilder();

// 如果所有乡镇和村庄的烈度都与 roundedIntensity 相等，则输出 "全境"
                if (countyTownsMatchCount == countyTownList.size() && villagesMatchCount == villageList.size()) {
                    finalResult.append("(主要涉及全境)");
                } else {
                    // 否则拼接符合条件的乡镇和村庄名称
                    if (CountyTown.length() > 0) {
                        finalResult.append(CountyTown);
                    }
                    if (VillageNames.length() > 0) {
                        if (finalResult.length() > 0) {
                            finalResult.append("、");
                        }
                        finalResult.append(VillageNames);
                    }
                }

                // 如果最终结果为空，说明没有任何匹配项，输出 "无匹配乡镇或村庄"
                if (finalResult.length() == 0) {
                    finalResult.append("(无匹配乡镇或村庄)");
                }

                // 输出最终的结果
                System.out.println(finalResult);




                // 注：StringBuilder
                StringBuilder zhuResult = new StringBuilder();

// 根据烈度生成描述
                for (int i = 1; i <= roundedIntensity; i++) {
                    zhuResult.append("地震烈度").append(i).append("度主要现象为：");

                    if (i == 0 || i == 1) {
                        zhuResult.append("仅仪器能记录到，人一般无感。");
                    } else if (i == 2) {
                        zhuResult.append("敏感的人在完全静止中有感。");
                    } else if (i == 3) {
                        zhuResult.append("室内少数静止中的人有感，悬挂物轻微摆动。");
                    } else if (i == 4) {
                        zhuResult.append("室内大多数人有感，悬挂物摆动，不稳定器皿作响。");
                    } else if (i == 5) {
                        zhuResult.append("室外大多数人有感，门窗作响，不稳定器物摇动或翻倒，个别房屋墙壁抹灰出现裂缝。");
                    } else if (i == 6) {
                        zhuResult.append("人站立不稳，家具移动，简陋棚舍损坏，个别房屋轻微破坏。");
                    } else if (i == 7) {
                        zhuResult.append("多数房屋轻微破坏，少数家具倾倒，单砖建筑损坏，地表出现裂缝及喷沙冒水。");
                    } else if (i == 8) {
                        zhuResult.append("房屋不同程度破坏，少数严重破坏，多数家具倾倒或移位，路基塌方，地下管道破裂。");
                    } else if (i == 9) {
                        zhuResult.append("大多数房屋严重破坏，少数倾倒，滑坡、塌方多见。");
                    } else if (i == 10) {
                        zhuResult.append("大多数房屋毁坏，道路毁坏，山石大量崩塌，水面大浪扑岸。");
                    } else if (i == 11) {
                        zhuResult.append("绝大多数房屋毁坏，路基堤岸大段崩毁，地表产生很大变化。");
                    } else if (i == 12) {
                        zhuResult.append("房屋几乎全部毁坏，地面剧烈变化，山河改观。");
                    }
                }

                // 最后打印结果
                System.out.println(zhuResult.toString());

                // 返回结果


                // 取整并返回结果
                String roundedIntensityResult = String.format("震中区最大地震烈度达%d度", roundedIntensity);
                System.out.println(roundedIntensityResult);


                String roundedIntensityResult1 = String.format("我市最大地震烈度达%d度", roundedIntensity1);
                // 输出结果
                System.out.println(roundedIntensityResult1);

                String roundedIntensityResult2 = String.format("我市乡（镇）政府、街道办驻地最大地震烈度为%d度", roundedIntensity2);
                System.out.println(roundedIntensityResult2);

                // 判断 finalResult 是否包含 "、" 符号
                if (finalResult.toString().contains("、")) {
                    // 如果包含 "、"，则执行以下逻辑
                    String villagesName = String.format("（主要位于%s一带），", finalResult);
                    System.out.println(villagesName);
                } else {
                    // 如果不包含 "、"，可以执行其他逻辑（根据需求调整）
                    System.out.println(finalResult);
                }

                String countyTown = String.format("初步分析，%s。", finalResult.toString());
                System.out.println(countyTown);

                String zhuResult1 = String.format("注：%s。", zhuResult.toString());
                System.out.println(zhuResult1);


                WordExporter(combinedResult, formattedTime,eqMagnitude,eqAddr);

            }

            //不在雅安市地震
            if (!eqAddr.contains("雅安市")){

                // 查询雅安市行政边界的所有点或多边形（geom字段）
                List<YaanAdministrativeBoundary> boundaryList1 = yaanAdministrativeBoundaryMapper.selectList(null);

                // 初始化最小距离
                double minBoundaryDistance1 = Double.MAX_VALUE;

                // 遍历所有边界点或多边形，计算震中到边界的最小距离
                for (YaanAdministrativeBoundary boundary : boundaryList1) {
                    Geometry boundaryGeom = boundary.getGeom();
                    if (boundaryGeom != null) {

                        if (boundaryGeom instanceof Point) {
                            // 点到点的距离
                            Point boundaryPoint = (Point) boundaryGeom;

                            double boundaryLat = boundaryPoint.getY(); // 纬度
                            double boundaryLon = boundaryPoint.getX(); // 经度

                            // 计算震中到乡镇点的距离（单位：米）
                            double distanceToCountyTown1 = calculateDistance(uploadLat, uploadLon, boundaryLat, boundaryLon);
                            // 更新最小距离
                            if (distanceToCountyTown1 < minBoundaryDistance1) {
                                minBoundaryDistance1 = distanceToCountyTown1;
                            }
                        }
                    }
                }

                // 计算后的最小距离（单位：公里）
                double minDistanceInKm1 = minBoundaryDistance1;  // 转换为公里
                // 生成返回的行政边界距离结果
                String fuJinBoundaryResult1 = "距离雅安市边界约 " + String.format("%.1f", minDistanceInKm1) + " 公里，";
                System.out.println(fuJinBoundaryResult1);

                // 查询雅安市所有乡镇（yaan_villages表）
                List<YaanVillages> villageList1 = yaanVillagesMapper.selectList(null);

                // 初始化最小距离
                double minVillageDistance1 = Double.MAX_VALUE;
                String nearestVillage1 = "";

                // 遍历所有乡镇，计算与上传经纬度的距离
                for (YaanVillages village : villageList1) {
                    Geometry geom = village.getGeom();
                    if (geom != null && geom instanceof Point) {
                        // 获取乡镇的经纬度
                        Point villagePoint = (Point) geom;
                        double villageLat = villagePoint.getY(); // 纬度
                        double villageLon = villagePoint.getX(); // 经度

                        // 计算震中到乡镇点的距离（单位：米）
                        double distanceToVillage1 =  calculateDistance(uploadLat, uploadLon, villageLat, villageLon);

                        // 计算震中到乡镇点的距离（单位：米）

                        // 更新最小距离
                        if (distanceToVillage1 < minVillageDistance1) {
                            minVillageDistance1 = distanceToVillage1;
                            nearestVillage1 = village.getVillagesName();  // 获取最近的乡镇名称
                        }
                    }
                }

                // 生成返回的乡镇距离结果
                String fuJinVillageResult1 = "距离雅安市"+nearestVillage1+"政府约"+String.format("%.1f", minVillageDistance1) + "公里，";
                System.out.println(fuJinVillageResult1);

                // 查询雅安市政府相关的乡镇（yaan_county_town表）
                List<YaanCountyTown> countyTownList1 = yaanCountyTownMapper.selectList(null);

                double minCountyTownDistance1 = Double.MAX_VALUE;
                String nearestCountyTown1 = "";

                // 特殊的政府乡镇距离变量
                double yaanCityGovDistance1 = Double.MAX_VALUE;
                double sichuanProvGovDistance1 = Double.MAX_VALUE;

                // 遍历所有政府相关的乡镇，计算与上传经纬度的距离
                for (YaanCountyTown countyTown : countyTownList1) {
                    Geometry geom = countyTown.getGeom();
                    if (geom != null && geom instanceof Point) {
                        // 获取政府乡镇的经纬度
                        Point countyTownPoint = (Point) geom;
                        // 提取经纬度
                        double countyTownLat = countyTownPoint.getY(); // 纬度
                        double countyTownLon = countyTownPoint.getX(); // 经度

                        // 计算震中到乡镇点的距离（单位：米）
                        double distanceToCountyTown1 =  calculateDistance(uploadLat, uploadLon, countyTownLat, countyTownLon);

                        // 单独处理雅安市政府和四川省政府的情况
                        if ("雅安市政府".equals(countyTown.getCountyTownName())) {
                            yaanCityGovDistance1 = distanceToCountyTown1; // 转换为公里
                        } else if ("四川省政府".equals(countyTown.getCountyTownName())) {
                            sichuanProvGovDistance1 = distanceToCountyTown1; // 转换为公里
                        } else {
                            // 正常更新最近的政府乡镇距离和名称
                            if (distanceToCountyTown1 < minCountyTownDistance1) {
                                minCountyTownDistance1 = distanceToCountyTown1;
                                nearestCountyTown1 = countyTown.getCountyTownName(); // 获取最近的政府乡镇名称
                            }
                        }
                    }
                }

                // 生成最终输出文字
                String fuJinCountyTownResult1 = String.format(
                        "距离雅安市"+nearestCountyTown1+"约%.1f公里，距离雅安市政府约%.1f公里，距离四川省政府约%.1f公里。",
                        minCountyTownDistance1, yaanCityGovDistance1, sichuanProvGovDistance1
                );
                // 生成返回的政府乡镇距离结果
                System.out.println(fuJinCountyTownResult1);

                // 查询所有城市的经纬度（geom字段）
                List<YaanProvinceCity> cityList1 = yaanProvinceCityMapper.selectList(null);

                // 初始化最小距离
                double minCityDistance1 = Double.MAX_VALUE;
                String nearestCity1 = "";

                // 遍历所有城市，计算与上传经纬度的距离
                for (YaanProvinceCity city : cityList1) {
                    Geometry geom = city.getGeom();
                    System.out.println("geom:"+geom);
                    if (geom != null && geom instanceof Point) {
                        // 获取城市的经纬度
                        Point cityPoint = (Point) geom;

                        // 提取经纬度
                        double cityLat = cityPoint.getY(); // 纬度
                        double cityLon = cityPoint.getX(); // 经度

                        // 计算震中到城市点的距离（单位：米）
                        double distanceToCity1 = calculateDistance(uploadLat, uploadLon, cityLat, cityLon);
                        System.out.println("距离: " + distanceToCity1);

                        // 更新最小距离
                        if (distanceToCity1 < minCityDistance1) {
                            minCityDistance1 = distanceToCity1;
                            nearestCity1 = city.getFullProvinceCityName();  // 获取最近的城市名称
                        }
                    }
                }

                // 计算后的最小距离（单位：公里）
                double minCityDistanceInKm1 = minCityDistance1;// 转换为公里
                System.out.println("最小距离（公里）：" + minCityDistanceInKm1);

                // 生成返回的城市距离结果
                String fuJinCityResult1 = String.format("距离"+ nearestCity1 +"政府约%.2f公里，", minCityDistanceInKm1);
                System.out.println(fuJinCityResult1);

                // 合并所有字符串
                String combinedResult1 = result + fuJinTownResult + fuJinCityResult1 + fuJinBoundaryResult1 + fuJinVillageResult1 + fuJinCountyTownResult1;
                System.out.println(combinedResult1);
                WordExporter(combinedResult1,formattedTime,eqMagnitude,eqAddr);

            }

        }


    }
    //计算震中距离
    // 计算地理距离的方法（单位：公里）
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // 地球半径，单位：公里

        // 将经纬度从度转换为弧度
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);
        // 纬度和经度的差值
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Haversine公式
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 计算距离（单位：公里）
        double distance = R * c;

        return distance;


    }

    private void WordExporter(String combinedResult1,String formattedTime,Double eqMagnitude,String eqAddr) throws IOException {

        // 创建一个 XWPFDocument 对象
        XWPFDocument document = new XWPFDocument();
//        setPageMargins(document);

        // 空3行
        for (int i = 0; i < 4; i++) {
            document.createParagraph();
        }

        // 第一行：地震应急辅助决策信息，居中，方正小标宋简体，44号，红色
        XWPFParagraph firstParagraph = document.createParagraph();
        firstParagraph.setAlignment(ParagraphAlignment.CENTER); // 居中
        XWPFRun firstRun = firstParagraph.createRun();
        firstRun.setText("雅安应急值班信息");
        firstRun.setFontFamily("方正小标宋简体");  // 方正小标宋简体
        firstRun.setFontSize(55);  // 44号字体
        firstRun.setColor("FF0000");  // 红色

        // 第二行：〔2025〕第XX期
        XWPFParagraph secondParagraph = document.createParagraph();
        secondParagraph.setAlignment(ParagraphAlignment.CENTER); // 居中
        XWPFRun secondRun = secondParagraph.createRun();
        secondRun.setText("〔2025〕第XX期");
        secondRun.setFontFamily("仿宋_GB2312");  // 仿宋_GB2312
        secondRun.setFontSize(16);  // 三号字体

        // 空1行
        document.createParagraph();

        // 第三行：雅安市应急管理局 签发人：叶 飞
        XWPFParagraph thirdParagraph = document.createParagraph();
        thirdParagraph.setAlignment(ParagraphAlignment.CENTER); // 居中
        XWPFRun thirdRun = thirdParagraph.createRun();
        // 设置两部分文本，并在中间加入20个空格
        thirdRun.setText("雅安市应急管理局" + "                    " + "签发人：叶  飞"); // 20个空格
        thirdRun.setFontFamily("仿宋_GB2312"); // 仿宋_GB2312
        thirdRun.setFontSize(16); // 三号字体

        // 在第三行下方添加一条红色的下划线（0.5磅宽）
        XWPFParagraph lineParagraph = document.createParagraph(); // 新的一段
        XWPFRun lineRun = lineParagraph.createRun(); // 创建新的run来设置下划线
        lineRun.setText(""); // 空文本（只插入下划线）
        lineRun.setUnderline(UnderlinePatterns.SINGLE); // 设置下划线
        lineRun.setColor("FF0000"); // 设置颜色为红色
        lineRun.setFontSize(1); // 设置字体大小为1（较小值，仅用于显示线条）


        // 创建第四行：eqAddr发生eqMagnitude级地震
        XWPFParagraph fourthParagraph = document.createParagraph();
        fourthParagraph.setAlignment(ParagraphAlignment.CENTER); // 设置居中对齐

        // 创建运行并插入文本
        XWPFRun fourthRun = fourthParagraph.createRun();

        // 构建地震信息文本
        String earthquakeInfo = eqAddr + "发生" + eqMagnitude + "级地震";

        // 设置文本内容
        fourthRun.setText(earthquakeInfo);
        fourthRun.setFontFamily("方正小标宋简体"); // 设置字体为仿宋_GB2312
        fourthRun.setFontSize(22); // 设置字体大小为二号（22号字体）


        document.createParagraph();

        // 创建第五行：一、震情情况
        XWPFParagraph fifthParagraph = document.createParagraph();
        fifthParagraph.setAlignment(ParagraphAlignment.LEFT); // 设置左对齐
        // 设置首行缩进2个字符
        fifthParagraph.setFirstLineIndent(560); // 2个字符大约是560twips（1字符 = 280twips）
        // 创建运行并插入文本
        XWPFRun fifthRun = fifthParagraph.createRun();
        // 设置文本内容
        fifthRun.setText("一、震情情况");
        fifthRun.setFontFamily("仿宋_GB2312"); // 设置字体为仿宋_GB2312
        fifthRun.setFontSize(16); // 设置字体大小为16号（即三号字体）


        // 第一部分剩下的内容：combinedResult1
        XWPFParagraph contentParagraph = document.createParagraph();
        contentParagraph.setAlignment(ParagraphAlignment.LEFT); // 设置左对齐
        // 设置首行缩进2个字符
        contentParagraph.setFirstLineIndent(560); // 2个字符大约是560twips（1字符 = 280twips）
        XWPFRun contentRun = contentParagraph.createRun();
        contentRun.setText(combinedResult1);
        contentRun.setFontFamily("仿宋_GB2312");
        contentRun.setFontSize(16);  // 四号字体

        // 创建第六行： 二、灾情评估
        XWPFParagraph sixParagraph = document.createParagraph();
        sixParagraph.setAlignment(ParagraphAlignment.LEFT); // 设置左对齐
        // 设置首行缩进2个字符
        sixParagraph.setFirstLineIndent(560); // 2个字符大约是560twips（1字符 = 280twips）
        // 创建运行并插入文本
        XWPFRun sixRun = sixParagraph.createRun();
        // 设置文本内容
        sixRun.setText("二、灾情评估");
        sixRun.setFontFamily("仿宋_GB2312"); // 设置字体为仿宋_GB2312
        sixRun.setFontSize(16); // 设置字体大小为16号（即三号字体）

        // 第二部分剩下的内容：combinedResult1
        XWPFParagraph contentParagraph1 = document.createParagraph();
        contentParagraph1.setAlignment(ParagraphAlignment.LEFT); // 设置左对齐
        // 设置首行缩进2个字符
        contentParagraph1.setFirstLineIndent(560); // 2个字符大约是560twips（1字符 = 280twips）
        XWPFRun contentRun1 = contentParagraph1.createRun();  // 这里应该使用 contentParagraph1
        contentRun1.setText(combinedResult1);
        contentRun1.setFontFamily("仿宋_GB2312");
        contentRun1.setFontSize(16);  // 四号字体


        // 创建第七行：  详情续报。
        XWPFParagraph sevenParagraph = document.createParagraph();
        sevenParagraph.setAlignment(ParagraphAlignment.LEFT); // 设置左对齐
        // 设置首行缩进2个字符
        sevenParagraph.setFirstLineIndent(560); // 2个字符大约是560twips（1字符 = 280twips）
        // 创建运行并插入文本
        XWPFRun sevenRun = sevenParagraph.createRun();
        // 设置文本内容
        sevenRun.setText("详情续报。");
        sevenRun.setFontFamily("仿宋_GB2312"); // 设置字体为仿宋_GB2312
        sevenRun.setFontSize(16); // 设置字体大小为16号（即三号字体）

        document.createParagraph();

        // 创建第八行：雅安市应急管理 2025年01月07日
        XWPFParagraph eightParagraph = document.createParagraph();
        eightParagraph.setAlignment(ParagraphAlignment.CENTER); // 设置居中对齐
        // 创建运行并插入文本
        XWPFRun eightRun = eightParagraph.createRun();
        // 设置第一部分文本
        eightRun.setText("雅安市应急管理");
        // 手动换行
        eightRun.addBreak(); // 添加换行
        // 设置第二部分文本
        eightRun.setText(formattedTime);
        eightRun.setFontFamily("仿宋_GB2312"); // 设置字体为仿宋_GB2312
        eightRun.setFontSize(16); // 设置字体大小为16号（即三号字体）

        document.createParagraph();
        document.createParagraph();

        // 创建表格
        XWPFTable table = document.createTable(1, 1); // 创建一个1行1列的表格

        // 获取表格的第一行和第一列的单元格
        XWPFTableRow tableRow = table.getRow(0);
        XWPFTableCell cell = tableRow.getCell(0);
        cell.setWidth("8888");  // 5676 Twips = 15cm

        // 获取单元格的段落并设置水平垂直居中
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER); // 水平居中
        paragraph.setVerticalAlignment(TextAlignment.CENTER); // 垂直居中

        // 设置段落格式：单倍行距，段前段后为0
        paragraph.setSpacingBetween(1.0);  // 设置单倍行距
        paragraph.setSpacingBefore(0);     // 设置段前间距为0
        paragraph.setSpacingAfter(0);      // 设置段后间距为0

        // 强制设置单元格字体，以避免默认样式的影响
        XWPFRun runInsideTable = paragraph.createRun();
        runInsideTable.setFontFamily("仿宋_GB2312"); // 强制设置字体为仿宋_GB2312
        runInsideTable.setFontSize(14); // 强制设置字体大小为14
        runInsideTable.setText("报：市委总值班室、市政府总值班室");


        // 获取表格的边框对象，检查是否已经设置边框，如果未设置，则添加边框
        CTTblBorders borders = table.getCTTbl().getTblPr().addNewTblBorders();

        // 设置表格单元格边框为黑色
        CTBorder topBorder = borders.addNewTop();
        topBorder.setVal(STBorder.SINGLE);  // 单线
        topBorder.setSz(BigInteger.valueOf(12)); // 边框线宽度为12磅
        topBorder.setColor("000000");  // 黑色边框

        CTBorder bottomBorder = borders.addNewBottom();
        bottomBorder.setVal(STBorder.SINGLE);  // 单线
        bottomBorder.setSz(BigInteger.valueOf(12)); // 边框线宽度为12磅
        bottomBorder.setColor("000000");  // 黑色边框


        // 设置左右边框为空，即没有边框
        CTBorder leftBorder = borders.addNewLeft();
        leftBorder.setVal(STBorder.NIL);  // 左边框为空
        CTBorder rightBorder = borders.addNewRight();
        rightBorder.setVal(STBorder.NIL);  // 右边框为空





        // 创建第十行：值班员：XXX   电话：0835-2220001  时间：01月07日16时11分
        XWPFParagraph tenthParagraph = document.createParagraph();
        tenthParagraph.setAlignment(ParagraphAlignment.CENTER); // 设置左对齐
        // 创建运行并插入文本
        XWPFRun tenthRun = tenthParagraph.createRun();
        // 设置文本内容
        tenthRun.setText("值班员：XXX   电话：0835-2220001  时间:"+formattedTime);
        tenthRun.setFontFamily("仿宋_GB2312"); // 设置字体为仿宋_GB2312
        tenthRun.setFontSize(14); // 设置字体大小为16号（即三号字体）

        // 获取所有段落并设置格式
        for (XWPFParagraph paragraph1 : document.getParagraphs()) {
            paragraph1.setSpacingBetween(1.0);  // 设置单倍行距
            paragraph1.setSpacingBefore(0);  // 设置段前间距为0
            paragraph1.setSpacingAfter(0);   // 设置段后间距为0
        }

        // 构造文件路径
        String fileName = formattedTime + "级地震（值班信息）.docx";
        String filePath = "C:/Users/Smile/Desktop/" + fileName;
        // 设置页面边距
        setPageMargins(document,filePath);


        // 写入文件
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            document.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //     设置页面边距
    public void setPageMargins(XWPFDocument document,String filePath) throws IOException {
        // 获取页面设置对象
        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        CTPageMar pageMargins = sectPr.addNewPgMar();
//        获取页面边距设置对象

////         设置上下左右边距（单位：Twips，1 cm = 567.6 Twips）
        pageMargins.setTop(3.7 * 567.6);  // 上边距 3.7厘米转换为Twips
        pageMargins.setBottom(3.5 * 567.6);  // 下边距 3.5厘米转换为Twips
        pageMargins.setLeft(2.8 * 567.6);  // 左边距 2.8厘米转换为Twips
        pageMargins.setRight(2.6 * 567.6);  // 右边距 2.6厘米转换为Twips
//         保存修改
        document.getDocument().save(new File(filePath));
    }


    /**
     * @param eqqueueId 触发地震接口返回的eqqueueId
     * @param params    触发接口时上传的数据
     * @date: 2024/12/10 8:42
     * @description: 对存库失败进行重试操作，确保每个阶段的存库都正常
     */
    private void retrySaving(EqEventTriggerDTO params, String eqqueueId) {

        if (!asyncIntensity) {
            log.info("正在重试保存地震影响场数据...");
            handleSeismicYxcEventAssessment(params, eqqueueId);  // 对地震影响场的灾损评估数据进行保存重试
        }

        if (!asyncTown) {
            log.info("正在重试保存乡镇级评估数据...");
            handleTownLevelAssessment(params, eqqueueId);       // 对乡镇级的灾损评估数据进行保存重试
        }

        if (!asyncOutputMap) {
            log.info("正在重试保存专题图文件...");
            handleSpecializedAssessment(params, eqqueueId);     // 对专题图的灾损评估数据进行保存重试
        }

        if (!asyncOutputReport) {
            log.info("正在重试保存灾情报告文件...");
            handleDisasterReportAssessment(params, eqqueueId);  // 对灾情报告的数据进行保存重试
        }

        updateEventState(params.getEvent(), eqqueueId, 2);    // 修改批次表中的地震状态

    }

    /**
     * @param eqqueueId 触发地震接口返回的eqqueueId
     * @param params    触发接口时上传的数据
     * @author: xiaodemos
     * @date: 2024/12/4 18:05
     * @description: 补偿机制：当事务回滚后需要重新执行第三方接口数据保存操作
     */
    private void retryThirdPartySave(String eqqueueId, EqEventTriggerDTO params) {
        try {
            // 根据 eqqueueId 或其他标识判断是否需要重新插入数据
            handleThirdPartySeismicTrigger(params); // 重新调用第三方接口
            log.info("Successfully retried third-party save for eqqueueId: {}", eqqueueId);
        } catch (Exception ex) {
            log.error("Error retrying third-party save for eqqueueId: {}", eqqueueId, ex);
            // 可以根据需求进行重试次数限制或其他补偿处理
        }
    }

    /**
     * @param params 触发地震时的数据
     * @author: xiaodemos
     * @date: 2024/12/4 18:09
     * @description: 调用第三方地震触发接口
     * @return: 返回eqqueueid
     */
    private String handleThirdPartySeismicTrigger(EqEventTriggerDTO params) {

        try {

            return thirdPartyCommonApi.getSeismicTriggerByPost(params);

        } catch (Exception e) {

            e.printStackTrace();

            throw new ThirdPartyApiException(MessageConstants.THIRD_PARTY_API_ERROR);

        }
    }

    /**
     * @param params    触发地震时上传的数据
     * @param eqqueueId 触发地震时返回的eqqueueid
     * @author: xiaodemos
     * @date: 2024/12/4 18:10
     * @description: 进行地震影响场的灾损评估
     */
    @Async
    public CompletableFuture<Void> handleSeismicYxcEventAssessment(EqEventTriggerDTO params, String eqqueueId) {

        assessmentBatchService.updateBatchState(params.getEvent(), eqqueueId, 1);    // 修改状态正在执行评估中...

        try {
            EqEventGetYxcDTO eventGetYxcDTO = EqEventGetYxcDTO.builder()
                    .event(params.getEvent())
                    .eqqueueId(eqqueueId)
                    //.type("shpfile") //如果不指定type类型则默认返回geojson类型的数据
                    .build();

            String fileJsonstring = thirdPartyCommonApi.getSeismicEventGetYxcByGet(eventGetYxcDTO);

            log.info("事件编码 -> {}", params.getEvent());

            Double progress = getEventProgress(params.getEvent());

            while (progress < 10.00) {

                log.info("当前进度: {}%，等待达到20%再继续", progress);

                Thread.sleep(4000);  // 9秒后重新请求

                progress = getEventProgress(params.getEvent());

            }
            fileJsonstring = thirdPartyCommonApi.getSeismicEventGetYxcByGet(eventGetYxcDTO);
            String filePath = JsonParser.parseJsonToFileField(fileJsonstring);

            if (filePath != "" | StringUtils.isNotEmpty(filePath)) {

                saveIntensity(params, filePath, eqqueueId, "geojson");  // 把数据插入到己方数据库

                FileUtils.downloadFile(filePath, Constants.PROMOTION_DOWNLOAD_PATH);     // 下载文件并保存到本地

                log.info("下载并且保存geojson文件成功");

                return CompletableFuture.completedFuture(null);
            }

            return CompletableFuture.failedFuture(new AsyncExecuteException(MessageConstants.YXC_ASYNC_EXECUTE_ERROR));

        } catch (Exception e) {

            updateEventState(params.getEvent(), eqqueueId, 4);    // 修改状态评估异常停止...

            e.printStackTrace();

            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * @param params    触发地震时上传的数据
     * @param eqqueueId 触发地震时返回的eqqueueid
     * @author: xiaodemos
     * @date: 2024/12/4 18:10
     * @description: 进行乡镇级经济建筑人员伤亡的灾损评估
     */
    @Async
    public CompletableFuture<Void> handleTownLevelAssessment(EqEventTriggerDTO params, String eqqueueId) {

        try {

            EqEventGetResultTownDTO eqEventGetResultTownDTO = EqEventGetResultTownDTO.builder()
                    .event(params.getEvent())
                    .eqqueueId(eqqueueId)
                    .build();


            String seismicEventResultTown = thirdPartyCommonApi.getSeismicEventGetGetResultTownByGet(eqEventGetResultTownDTO);

            Double progress = getEventProgress(params.getEvent());

            while (progress < 25.00) {

                log.info("当前进度: {}%，等待达到40%再继续", progress);

                Thread.sleep(4000);  // 9秒后重新请求

                progress = getEventProgress(params.getEvent());

            }

            seismicEventResultTown = thirdPartyCommonApi.getSeismicEventGetGetResultTownByGet(eqEventGetResultTownDTO);

            ResultEventGetResultTownDTO resultEventGetResultTownDTO = JsonParser.parseJson(
                    seismicEventResultTown,
                    ResultEventGetResultTownDTO.class);

            List<ResultEventGetResultTownVO> eventGetResultTownDTOData = resultEventGetResultTownDTO.getData();

            if (eventGetResultTownDTOData.size() != MessageConstants.RESULT_ZERO) {

                saveTownResult(eventGetResultTownDTOData);  // 保存到己方数据库

                log.info("保存乡镇结果成功");

                return CompletableFuture.completedFuture(null);
            }

            return CompletableFuture.failedFuture(new AsyncExecuteException(MessageConstants.XZ_ASYNC_EXECUTE_ERROR));

        } catch (Exception e) {

            updateEventState(params.getEvent(), eqqueueId, 4);    // 修改状态评估异常停止...

            e.printStackTrace();

            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * @param params    触发地震时上传的数据
     * @param eqqueueId 触发地震时返回的eqqueueid
     * @author: xiaodemos
     * @date: 2024/12/4 18:10
     * @description: 获取地震评估的专题图
     */
    @Async
    public CompletableFuture<Void> handleSpecializedAssessment(EqEventTriggerDTO params, String eqqueueId) {

        try {

            EqEventGetMapDTO getMapDTO = EqEventGetMapDTO.builder()
                    .event(params.getEvent())
                    .eqqueueId(eqqueueId)
                    .build();

            String eventGetMap = thirdPartyCommonApi.getSeismicEventGetMapByGet(getMapDTO);

            Double progress = getEventProgress(params.getEvent());

            while (progress < 100.00) {

                log.info("当前进度: {}%，等待达到100%再继续", progress);

                Thread.sleep(4000);  // 9秒后重新请求

                progress = getEventProgress(params.getEvent());

            }

            eventGetMap = thirdPartyCommonApi.getSeismicEventGetMapByGet(getMapDTO);

            ResultEventGetMapDTO resultEventGetMapDTO = JsonParser.parseJson(eventGetMap, ResultEventGetMapDTO.class);
            List<ResultEventGetMapVO> eventGetMapDTOData = resultEventGetMapDTO.getData();

            if (eventGetMapDTOData.size() != MessageConstants.RESULT_ZERO) {

                saveMap(eventGetMapDTOData, params.getEvent());  // 保存到己方数据库

                log.info("保存专题图成功");

                return CompletableFuture.completedFuture(null);
            }

            return CompletableFuture.failedFuture(new AsyncExecuteException(MessageConstants.ZTT_ASYNC_EXECUTE_ERROR));

        } catch (Exception e) {

            updateEventState(params.getEvent(), eqqueueId, 4);    // 修改状态评估异常停止...

            e.printStackTrace();

            return CompletableFuture.failedFuture(e);
        }

    }

    /**
     * @param params    触发地震时上传的数据
     * @param eqqueueId 触发地震时返回的eqqueueid
     * @author: xiaodemos
     * @date: 2024/12/4 18:10
     * @description: 获取地震评估的灾情报告
     */
    @Async
    public CompletableFuture<Void> handleDisasterReportAssessment(EqEventTriggerDTO params, String eqqueueId) {

        try {

            EqEventGetReportDTO getReportDTO = EqEventGetReportDTO.builder()
                    .event(params.getEvent())
                    .eqqueueId(eqqueueId)
                    .build();

            String eventGetReport = thirdPartyCommonApi.getSeismicEventGetReportByGET(getReportDTO);

            Double progress = getEventProgress(params.getEvent());

            while (progress < 100.00) {

                log.info("当前进度: {}%，等待达到100%再继续", progress);

                Thread.sleep(4000);  // 9秒后重新请求

                progress = getEventProgress(params.getEvent());

            }

            eventGetReport = thirdPartyCommonApi.getSeismicEventGetReportByGET(getReportDTO);

            ResultEventGetReportDTO resultEventGetReportDTO = JsonParser.parseJson(eventGetReport, ResultEventGetReportDTO.class);
            List<ResultEventGetReportVO> eventGetReportDTOData = resultEventGetReportDTO.getData();

            if (eventGetReportDTOData.size() != MessageConstants.RESULT_ZERO) {

                saveReport(eventGetReportDTOData, params.getEvent());  // 保存到己方数据库

                log.info("保存灾情报告结果成功");

                return CompletableFuture.completedFuture(null);
            }

            return CompletableFuture.failedFuture(new AsyncExecuteException(MessageConstants.BG_ASYNC_EXECUTE_ERROR));

        } catch (Exception e) {

            updateEventState(params.getEvent(), eqqueueId, 4);    // 修改状态评估 异常停止...

            e.printStackTrace();

            return CompletableFuture.failedFuture(e);
        }

    }

    /**
     * @param eqid           地震id
     * @param eventGetReport 灾情报告
     * @author: xiaodemos
     * @date: 2024/12/4 14:32
     * @description: 保存灾情报告结果
     */
    public void saveReport(List<ResultEventGetReportVO> eventGetReport, String eqid) {

        List<AssessmentOutput> saveList = new ArrayList<>();
        for (ResultEventGetReportVO res : eventGetReport) {
            AssessmentOutput assessmentOutput = AssessmentOutput.builder()
                    .id(res.getId())
                    .eqqueueId(res.getEqqueueId())
                    .eqid(eqid)
                    .code(res.getCode())
                    .proTime(res.getProTime())
                    .fileType(res.getFileType())
                    .fileName(res.getFileName())
                    .fileExtension(res.getFileExtension())
                    .fileSize(res.getFileSize())
                    .sourceFile(res.getSourceFile())
                    .localSourceFile(Constants.PROMOTION_INVOKE_URL_HEAD + res.getSourceFile())
                    .remark(res.getRemark())
                    .type("2")
                    .size(res.getSize())
                    .build();

            // BeanUtils.copyProperties(res, assessmentOutput);

            saveList.add(assessmentOutput);

            try {

                log.info("--------------灾情报告准备开始下载--------------{}", res.getSourceFile());

                FileUtils.downloadFile(res.getSourceFile(), Constants.PROMOTION_DOWNLOAD_PATH);

            } catch (IOException e) {
                e.printStackTrace();

                // 如果非必须就不要抛出异常，能下载多少就下载多少。

                // throw new FileDownloadException(MessageConstants.FILE_DOWNLOAD_ERROR);
            }
        }

        asyncOutputReport = assessmentOutputService.saveBatch(saveList);

    }

    /**
     * @param eventGetMap 专题图数据
     * @param eqid        地震id
     * @author: xiaodemos
     * @date: 2024/12/3 23:51
     * @description: 保存专题图数据到数据库并下载文件到本地
     */
    public void saveMap(List<ResultEventGetMapVO> eventGetMap, String eqid) {

        List<AssessmentOutput> saveList = new ArrayList<>();
        for (ResultEventGetMapVO res : eventGetMap) {
            AssessmentOutput assessmentOutput = AssessmentOutput.builder()
                    .id(res.getId())
                    .eqqueueId(res.getEqqueueId())
                    .eqid(eqid)
                    .code(res.getCode())
                    .proTime(res.getProTime())
                    .fileType(res.getFileType())
                    .fileName(res.getFileName())
                    .fileExtension(res.getFileExtension())
                    .fileSize(res.getFileSize())
                    .sourceFile(res.getSourceFile())
                    .localSourceFile(Constants.PROMOTION_INVOKE_URL_HEAD + res.getSourceFile())
                    .remark(res.getRemark())
                    .size(res.getSize())
                    .type("1")
                    .size(res.getSize())
                    .build();

            // BeanUtils.copyProperties(res, assessmentOutput);

            saveList.add(assessmentOutput);

            try {
                log.info("--------------专题图准备开始下载--------------{}", res.getSourceFile());

                FileUtils.downloadFile(res.getSourceFile(), Constants.PROMOTION_DOWNLOAD_PATH);

            } catch (IOException e) {

                e.printStackTrace();

                // throw new FileDownloadException(MessageConstants.FILE_DOWNLOAD_ERROR);

            }
        }

        asyncOutputMap = assessmentOutputService.saveBatch(saveList);
    }

    /**
     * @param eventResult 评估结果
     * @author: xiaodemos
     * @date: 2024/12/2 19:41
     * @description: 批量保存乡镇级结果到己方数据库
     */
    public void saveTownResult(List<ResultEventGetResultTownVO> eventResult) {

        List<AssessmentResult> saveList = new ArrayList<>();

        for (ResultEventGetResultTownVO res : eventResult) {
            AssessmentResult assessmentResult = AssessmentResult.builder()
                    .id(UUID.randomUUID().toString())
                    .eqqueueId(res.getEqqueueId())
                    .eqid(res.getEvent())
                    .batch(res.getBatch())
                    .eqName(res.getEqName())
                    .inty(res.getInty())
                    .pac(res.getPac())
                    .pacName(res.getPacName())
                    .buildingDamage(String.valueOf(res.getBuildingDamage()))
                    .pop(res.getPop())
                    .death(res.getDeath())
                    .missing(res.getMissing())
                    .injury(res.getInjury())
                    .buriedCount(res.getBuriedCount())
                    .resetNumber(res.getResetNumber())
                    .economicLoss(String.valueOf(res.getEconomicLoss()))
                    .build();

            //BeanUtils.copyProperties(res, assessmentResult);

            saveList.add(assessmentResult);
        }
        asyncTown = assessmentResultService.saveBatch(saveList);
    }

    /**
     * @param params    上传的参数
     * @param filePath  返回的影响场文件路径
     * @param eqqueueId 评估批次编码
     * @author: xiaodemos
     * @date: 2024/12/2 23:17
     * @description: 保存地震影响场的灾损评估结果到数据库
     */
    public void saveIntensity(EqEventTriggerDTO params, String filePath, String eqqueueId, String fileType) {

        AssessmentIntensity assessmentIntensity = AssessmentIntensity.builder()
                .id(UUID.randomUUID().toString())
                .eqqueueId(eqqueueId)
                .batch("1")
                .file(filePath)
                .eqid(params.getEvent())
                .fileType(fileType)
                .localFile(filePath)
                .build();

        asyncIntensity = assessmentIntensityService.save(assessmentIntensity);
    }

    /**
     * @param eqqueueId 查询触发的那条地震
     * @author: xiaodemos
     * @date: 2024/11/26 17:07
     * @description: 需要把字段转换成保存数据到我们的数据库中
     */
    public void getWithSave(EqEventTriggerDTO params, String eqqueueId) {
        // 这个eqqueueid可能存在多个批次，所以需要最新的那一个批次保存到本地，批次应该插入到多对多的那张表中
        AssessmentBatch batch = AssessmentBatch.builder()
                .eqqueueId(eqqueueId)
                .eqid(params.getEvent())
                .batch(1)
                .state(0)
                .type("1")
                .progress(0.0)
                .remark("")
                .build();

        boolean flag = assessmentBatchService.save(batch);

        if (!flag) {
            throw new DataSaveException(MessageConstants.DATA_SAVE_FAILED);
        }

        log.info("触发的数据已经同步到批次表中 -> : ok");

        EqEventGetPageDTO dto = EqEventGetPageDTO.builder().event(params.getEvent()).build();

        String seismicEvent = thirdPartyCommonApi.getSeismicEventByGet(dto);

        log.info("解析的json字符串 seismicEvent -> : {}", seismicEvent);

        //转换为JSONObject
        ResultEventGetPageDTO parsed = JsonParser.parseJson(seismicEvent, ResultEventGetPageDTO.class);
        ResultEventGetPageVO resultEventGetPageVO = parsed.getData().getRows().get(0);

        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(params.getLongitude(), params.getLatitude()));

        // TODO 修改数据库字段与dto保持一致可以优化这段代码
        EqList eqList = EqList.builder()
                .eqid(resultEventGetPageVO.getEvent())
                .eqqueueId(eqqueueId)
                .earthquakeName(resultEventGetPageVO.getEqName())
                .earthquakeFullName(resultEventGetPageVO.getEqFullName())
                .geom(point)
                .depth(resultEventGetPageVO.getEqDepth().toString())
                .magnitude(resultEventGetPageVO.getEqMagnitude())
                .occurrenceTime(LocalDateTime.parse(
                        params.getEqTime(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                ))     //这里是上传dto时保存的地震时间
                .pac("")
                .type("")
                .isDeleted(0)
                .build();

        BeanUtils.copyProperties(resultEventGetPageVO, eqList);

        eqListService.save(eqList);

        log.info("触发的数据已经同步到 eqlist 表中 -> : ok");

    }

    /**
     * @author: xiaodemos
     * @date: 2024/12/6 13:19
     * @description: 用于判断地震触发后获取到的灾损评估数据是否保存到己方数据库成功
     * @return: 返回True 或者 False
     */
    public boolean isSaved() {
        return asyncIntensity && asyncTown && asyncOutputMap && asyncOutputReport;
    }

    /**
     * @param eqId      事件编码
     * @param eqqueueId 批次编码
     * @param state     状态
     * @author: xiaodemos
     * @date: 2024/12/14 16:08
     * @description: 对状态进行更新
     */
    public void updateEventState(String eqId, String eqqueueId, int state) {
        assessmentBatchService.updateBatchState(eqId, eqqueueId, state);
    }

    /**
     * @param eqId 事件编码
     * @author: xiaodemos
     * @date: 2024/12/14 16:09
     * @description: 根据Id查询这场评估结果的进度
     * @return: 返回批次进度
     */
    public Double getEventProgress(String eqId) {

        AssessmentBatch processes = assessmentProcessesService.getSeismicAssessmentProcesses(eqId);
        return processes.getProgress();
    }

}
