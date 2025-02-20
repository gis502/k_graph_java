package com.ruoyi.web.api.service;

import com.ruoyi.system.domain.dto.EqEventTriggerDTO;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.mapper.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author: xiaodemos
 * @date: 2024-11-26 2:45
 * @description: 地震触发业务逻辑
 */

@Slf4j
@Service
public class SeismicTableTriggerService {

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

    //接入杜科的辅助决策报告
    @SneakyThrows
    public void tableFile(EqEventTriggerDTO params, String eqqueueId) {
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


        // 表格一：
        List<List<String>> table1 = new ArrayList<>();
        // 表头：
        List<String> headerRow = new ArrayList<>();
        headerRow.add("序号");
        headerRow.add("政府名称");
        headerRow.add("震中距");
        headerRow.add("预估烈度");
        headerRow.add("一般建筑设防烈度");
        table1.add(headerRow);

        // 政府名称：
        List<YaanCountyTown> countyTownTableList1 = yaanCountyTownMapper.selectList(null);
        // 创建一个列表用于存储政府名称
        List<String> countyTownNames = new ArrayList<>();
        // 遍历所有县镇记录，提取政府名称
        for (YaanCountyTown countyTown : countyTownTableList1) {
            // 获取县镇名称
            String countyTownName = countyTown.getCountyTownName();
            // 将政府名称添加到列表中
            countyTownNames.add(countyTownName);
        }
        // 获取政府数量
        int rowCount = countyTownTableList1.size(); // 获取政府名称这一列的数量

        // 震中距：
        List<String> distanceColumn1 = new ArrayList<>();
        // 创建上传经纬度的点
        String wkt1 = "POINT(" + longitude + " " + latitude + ")";
        WKTReader reader1 = new WKTReader();
        Point uploadPoint1 = null;
        try {
            uploadPoint1 = (Point) reader1.read(wkt1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        double uploadLat1 = uploadPoint1.getY(); // 上传点的纬度
        double uploadLon1 = uploadPoint1.getX(); // 上传点的经度
        // 特殊的政府乡镇距离变量
        double yaanCityGovDistance2 = Double.MAX_VALUE;
        double sichuanProvGovDistance2 = Double.MAX_VALUE;

        // 特殊政府名称
        String yaanCityGovName = "雅安市政府";
        String sichuanProvGovName = "四川省政府";

        // 遍历所有政府相关的乡镇，计算震中距
        for (YaanCountyTown countyTown : countyTownTableList1) {
            Geometry geom = countyTown.getGeom();
            if (geom != null && geom instanceof Point) {
                // 获取政府乡镇的经纬度
                Point countyTownPoint = (Point) geom;
                double countyTownLat = countyTownPoint.getY(); // 纬度
                double countyTownLon = countyTownPoint.getX(); // 经度

                // 计算震中距（单位：米）
                double distanceToCountyTown1 = calculateDistance(uploadLat1, uploadLon1, countyTownLat, countyTownLon);

                // 特殊处理雅安市政府和四川省政府
                if (yaanCityGovName.equals(countyTown.getCountyTownName())) {
                    yaanCityGovDistance2 = distanceToCountyTown1;
                } else if (sichuanProvGovName.equals(countyTown.getCountyTownName())) {
                    sichuanProvGovDistance2 = distanceToCountyTown1;
                }

                // 四舍五入取整（单位：公里）
                int roundedDistance = (int) distanceToCountyTown1;

                // 将四舍五入后的整数结果转换为字符串，并加上单位“公里”
                distanceColumn1.add(roundedDistance + "公里");

            } else {
                // 如果 geom 为空，则填入默认值
                distanceColumn1.add("N/A");
            }
        }

        // 预估烈度：
        // 存储预估烈度
        List<String> estimatedIntensities = new ArrayList<>(); // 修改为 List<String> 以存储带单位的字符串
        // 预估烈度计算公式的常数
        for (YaanCountyTown countyTown : countyTownTableList1) {
            Geometry geom = countyTown.getGeom();
            if (geom != null && geom instanceof Point) {
                Point countyTownPoint = (Point) geom;
                double countyTownLat = countyTownPoint.getY();
                double countyTownLon = countyTownPoint.getX();
                double distanceToCountyTown = calculateDistance(uploadLat1, uploadLon1, countyTownLat, countyTownLon);
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

                // 将预估烈度转换为带单位的字符串
                estimatedIntensities.add(CountyTownRoundedIntensity + "度"); // 添加单位“度”
            }
        }

        // 一般建筑设防烈度：
        // 创建一个列表用于存储建筑设防烈度
        List<String> SeismicFortificationIntensity = new ArrayList<>();

        // 遍历所有县镇记录，提取建筑设防烈度
        for (YaanCountyTown countyTown : countyTownTableList1) {
            // 获取建筑设防烈度
            String countyTownSeismicFortificationIntensity = String.valueOf(countyTown.getSeismicFortificationIntensity());
            // 将建筑设防烈度添加到列表中
            SeismicFortificationIntensity.add(countyTownSeismicFortificationIntensity + "度");
        }

        // 序号：
        for (int i = 0; i < rowCount; i++) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(i + 1)); // 序号从1开始
            row.add(countyTownTableList1.get(i).getCountyTownName()); // 政府名称
            row.add(distanceColumn1.get(i)); //震中距
            row.add(String.valueOf(estimatedIntensities.get(i))); // 预估烈度
            row.add(SeismicFortificationIntensity.get(i)); // 一般建筑设防烈度
            table1.add(row);
        }

        // 表格二：
        List<List<String>> table2 = new ArrayList<>();
        // 表头：
        List<String> headerRow2 = new ArrayList<>();
        headerRow2.add("序号");
        headerRow2.add("乡镇名称");
        headerRow2.add("震中距");
        headerRow2.add("预估烈度");
        headerRow2.add("一般建筑设防烈度");
        table2.add(headerRow2);

        // 乡镇名称：
        List<YaanVillages> villageTableList = yaanVillagesMapper.selectList(null);
        // 创建一个列表用于存储乡镇名称
        List<String> villageNames = new ArrayList<>();
        // 遍历所有乡镇记录，提取政府名称
        for (YaanVillages village : villageTableList) {
            // 获取乡镇名称
            String villageName = village.getVillagesName();
            // 将政府名称添加到列表中
            villageNames.add(villageName);
        }

        // 获取政府数量
        int rowCount2 = villageTableList.size(); // 获取政府名称这一列的数量

        // 震中距：
        List<String> distanceColumn2 = new ArrayList<>(); // 修改为 List<String> 以存储带单位的字符串
        // 创建上传经纬度的点
        String wkt2 = "POINT(" + longitude + " " + latitude + ")";
        WKTReader reader2 = new WKTReader();
        Point uploadPoint2 = null;
        try {
            uploadPoint2 = (Point) reader2.read(wkt2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        double uploadLat2 = uploadPoint2.getY(); // 上传点的纬度
        double uploadLon2 = uploadPoint2.getX(); // 上传点的经度
        // 遍历所有的乡镇，计算震中距
        for (YaanVillages village : villageTableList) {
            Geometry geom = village.getGeom();
            if (geom != null && geom instanceof Point) {
                // 获取乡镇的经纬度
                Point villagePoint = (Point) geom;
                double villageLat = villagePoint.getY(); // 纬度
                double villageLon = villagePoint.getX(); // 经度

                // 计算震中到乡镇点的距离
                double distanceToVillageMeters = calculateDistance(uploadLat2, uploadLon2, villageLat, villageLon);

                // 将距离转换为公里，并四舍五入取整
                int distanceToVillageKm = (int) distanceToVillageMeters;

                // 将四舍五入后的整数结果转换为字符串，并加上单位“公里”
                distanceColumn2.add(distanceToVillageKm + "公里");
            }
        }


        // 预估烈度：
        // 存储预估烈度
        List<String> estimatedIntensities2 = new ArrayList<>(); // 修改为 List<String> 以存储带单位的字符串
        // 预估烈度计算公式的常数
        for (YaanVillages village : villageTableList) {
            Geometry geom = village.getGeom();
            if (geom != null && geom instanceof Point) {
                Point villagePoint = (Point) geom;
                double villageLat = villagePoint.getY(); // 纬度
                double villageLon = villagePoint.getX(); // 经度
                double distanceToVillage = calculateDistance(uploadLat2, uploadLon2, villageLat, villageLon);
                // 进行烈度计算
                double villageIntensity;
                if (eqMagnitude > 5.5) {
                    // 震级大于5.5时使用这个公式
                    villageIntensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(distanceToVillage + 24)) - 0.4;
                } else {
                    // 震级小于或等于5.5时使用这个公式
                    villageIntensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(distanceToVillage + 24)) - 0.6;
                }
                int villageRoundedIntensity = (int) Math.round(villageIntensity);

                // 将预估烈度转换为带单位的字符串
                estimatedIntensities2.add(villageRoundedIntensity + "度"); // 添加单位“度”
            }
        }

        // 一般建筑设防烈度：
        // 创建一个列表用于存储建筑设防烈度
        List<String> SeismicFortificationIntensity2 = new ArrayList<>();

        // 遍历所有县镇记录，提取建筑设防烈度
        for (YaanVillages village : villageTableList) {
            // 获取建筑设防烈度
            String villageSeismicFortificationIntensity = String.valueOf(village.getSeismicFortificationIntensity());
            // 将建筑设防烈度添加到列表中
            SeismicFortificationIntensity2.add(villageSeismicFortificationIntensity + "度");
        }

        // 序号：
        for (int i = 0; i < rowCount2; i++) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(i + 1)); // 序号从1开始
            row.add(villageNames.get(i)); // 政府名称
            row.add(String.valueOf(distanceColumn2.get(i))); //震中距
            row.add(String.valueOf(estimatedIntensities2.get(i))); // 预估烈度
            row.add(SeismicFortificationIntensity2.get(i)); // 一般建筑设防烈度
            table2.add(row);
        }

        // 震中附近乡镇计算
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
//                  otherCountyTownDistances.add(new YaanCountyTown(countyTown.getCountyTownName(), distanceToCountyTown, intensity));

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

            List<YaanCountyTown> countyTownList = null;
            List<YaanVillages> villageList = null;
            double minDistanceInKm = 0;
            double minVillageDistance=0;
            String combinedResult11="";
            String combinedResult12="";


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
                minDistanceInKm = minBoundaryDistance;  // 转换为公里
                // 生成返回的行政边界距离结果
                String fuJinBoundaryResult = "距离雅安市边界约 " + String.format("%.1f", minDistanceInKm) + " 公里，";
                System.out.println(fuJinBoundaryResult);

                // 查询雅安市所有乡镇（yaan_villages表）
                villageList = yaanVillagesMapper.selectList(null);

                // 初始化最小距离
                minVillageDistance = Double.MAX_VALUE;
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
                countyTownList = yaanCountyTownMapper.selectList(null);

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
                combinedResult11 = result + fuJinTownResult  + fuJinBoundaryResult + fuJinVillageResult + fuJinCountyTownResult;
                System.out.println(combinedResult11);


                //*****************************************************

                // 初步评估
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
                combinedResult12 = result + fuJinTownResult + fuJinCityResult1 + fuJinBoundaryResult1 + fuJinVillageResult1 + fuJinCountyTownResult1;
                System.out.println(combinedResult12);

                System.out.println("101010101010");
            }

            //                初步评估 公式部分

            double intensity;  //震中点最大烈度（烈度衰减长轴计算结果）
            double intensity1; //辖区外地震雅安最大烈度
            double intensity2; //最大烈度（烈度衰减公式长轴计算结果）
            //主要受影响区域
            if (eqMagnitude > 5.5) {
                // 震级大于5.5时使用这个公式
                intensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(0.1 + 24)) - 0.4;
                intensity1 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minDistanceInKm + 24)) - 0.4;
                intensity2 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minVillageDistance + 24)) - 0.4;

            } else {
                // 震级小于或等于5.5时使用这个公式
                intensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(0.1 + 24)) - 0.6;
                intensity1 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minDistanceInKm + 24)) - 0.6;
                intensity2 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minVillageDistance + 24)) - 0.6;
            }
            int roundedIntensity = (int) Math.round(intensity);
            int roundedIntensity1 = (int) Math.round(intensity1);
            int roundedIntensity2 = (int) Math.round(intensity2);

            if(roundedIntensity<0||roundedIntensity1<0||roundedIntensity2<0){
                roundedIntensity=0;
                roundedIntensity1=0;
                roundedIntensity2=0;
            }
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
                    if (CountyTownRoundedIntensity<0){
                        CountyTownRoundedIntensity=0;
                    }

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

            //初步分析
            // 声明一个变量用于存储结果
            StringBuilder FinalCountyTown = new StringBuilder();
            StringBuilder CountyTownResult1 = new StringBuilder();
            StringBuilder CountyTownResult2 = new StringBuilder();
            StringBuilder CountyTownResult3 = new StringBuilder();
            StringBuilder CountyTownResult4 = new StringBuilder();
            StringBuilder CountyTownResult5 = new StringBuilder();
            boolean hasIntensity1Or0 = false;
            boolean hasIntensity2 = false;
            boolean hasIntensity3Or4 = false;
            boolean hasIntensity5 = false;
            boolean hasIntensity6To12 = false;
            for (YaanCountyTown countyTown2 : countyTownList) {
                // 跳过 "雅安市政府" 和 "四川省政府" 乡镇
                if ("雅安市政府".equals(countyTown2.getCountyTownName()) || "四川省政府".equals(countyTown2.getCountyTownName())) {
                    continue; // 跳过当前循环，继续下一个乡镇
                }
                Geometry geom2 = countyTown2.getGeom();
                if (geom2 != null && geom2 instanceof Point) {
                    Point countyTownPoint = (Point) geom2;
                    double countyTownLat = countyTownPoint.getY();
                    double countyTownLon = countyTownPoint.getX();
                    distanceToCountyTown = calculateDistance(uploadLat, uploadLon, countyTownLat, countyTownLon);
                    // 进行烈度计算
                    double CountyTownIntensity2;
                    if (eqMagnitude > 5.5) {
                        // 震级大于5.5时使用这个公式
                        CountyTownIntensity2 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(distanceToCountyTown + 24)) - 0.4;
                    } else {
                        // 震级小于或等于5.5时使用这个公式
                        CountyTownIntensity2 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(distanceToCountyTown + 24)) - 0.6;
                    }

                    int CountyTownRoundedIntensity2 = (int) Math.round(CountyTownIntensity2);

                    if (CountyTownRoundedIntensity2<0){
                        CountyTownRoundedIntensity2=0;
                    }

                    // 对于烈度为1或0，拼接到CountyTownResult1
                    if (CountyTownRoundedIntensity2 == 1 || CountyTownRoundedIntensity2 == 0) {
                        if (CountyTownResult1.length() > 0) {
                            CountyTownResult1.append("、");
                        }
                        // 获取区县名称的前三个字符并拼接
                        CountyTownResult1.append(countyTown2.getCountyTownName().substring(0, Math.min(3, countyTown2.getCountyTownName().length())));
                        hasIntensity1Or0 = true;
                    }

                    // 对于烈度为2，拼接到CountyTownResult2
                    if (CountyTownRoundedIntensity2 == 2) {
                        if (CountyTownResult2.length() > 0) {
                            CountyTownResult2.append("、");
                        }
                        CountyTownResult2.append(countyTown2.getCountyTownName().substring(0, Math.min(3, countyTown2.getCountyTownName().length())));
                        hasIntensity2 = true;

                    }

                    // 对于烈度为3或4，拼接到CountyTownResult3
                    if (CountyTownRoundedIntensity2 == 3 || CountyTownRoundedIntensity2 == 4) {
                        if (CountyTownResult3.length() > 0) {
                            CountyTownResult3.append("、");
                        }
                        CountyTownResult3.append(countyTown2.getCountyTownName().substring(0, Math.min(3, countyTown2.getCountyTownName().length())));
                        hasIntensity3Or4 = true;
                    }
                    // 对于烈度为5，拼接到CountyTownResult4
                    if (CountyTownRoundedIntensity2 == 5) {
                        if (CountyTownResult4.length() > 0) {
                            CountyTownResult4.append("、");
                        }
                        CountyTownResult4.append(countyTown2.getCountyTownName().substring(0, Math.min(3, countyTown2.getCountyTownName().length())));
                        hasIntensity5 = true;
                    }

                    // 对于烈度为6到12，拼接到CountyTownResult5
                    if (CountyTownRoundedIntensity2 >= 6 && CountyTownRoundedIntensity2 <= 12) {
                        if (CountyTownResult5.length() > 0) {
                            CountyTownResult5.append("、");
                        }
                        CountyTownResult5.append(countyTown2.getCountyTownName().substring(0, Math.min(3, countyTown2.getCountyTownName().length())));
                        hasIntensity6To12 = true;
                    }
                }
            }
            //拼接文字
            if (hasIntensity1Or0) {
                if (CountyTownResult1.length() > 0 && !CountyTownResult1.toString().contains("特别敏感人有轻微震感")) {
                    CountyTownResult1.append("特别敏感人有轻微震感");
                }
            } else if (CountyTownResult1.length() == 0) {
                CountyTownResult1.append("无明显震感");
            }

            if (hasIntensity2) {
                if (CountyTownResult2.length() > 0 && !CountyTownResult2.toString().contains("轻微有震感")) {
                    CountyTownResult2.append("轻微有震感");
                }
            } else if (CountyTownResult2.length() == 0) {
                CountyTownResult2.append("无明显震感");
            }

            if (hasIntensity3Or4) {
                if (CountyTownResult3.length() > 0 && !CountyTownResult3.toString().contains("震感明显")) {
                    CountyTownResult3.append("震感明显");
                }
            } else if (CountyTownResult3.length() == 0) {
                CountyTownResult3.append("无明显震感");
            }

            if (hasIntensity5) {
                if (CountyTownResult4.length() > 0 && !CountyTownResult4.toString().contains("震感强烈")) {
                    CountyTownResult4.append("震感强烈");
                }
            } else if (CountyTownResult4.length() == 0) {
                CountyTownResult4.append("无明显震感");
            }

            if (hasIntensity6To12) {
                if (CountyTownResult5.length() > 0 && !CountyTownResult5.toString().contains("可能有房屋破坏和人员伤亡")) {
                    CountyTownResult5.append("可能有房屋破坏和人员伤亡");
                }
            } else if (CountyTownResult5.length() == 0) {
                CountyTownResult5.append("无明显震感");
            }

            // 输出最终结果
            System.out.println("CountyTownResult1: " + CountyTownResult1.toString());
            System.out.println("CountyTownResult2: " + CountyTownResult2.toString());
            System.out.println("CountyTownResult3: " + CountyTownResult3.toString());
            System.out.println("CountyTownResult4: " + CountyTownResult4.toString());
            System.out.println("CountyTownResult5: " + CountyTownResult5.toString());
            // 标志变量，用来判断是否已经添加了"我市"
            boolean isFirst = true;

            // 拼接到FinalCountyTown
            // 检查是否有任何结果非“无”或“无明显震感”
            if (!CountyTownResult1.toString().contains("无")) {
                if (isFirst) {
                    FinalCountyTown.append("我市");
                    isFirst = false; // 只在第一次拼接时添加"我市"
                }
                FinalCountyTown.append(CountyTownResult1).append("，");
            }

            if (!CountyTownResult2.toString().contains("无")) {
                if (isFirst) {
                    FinalCountyTown.append("我市");
                    isFirst = false;
                }
                FinalCountyTown.append(CountyTownResult2).append("，");
            }

            if (!CountyTownResult3.toString().contains("无")) {
                if (isFirst) {
                    FinalCountyTown.append("我市");
                    isFirst = false;
                }
                FinalCountyTown.append(CountyTownResult3).append("，");
            }

            if (!CountyTownResult4.toString().contains("无")) {
                if (isFirst) {
                    FinalCountyTown.append("我市");
                    isFirst = false;
                }
                FinalCountyTown.append(CountyTownResult4).append("，");
            }

            if (!CountyTownResult5.toString().contains("无")) {
                if (isFirst) {
                    FinalCountyTown.append("我市");
                    isFirst = false;
                }
                FinalCountyTown.append(CountyTownResult5).append("，");
            }
            boolean allNoFeeling=false;
            // 检查每个 CountyTownResultX，并拼接
            if (CountyTownResult1.toString().equals("无明显震感")&& CountyTownResult2.toString().equals("无明显震感")&& CountyTownResult3.toString().equals("无明显震感")&& CountyTownResult4.toString().equals("无明显震感")&& CountyTownResult5.toString().equals("无明显震感")) {
                allNoFeeling=true;
            }
            // 如果所有县镇结果都是 "无明显震感"，只保留 "我市无明显震感"
            if (allNoFeeling) {
                FinalCountyTown.setLength(0); // 清空原有拼接
                FinalCountyTown.append("我市无明显震感");
            } else {
                // 去掉最后的逗号
                if (FinalCountyTown.length() > 0 && FinalCountyTown.charAt(FinalCountyTown.length() - 1) == '，') {
                    FinalCountyTown.deleteCharAt(FinalCountyTown.length() - 1);
                }
            }
            // 输出最终结果
            System.out.println("FinalCountyTown: " + FinalCountyTown.toString());

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

                    if (VillageRoundedIntensity<0){
                        VillageRoundedIntensity=0;
                    }
                    System.out.println("乡镇:"+village.getVillagesName()+" 烈度： "+VillageRoundedIntensity+"  比较烈度：  "+roundedIntensity);

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
                finalResult.append("(主要涉及全境)。");
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
                finalResult.append("(无匹配乡镇或村庄)。");
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


            // 取整并返回结果
            String roundedIntensityResult = String.format("震中区最大地震烈度达%d度，", roundedIntensity);
            System.out.println(roundedIntensityResult);


            String roundedIntensityResult1 = String.format("我市最大地震烈度达%d度，", roundedIntensity1);
            // 输出结果
            System.out.println(roundedIntensityResult1);

            String roundedIntensityResult2 = String.format("我市乡（镇）政府、街道办驻地最大地震烈度为%d度", roundedIntensity2);
            System.out.println(roundedIntensityResult2);


            String villagesName = new String();

            // 判断 finalResult 是否包含特定内容
            if (finalResult.toString().contains("无匹配乡镇或村庄") || finalResult.toString().contains("主要涉及全境")) {
                // 如果包含 "无匹配乡镇或村庄" 或 "主要涉及全境"，直接打印无匹配乡镇或村庄
                System.out.println(finalResult.toString());
                villagesName = finalResult.toString();
            } else {
                // 如果包含 "、"，则执行以下逻辑
                villagesName = String.format("（主要位于%s一带）。", finalResult);
                System.out.println(villagesName);
            }

            String countyTown = String.format("初步分析，%s。", FinalCountyTown.toString());
            System.out.println(countyTown);

            String zhuResult1 = String.format("注：%s", zhuResult.toString());
            System.out.println(zhuResult1);
            String combinedResult2 = "初步估算，" + roundedIntensityResult + roundedIntensityResult1 + roundedIntensityResult2 + villagesName + countyTown;
            System.out.println(combinedResult2);
            if (eqAddr.contains("雅安市")) {
                WordExporter(combinedResult11, combinedResult2, formattedTime,table1,table2,eqMagnitude, eqAddr,zhuResult1);
            }else{
                WordExporter(combinedResult12, combinedResult2, formattedTime,table1,table2,eqMagnitude,eqAddr,zhuResult1);
            }
        }


    }
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

    @SneakyThrows
    private void WordExporter(String combinedResult1,String combinedResult2,String formattedTime, List<List<String>> tableData1, List<List<String>> tableData2, Double eqMagnitude, String eqAddr,String zhuResult) {
//        System.out.println("combinedResult1 的数据: " + combinedResult1);
//        System.out.println("formattedTime 的数据: " + formattedTime);
//        // 打印 tableData1 的数据
//        System.out.println("TableData1:");
//        for (List<String> row : tableData1) {
//            for (String cell : row) {
//                System.out.print(cell + "\t"); // 使用制表符分隔单元格内容
//            }
//            System.out.println(); // 换行，表示一行结束
//        }

//        // 打印 tableData2 的数据
//        System.out.println("TableData2:");
//        for (List<String> row : tableData2) {
//            for (String cell : row) {
//                System.out.print(cell + "\t"); // 使用制表符分隔单元格内容
//            }
//            System.out.println(); // 换行，表示一行结束
//        }


        // 创建一个 XWPFDocument 对象
        XWPFDocument document = new XWPFDocument();

        // 第一行：对内掌握，黑体三号，右对齐
        XWPFParagraph firstParagraph = document.createParagraph();
        firstParagraph.setAlignment(ParagraphAlignment.RIGHT); // 右对齐
        XWPFRun firstRun = firstParagraph.createRun();
        firstRun.setText("对内掌握");
        firstRun.setFontFamily("黑体");  // 黑体
        firstRun.setFontSize(16);  // 三号字体

        // 空3行
        for (int i = 0; i < 3; i++) {
            document.createParagraph();
        }

        // 第二行：地震应急辅助决策信息，居中，方正小标宋简体，44号，红色
        XWPFParagraph secondParagraph = document.createParagraph();
        secondParagraph.setAlignment(ParagraphAlignment.CENTER); // 居中
        XWPFRun secondRun = secondParagraph.createRun();
        secondRun.setText("地震应急辅助决策信息");
        secondRun.setFontFamily("方正小标宋简体");  // 方正小标宋简体
        secondRun.setFontSize(44);  // 44号字体
        secondRun.setColor("FF0000");  // 红色

        // 空1行
        document.createParagraph();

        // 第三行：雅安市应急管理局 + 10个空格 + formattedTime
        XWPFParagraph thirdParagraph = document.createParagraph();
        thirdParagraph.setAlignment(ParagraphAlignment.LEFT); // 左对齐
        XWPFRun thirdRun = thirdParagraph.createRun();
        //处理时间
        // 原始时间格式
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        // 目标时间格式
        SimpleDateFormat targetFormat = new SimpleDateFormat("MM月dd日HH时mm分");
        // 解析原始字符串为 Date 对象
        Date date = originalFormat.parse(formattedTime);
        // 格式化为目标格式并存入 currentFormattedTime
        String currentFormattedTime = targetFormat.format(date);
        thirdRun.setText("雅安市应急管理局" + "          " + currentFormattedTime);  // 10个空格 + formattedTime
        thirdRun.setFontFamily("仿宋_GB2312"); // 仿宋_GB2312
        thirdRun.setFontSize(16); // 三号字体

        // 在第三行下方添加一条红色的下划线（0.5磅宽）
        // 添加模拟红线的文本行（这里使用加粗的下划线文本模拟，但注意这不是真正的红线）
        // 如果需要真正的红线（如矩形框），则需要使用Apache POI的绘图功能
        XWPFParagraph redLineParagraph = document.createParagraph();
        // 设置段落居中对齐
        redLineParagraph.setAlignment(ParagraphAlignment.CENTER); // 这行代码在您的原始代码中已经存在，但为了完整性再次提及
        redLineParagraph.setSpacingAfter(0); // 段后0行距
        XWPFRun redLineRun = redLineParagraph.createRun();
        redLineRun.setText("________________________________________"); // 下划线，长度根据需要调整
        redLineRun.setFontFamily("华文行楷");  // 华文行楷字体
        redLineRun.setFontSize(22);  // 2号字体（调整为22以接近Word中的2号）
        redLineRun.setBold(true);  // 加粗
        redLineRun.setColor("FF0000");  // 红色
        redLineParagraph.setSpacingBetween(1.0);
        redLineParagraph.setSpacingBefore(0);
        redLineParagraph.setSpacingAfter(0);
        // 注意：这里的下划线是通过文本模拟的，如果需要真正的线条，请使用Apache POI的绘图功能

        // 空1行
        document.createParagraph();

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

        // 创建（辅助决策信息一）行
        XWPFParagraph Paragraph5 = document.createParagraph();
        fourthParagraph.setAlignment(ParagraphAlignment.CENTER); // 设置居中对齐
        // 创建运行并插入文本
        XWPFRun Run5 = fourthParagraph.createRun();
        // 设置文本内容
        Run5.setText("（辅助决策信息一）");
        Run5.setFontFamily("方正小标宋简体"); // 设置字体为仿宋_GB2312
        Run5.setFontSize(22); // 设置字体大小为二号（22号字体）

        // 创建第五行：一、震情情况
        XWPFParagraph fifthParagraph = document.createParagraph();
        fifthParagraph.setAlignment(ParagraphAlignment.LEFT); // 设置左对齐
        // 设置首行缩进2个字符
        fifthParagraph.setFirstLineIndent(560); // 2个字符大约是560twips（1字符 = 280twips）
        // 创建运行并插入文本
        XWPFRun fifthRun = fifthParagraph.createRun();
        // 设置文本内容
        fifthRun.setText("一、震情情况");
        fifthRun.setFontFamily("黑体"); // 设置字体为仿宋_GB2312
        fifthRun.setFontSize(16); // 设置字体大小为16号（即三号字体）

        // 第一部分剩下的内容：combinedResult1
        XWPFParagraph contentParagraph = document.createParagraph();
        contentParagraph.setAlignment(ParagraphAlignment.BOTH);
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
        sixRun.setFontFamily("黑体"); // 设置字体为仿宋_GB2312
        sixRun.setFontSize(16); // 设置字体大小为16号（即三号字体）

        // 第二部分剩下的内容：combinedResult2
        XWPFParagraph contentParagraph1 = document.createParagraph();
        contentParagraph1.setAlignment(ParagraphAlignment.BOTH);
        // 设置首行缩进2个字符
        contentParagraph1.setFirstLineIndent(560); // 2个字符大约是560twips（1字符 = 280twips）
        XWPFRun contentRun1 = contentParagraph1.createRun();  // 这里应该使用 contentParagraph1
        contentRun1.setText(combinedResult2);
        contentRun1.setFontFamily("仿宋_GB2312");
        contentRun1.setFontSize(16);  // 四号字体


        // 灾情评估注释部分：
        XWPFParagraph zhuResult1 = document.createParagraph();
        zhuResult1.setAlignment(ParagraphAlignment.BOTH);
        // 设置首行缩进2个字符
        zhuResult1.setFirstLineIndent(560); // 2个字符大约是560twips（1字符 = 280twips）
        XWPFRun zhuResultRun = zhuResult1.createRun();
        zhuResultRun.setText(zhuResult);
        zhuResultRun.setFontFamily("仿宋_GB2312");
        zhuResultRun.setFontSize(12);  // 小四字体

        // 空1行
        document.createParagraph();

        // 附件部分
        String fuJian1 = "附件：1.市、县（区）地震烈度估算表";
        String fuJian2 = "2.乡镇（街道）地震烈度估算表";
        String fuJian3 = "雅安市应急管理局值班电话：0835-2220001，卫星电话：17406544731。";
        String fuJian4 = "（本期送：市政府领导、局领导、局机关各科室。）";
        String fuJian5 = "附件1";
        String fuJian6 = "市、县（区）地震烈度估算表";
        String fuJian7 = "附件2";
        String fuJian8 = "乡镇（街道）地震烈度估算表";
        // 表格下说明内容
        String tableDescription = "说明：表中“震中距”为震中到市政府驻地、县（区）政府驻地直线距离；“预估烈度”为烈度衰减数学模型计算结果，以上烈度值为市政府驻地、县（区）政府驻地预估地震烈度，仅供参考；“一般建筑设防烈度”为一般建设工程基本抗震设防烈度，数据摘自GB18306-2015。";

        XWPFParagraph fuJianContent1 = document.createParagraph();
        fuJianContent1.setAlignment(ParagraphAlignment.BOTH);
        // 设置首行缩进2个字符
        fuJianContent1.setFirstLineIndent(560); // 2个字符大约是560twips（1字符 = 280twips）
        XWPFRun fuJianRun1 = fuJianContent1.createRun();
        fuJianRun1.setText(fuJian1);
        fuJianRun1.setFontFamily("仿宋_GB2312");
        fuJianRun1.setFontSize(16);  // 三号字体

        XWPFParagraph fuJianContent2 = document.createParagraph();
        fuJianContent2.setAlignment(ParagraphAlignment.BOTH);
        // 设置首行缩进2个字符
        fuJianContent2.setFirstLineIndent(1400); // 2个字符大约是560twips（1字符 = 280twips）
        XWPFRun fuJianRun2 = fuJianContent2.createRun();
        fuJianRun2.setText(fuJian2);
        fuJianRun2.setFontFamily("仿宋_GB2312");
        fuJianRun2.setFontSize(16);  // 三号字体

        // 空1行
        document.createParagraph();

        XWPFParagraph fuJianContent3 = document.createParagraph();
        fuJianContent3.setAlignment(ParagraphAlignment.BOTH);
        // 设置首行缩进2个字符
        fuJianContent3.setFirstLineIndent(560); // 2个字符大约是560twips（1字符 = 280twips）
        XWPFRun fuJianRun3 = fuJianContent3.createRun();
        fuJianRun3.setText(fuJian3);
        fuJianRun3.setFontFamily("仿宋_GB2312");
        fuJianRun3.setFontSize(16);  // 三号字体

        // 空1行
        document.createParagraph();

        XWPFParagraph fuJianContent4 = document.createParagraph();
        fuJianContent4.setAlignment(ParagraphAlignment.BOTH);
        // 设置首行缩进2个字符
        fuJianContent4.setFirstLineIndent(560); // 2个字符大约是560twips（1字符 = 280twips）
        XWPFRun fuJianRun4 = fuJianContent4.createRun();
        fuJianRun4.setText(fuJian4);
        fuJianRun4.setFontFamily("仿宋_GB2312");
        fuJianRun4.setFontSize(16);  // 三号字体

        // 添加分页符
        fuJianContent4 = document.createParagraph();
        fuJianRun4 = fuJianContent4.createRun();
        fuJianRun4.addBreak(BreakType.PAGE);  // 添加分页符

        XWPFParagraph fuJianContent5 = document.createParagraph();
        fuJianContent5.setAlignment(ParagraphAlignment.BOTH);
        XWPFRun fuJianRun5 = fuJianContent5.createRun();
        fuJianRun5.setText(fuJian5);
        fuJianRun5.setFontFamily("黑体");
        fuJianRun5.setFontSize(16);  // 三号字体

        XWPFParagraph fuJianContent6 = document.createParagraph();
        fuJianContent6.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun fuJianRun6 = fuJianContent6.createRun();
        fuJianRun6.setText(fuJian6);
        fuJianRun6.setFontFamily("方正小标宋简体");
        fuJianRun6.setFontSize(20);  // 三号字体


        //  在文档中添加表格一
        if (tableData1 != null && !tableData1.isEmpty()) {
            // 创建一个段落来包含表格，设置段落对齐方式为居中
            XWPFParagraph tableParagraph = document.createParagraph();
            tableParagraph.setAlignment(ParagraphAlignment.CENTER);
            tableParagraph.setSpacingBefore(0); // 设置表格段落之前的间距为0
            tableParagraph.setSpacingAfter(0);  // 设置表格段落之后的间距为0

            // 根据 tableData1 的行数和列数创建表格
            XWPFTable table = document.createTable(tableData1.size(), tableData1.get(0).size());

            table.setTableAlignment(TableRowAlign.CENTER);

            table.setCellMargins(0, 108, 0, 108);  // 上边距0, 左右边距约0.19cm, 下边距0


            // 遍历表格的每一行（从表头开始）
            for (int i = 0; i < tableData1.size(); i++) {
                XWPFTableRow row = table.getRow(i);  // 获取当前行
                List<String> rowData = tableData1.get(i);  // 获取当前行的数据

                // 遍历当前行的每一个单元格
                for (int j = 0; j < rowData.size(); j++) {
                    XWPFTableCell cell = row.getCell(j);  // 获取当前单元格

                    // 获取单元格的第一个段落，如果没有则创建一个新的段落
                    XWPFParagraph paragraph = cell.getParagraphArray(0);
                    if (paragraph == null) {
                        paragraph = cell.addParagraph();  // 如果没有段落，则添加一个新段落
                    }

                    // 设置段落的对齐方式
                    paragraph.setAlignment(ParagraphAlignment.CENTER);  // 水平居中
                    paragraph.setVerticalAlignment(TextAlignment.CENTER); // 垂直居中

                    // 设置段落的行间距、段前段后间距
                    paragraph.setSpacingBetween(1.0);  // 单倍行距
                    paragraph.setSpacingBefore(0);     // 段前间距为0
                    paragraph.setSpacingAfter(0);      // 段后间距为0

                    // 强制设置单元格的字体样式
                    XWPFRun runInsideTable = paragraph.createRun();
                    runInsideTable.setFontFamily("仿宋_GB2312"); // 设置字体
                    runInsideTable.setFontSize(13);              // 设置字体大小为13
                    if (i == 0) {
                        runInsideTable.setBold(true);  // 如果是第一行，设置为粗体（表头）
                    } else {
                        runInsideTable.setBold(false);  // 非表头行，取消粗体
                    }

                    // 填充单元格的文本内容
                    runInsideTable.setText(rowData.get(j));
                }
            }
        }

        // 空1行
        document.createParagraph();

        // 插入表格下方的说明内容
        XWPFParagraph descriptionParagraph = document.createParagraph();
        XWPFRun run1 = descriptionParagraph.createRun();
        run1.setText(tableDescription);
        run1.setFontSize(12);
        run1.setFontFamily("仿宋_GB2312");
        descriptionParagraph.setFirstLineIndent(560); // 2个字符大约是560twips（1字符 = 280twips）

        // 添加分页符
        descriptionParagraph = document.createParagraph();
        run1 = descriptionParagraph.createRun();
        run1.addBreak(BreakType.PAGE);  // 添加分页符

        XWPFParagraph fuJianContent7 = document.createParagraph();
        fuJianContent7.setAlignment(ParagraphAlignment.BOTH);
        XWPFRun fuJianRun7 = fuJianContent7.createRun();
        fuJianRun7.setText(fuJian7);
        fuJianRun7.setFontFamily("黑体");
        fuJianRun7.setFontSize(16);  // 三号字体

        XWPFParagraph fuJianContent8 = document.createParagraph();
        fuJianContent8.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun fuJianRun8 = fuJianContent8.createRun();
        fuJianRun8.setText(fuJian8);
        fuJianRun8.setFontFamily("方正小标宋简体");
        fuJianRun8.setFontSize(20);  // 三号字体

        // 在文档中添加表格二
        if (tableData2 != null && !tableData2.isEmpty()) {

            // 创建一个段落来包含表格，设置段落对齐方式为居中
            XWPFParagraph tableParagraph = document.createParagraph();
            tableParagraph.setAlignment(ParagraphAlignment.CENTER);

            // 根据 tableData1 的行数和列数创建表格
            XWPFTable table = document.createTable(tableData2.size(), tableData2.get(0).size());

            table.setTableAlignment(TableRowAlign.CENTER);

            table.setCellMargins(0, 108, 0, 108);  // 上边距0, 左右边距约0.19cm, 下边距0


            // 遍历表格的每一行（从表头开始）
            for (int i = 0; i < tableData2.size(); i++) {
                XWPFTableRow row = table.getRow(i);  // 获取当前行
                List<String> rowData = tableData2.get(i);  // 获取当前行的数据

                // 遍历当前行的每一个单元格
                for (int j = 0; j < rowData.size(); j++) {
                    XWPFTableCell cell = row.getCell(j);  // 获取当前单元格

                    // 获取单元格的第一个段落，如果没有则创建一个新的段落
                    XWPFParagraph paragraph = cell.getParagraphArray(0);
                    if (paragraph == null) {
                        paragraph = cell.addParagraph();  // 如果没有段落，则添加一个新段落
                    }

                    // 设置段落的对齐方式
                    paragraph.setAlignment(ParagraphAlignment.CENTER);  // 水平居中
                    paragraph.setVerticalAlignment(TextAlignment.CENTER); // 垂直居中

                    // 设置段落的行间距、段前段后间距
                    paragraph.setSpacingBetween(1.0);  // 单倍行距
                    paragraph.setSpacingBefore(0);     // 段前间距为0
                    paragraph.setSpacingAfter(0);      // 段后间距为0

                    // 强制设置单元格的字体样式
                    XWPFRun runInsideTable = paragraph.createRun();
                    runInsideTable.setFontFamily("仿宋_GB2312"); // 设置字体
                    runInsideTable.setFontSize(13);              // 设置字体大小为13
                    if (i == 0) {
                        runInsideTable.setBold(true);  // 如果是第一行，设置为粗体（表头）
                    } else {
                        runInsideTable.setBold(false);  // 非表头行，取消粗体
                    }

                    // 填充单元格的文本内容
                    runInsideTable.setText(rowData.get(j));
                }
            }
        }

        // 空1行
        document.createParagraph();

        // 插入表格下方的说明内容
        XWPFParagraph descriptionParagraph1 = document.createParagraph();
        XWPFRun run2 = descriptionParagraph1.createRun();
        run2.setText(tableDescription);
        run2.setFontSize(12);
        run2.setFontFamily("仿宋_GB2312");
        descriptionParagraph1.setFirstLineIndent(560); // 2个字符大约是560twips（1字符 = 280twips）

        // 构造文件路径
        String fileName = formattedTime + "级地震（辅助决策信息一）.docx";
//        String filePath = "C:/Users/Smile/Desktop/" + fileName;
//        String filePath = "D:/桌面夹/桌面/demo/" + fileName;
        String filePath = "/data/image"+ fileName;
        // 设置页面边距
        setPageMargins(document, filePath);
        // 获取所有段落并设置格式
        for (XWPFParagraph paragraph1 : document.getParagraphs()) {
            paragraph1.setSpacingBetween(1.5);  // 设置单倍行距
            paragraph1.setSpacingBefore(0);  // 设置段前间距为0
            paragraph1.setSpacingAfter(0);   // 设置段后间距为0
        }

        // 写入文件
        try (FileOutputStream out = new FileOutputStream(filePath)){
            document.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setPageMargins(XWPFDocument document, String filePath) throws IOException {
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
}
