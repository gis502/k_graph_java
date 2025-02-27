package com.ruoyi.web.api.service;


import com.ruoyi.common.constant.Constants;
import com.ruoyi.system.domain.dto.EqEventTriggerDTO;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.mapper.*;
import lombok.SneakyThrows;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: xiaodemos
 * @date: 2024-1-18 16:45
 * @description: 地震应急辅助决策信息二
 */
@Service
public class SismiceMergencyAssistanceService {
    @Resource
    private YaanResidentPopulationDensityMapper yaanResidentPopulationDensityMapper;

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
    private YaanVillageAndCommunityMapper yaanVillageAndCommunityMapper;

    @Resource
    private AssessmentResultMapper assessmentResultMapper;

    @Resource
    private EarthquakeListMapper earthquakeListMapper;

    // 函数计算公式
    // 计算 QX 的函数
    public static double calculateQX(double latitude1, double longitude1, double latitude2, double longitude2) {
        //latitude1,longitude1,为各A，B，C，D 控制点的经纬度、
        //latitude2，longitude2为新建地震的经纬度
        // 将角度转换为弧度
        double lat1Rad = Math.toRadians(90 - latitude1);
        double lon1Rad = Math.toRadians(longitude1);
        double lat2Rad = Math.toRadians(90 - latitude2);
        double lon2Rad = Math.toRadians(longitude2);

        // 计算球面距离公式
        double part1 = Math.sin(lat1Rad) * Math.cos(lon1Rad) - Math.sin(lat2Rad) * Math.cos(lon2Rad);
        double part2 = Math.sin(lat1Rad) * Math.sin(lon1Rad) - Math.sin(lat2Rad) * Math.sin(lon2Rad);
        double part3 = Math.cos(lat1Rad) - Math.cos(lat2Rad);

        double distance = (6371004 * Math.acos(1 - (Math.pow(part1, 2) + Math.pow(part2, 2) + Math.pow(part3, 2)) / 2)) / 1000; // 返回千米

        return distance;
    }

    // 计算公式 (D5 + D6 + C10) / 2
    public static double calculateQAB(double QA, double QB, double AB) {
        // 计算并返回结果
        return (QA + QB + AB) / 2;
    }

    // 计算公式 (D6 + D7 + C11) / 2
    public static double calculateQBC(double QB, double QC, double BC) {
        // 计算并返回结果
        return (QB + QC + BC) / 2;
    }

    // 计算公式 (D7 + D8 + C12) / 2
    public static double calculateQCD(double QC, double QD, double CD) {
        // 计算并返回结果
        return (QC + QD + CD) / 2;
    }

    // 计算公式 (D5 + D8 + C13) / 2
    public static double calculateQDA(double QA, double QD, double DA) {
        // 计算并返回结果
        return (QA + QD + DA) / 2;
    }

    public static double calculateArea(double QAB, double QBC, double QCD, double QAD,
                                       double QA, double QB, double QC, double QD,
                                       double AB, double BC, double CD, double DA) {
        double part1 = Math.sqrt(QAB * (QAB - QA) * (QAB - QB) * (QAB - AB));
        double part2 = Math.sqrt(QBC * (QBC - QB) * (QBC - QC) * (QBC - BC));
        double part3 = Math.sqrt(QCD * (QCD - QC) * (QCD - QD) * (QCD - CD));
        double part4 = Math.sqrt(QAD * (QAD - QA) * (QAD - QD) * (QAD - DA));

        // 返回最终的计算结果
        return part1 + part2 + part3 + part4;
    }


    //开始据条件判断输出语句
    @SneakyThrows
    public void file(EqEventTriggerDTO params, String eqqueueId) {
        System.out.println("前端构建文本传的参数" + params);
        String event = params.getEvent();  //深度   N3
        String eqName = params.getEqName();  //名字
        String eqTime = params.getEqTime();  //时间
        String eqAddr = params.getEqAddr();  //地址
        Double latitude = params.getLatitude();  //纬度  G3
        Double longitude = params.getLongitude();  //经度  H3
        Double eqMagnitude = params.getEqMagnitude();  //震级  I3
        Double eqDepth = params.getEqDepth();  //深度   N3

        //***********************************************************************************************

        //---------文档标题----------
        eqName = eqName.replace("undefined", "");

        String title = eqName + "发生" + eqMagnitude + "级地震";


        //***********************************************************************************************


        // Lushan 地区
        double QA_Lushan = calculateQX(30.521, 102.913, latitude, longitude);
        double QB_Lushan = calculateQX(30.444, 103.192, latitude, longitude);
        double QC_Lushan = calculateQX(30.054, 102.998, latitude, longitude);
        double QD_Lushan = calculateQX(30.162, 102.695, latitude, longitude);

        // Wenchuan 地区
        double QA_Wenchuan = calculateQX(33.113, 105.338, latitude, longitude);
        double QB_Wenchuan = calculateQX(32.678, 106.011, latitude, longitude);
        double QC_Wenchuan = calculateQX(30.543, 103.560, latitude, longitude);
        double QD_Wenchuan = calculateQX(31.053, 102.779, latitude, longitude);

        // Jiuzhaigou 地区
        double QA_Jiuzhaigou = calculateQX(33.468, 103.591, latitude, longitude);
        double QB_Jiuzhaigou = calculateQX(33.53, 103.858, latitude, longitude);
        double QC_Jiuzhaigou = calculateQX(32.984, 104.106, latitude, longitude);
        double QD_Jiuzhaigou = calculateQX(32.893, 103.875, latitude, longitude);

        // Changning 地区
        double QA_Changning = calculateQX(28.4445, 104.6478, latitude, longitude);
        double QB_Changning = calculateQX(28.5644, 104.7633, latitude, longitude);
        double QC_Changning = calculateQX(28.3907, 105.0221, latitude, longitude);
        double QD_Changning = calculateQX(28.2705, 104.9237, latitude, longitude);

        // Luding 地区
        double QA_Luding = calculateQX(29.7232, 101.9167, latitude, longitude);
        double QB_Luding = calculateQX(29.7417, 102.1447, latitude, longitude);
        double QC_Luding = calculateQX(29.3869, 102.3095, latitude, longitude);
        double QD_Luding = calculateQX(29.3032, 102.0973, latitude, longitude);

        // 打印结果
        System.out.println("Lushan QA: " + QA_Lushan + ", QB: " + QB_Lushan + ", QC: " + QC_Lushan + ", QD: " + QD_Lushan);
        System.out.println("Wenchuan QA: " + QA_Wenchuan + ", QB: " + QB_Wenchuan + ", QC: " + QC_Wenchuan + ", QD: " + QD_Wenchuan);
        System.out.println("Jiuzhaigou QA: " + QA_Jiuzhaigou + ", QB: " + QB_Jiuzhaigou + ", QC: " + QC_Jiuzhaigou + ", QD: " + QD_Jiuzhaigou);
        System.out.println("Changning QA: " + QA_Changning + ", QB: " + QB_Changning + ", QC: " + QC_Changning + ", QD: " + QD_Changning);
        System.out.println("Luding QA: " + QA_Luding + ", QB: " + QB_Luding + ", QC: " + QC_Luding + ", QD: " + QD_Luding);


        // 计算各地区的 QAB, QBC, QCD, QAD
        double QAB_Lushan = calculateQAB(QA_Lushan, QB_Lushan, 28.0730);
        double QBC_Lushan = calculateQBC(QB_Lushan, QC_Lushan, 47.2002);
        double QCD_Lushan = calculateQCD(QC_Lushan, QD_Lushan, 31.5235);
        double QAD_Lushan = calculateQDA(QA_Lushan, QD_Lushan, 45.0686);

        double QAB_Wenchuan = calculateQAB(QA_Wenchuan, QB_Wenchuan, 79.2962);
        double QBC_Wenchuan = calculateQBC(QB_Wenchuan, QC_Wenchuan, 331.9871);
        double QCD_Wenchuan = calculateQCD(QC_Wenchuan, QD_Wenchuan, 93.7043);
        double QAD_Wenchuan = calculateQDA(QA_Wenchuan, QD_Wenchuan, 332.5328);

        double QAB_Jiuzhaigou = calculateQAB(QA_Jiuzhaigou, QB_Jiuzhaigou, 25.6995);
        double QBC_Jiuzhaigou = calculateQBC(QB_Jiuzhaigou, QC_Jiuzhaigou, 64.9442);
        double QCD_Jiuzhaigou = calculateQCD(QC_Jiuzhaigou, QD_Jiuzhaigou, 23.8138);
        double QAD_Jiuzhaigou = calculateQDA(QA_Jiuzhaigou, QD_Jiuzhaigou, 69.1846);

        double QAB_Changning = calculateQAB(QA_Changning, QB_Changning, 17.4679);
        double QBC_Changning = calculateQBC(QB_Changning, QC_Changning, 31.8262);
        double QCD_Changning = calculateQCD(QC_Changning, QD_Changning, 16.4741);
        double QAD_Changning = calculateQDA(QA_Changning, QD_Changning, 33.2144);

        double QAB_Luding = calculateQAB(QA_Luding, QB_Luding, 22.1107);
        double QBC_Luding = calculateQBC(QB_Luding, QC_Luding, 42.5501);
        double QCD_Luding = calculateQCD(QC_Luding, QD_Luding, 22.5756);
        double QAD_Luding = calculateQDA(QA_Luding, QD_Luding, 49.8646);

        // 打印各地区的计算结果
        System.out.println("Lushan - QAB: " + QAB_Lushan + ", QBC: " + QBC_Lushan + ", QCD: " + QCD_Lushan + ", QAD: " + QAD_Lushan);
        System.out.println("Wenchuan - QAB: " + QAB_Wenchuan + ", QBC: " + QBC_Wenchuan + ", QCD: " + QCD_Wenchuan + ", QAD: " + QAD_Wenchuan);
        System.out.println("Jiuzhaigou - QAB: " + QAB_Jiuzhaigou + ", QBC: " + QBC_Jiuzhaigou + ", QCD: " + QCD_Jiuzhaigou + ", QAD: " + QAD_Jiuzhaigou);
        System.out.println("Changning - QAB: " + QAB_Changning + ", QBC: " + QBC_Changning + ", QCD: " + QCD_Changning + ", QAD: " + QAD_Changning);
        System.out.println("Luding - QAB: " + QAB_Luding + ", QBC: " + QBC_Luding + ", QCD: " + QCD_Luding + ", QAD: " + QAD_Luding);


        // 调用函数计算各地区的区域面积并打印
        double lushanArea = calculateArea(QAB_Lushan, QBC_Lushan, QCD_Lushan, QAD_Lushan, QA_Lushan, QB_Lushan, QC_Lushan, QD_Lushan, 28.0730, 47.2002, 31.5235, 45.0686);
        System.out.println("Lushan Area 计算结果: " + lushanArea);

        double wenchuanArea = calculateArea(QAB_Wenchuan, QBC_Wenchuan, QCD_Wenchuan, QAD_Wenchuan, QA_Wenchuan, QB_Wenchuan, QC_Wenchuan, QD_Wenchuan, 79.2962, 331.9871, 93.7043, 332.5328);
        System.out.println("Wenchuan Area 计算结果: " + wenchuanArea);

        double jiuzhaigouArea = calculateArea(QAB_Jiuzhaigou, QBC_Jiuzhaigou, QCD_Jiuzhaigou, QAD_Jiuzhaigou, QA_Jiuzhaigou, QB_Jiuzhaigou, QC_Jiuzhaigou, QD_Jiuzhaigou, 25.6995, 64.9442, 23.8138, 69.1846);
        System.out.println("Jiuzhaigou Area 计算结果: " + jiuzhaigouArea);

        double changningArea = calculateArea(QAB_Changning, QBC_Changning, QCD_Changning, QAD_Changning, QA_Changning, QB_Changning, QC_Changning, QD_Changning, 17.4679, 31.8262, 16.4741, 33.2144);
        System.out.println("Changning Area 计算结果: " + changningArea);

        double ludingArea = calculateArea(QAB_Luding, QBC_Luding, QCD_Luding, QAD_Luding, QA_Luding, QB_Luding, QC_Luding, QD_Luding, 22.1107, 42.5501, 22.5756, 49.8646);
        System.out.println("Luding Area 计算结果: " + ludingArea);


        // 示例输入
        String lushan; // 假设数据来自外部输入
        String wenchuan;
        String jiuzhaigou;
        String changning;
        String luding;


        if (lushanArea <= 1370) {
            lushan = "是";
        } else {
            lushan = "否";
        }
        if (wenchuanArea <= 28460) {
            wenchuan = "是";
        } else {
            wenchuan = "否";
        }
        if (jiuzhaigouArea <= 1654) {
            jiuzhaigou = "是";
        } else {
            jiuzhaigou = "否";
        }
        if (changningArea <= 552) {
            changning = "是";
        } else {
            changning = "否";
        }
        if (ludingArea <= 1015) {
            luding = "是";
        } else {
            luding = "否";
        }


        // 打印这5个值
        System.out.println("lushan: " + lushan);
        System.out.println("wenchuan: " + wenchuan);
        System.out.println("jiuzhaigou: " + jiuzhaigou);
        System.out.println("changning: " + changning);
        System.out.println("luding: " + luding);

        // 变量 to store the conclusion
        String aftershockConclusion = "";

        // 判断并赋值给 aftershockConclusion
        if ("是".equals(lushan)) {
            aftershockConclusion = "所在位置位于2013年“4·20”芦山7.0级地震余震活动区范围";
        } else if ("是".equals(wenchuan)) {
            aftershockConclusion = "所在位置位于2008年“5·12”汶川8.0级地震余震活动区范围";
        } else if ("是".equals(jiuzhaigou)) {
            aftershockConclusion = "所在位置位于2017年“8·8”九寨沟7.0级地震余震活动区范围";
        } else if ("是".equals(changning)) {
            aftershockConclusion = "所在位置位于2019年“6·17”长宁6.0级地震余震活动区范围";
        } else if ("是".equals(luding)) {
            aftershockConclusion = "所在位置位于2022年“9·5”泸定6.8级地震余震活动区范围";
        } else {
            aftershockConclusion = "不在近年有影响的地震余震活动区范围";
        }

        // 输出结果
        System.out.println("余震结论判断：" + aftershockConclusion);


        //***********************************************************************************************


        //---------时间------------
        // 创建 SimpleDateFormat 对象，用于解析原始时间字符串
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 将输入的时间字符串解析为 Date 对象
        Date parsedDate = inputFormat.parse(eqTime);

        // 1.创建 SimpleDateFormat 对象，用于格式化为目标格式  MM月dd日HH时mm分
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM月dd日HH时mm分");

        // 格式化 Date 对象为目标格式的字符串
        String formattedTime = outputFormat.format(parsedDate);

        // 2. 使用 Calendar 获取 年、月、日
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parsedDate);

        int year = calendar.get(Calendar.YEAR);   //B3
        int month = calendar.get(Calendar.MONTH) + 1;     //C3    // Calendar 的月份从 0 开始，所以 +1
        int day = calendar.get(Calendar.DAY_OF_MONTH);  //D3
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        System.out.println("年: " + year);
        System.out.println("月: " + month);
        System.out.println("日: " + day);

        String monthDay = month + " • " + day; // C3•D3
        System.out.println(monthDay);


        // --------遍历查询雅安市人口密度----------------
        List<YaanResidentPopulationDensity> density = yaanResidentPopulationDensityMapper.selectList(null);

        // 用于存储匹配到的人口密度
        String populationDensity = "没有该地人口密度信息";
        String countyOrDistrict = "县/区";
        String cityOrState = "市/州";

        // 遍历查询匹配的 cityAndCounty 字段
        for (YaanResidentPopulationDensity populationData : density) {
            if (eqName != null && eqName.contains(populationData.getCityAndCounty())) {
                populationDensity = populationData.getPopulationDensity();
                countyOrDistrict = populationData.getCountyOrDistrict();
                cityOrState = populationData.getCityOrState();
                break; // 找到匹配项后，停止遍历
            }
        }


        System.out.println("人口密度：" + populationDensity);   //  G26
        System.out.println("县/区：" + countyOrDistrict);  //  县/区  输出：泸定县  L3
        System.out.println("市/州：" + cityOrState);  //  市/州 输出：甘孜州  K3


        String newCountyOrDistrict = " ";
        newCountyOrDistrict = countyOrDistrict.replace("区", "").replace("县", "");
        System.out.println("去除县/区后的县/区字段：" + newCountyOrDistrict);  // 输出：泸定

        //--------地区-------
        // 地区
        String position;
        if (eqName.contains("雅安")) { // 如果 eqName 包含“雅安”
            if (latitude <= 29.57) {
                position = "雅安南部";
            } else {
                position = "雅安中北部";
            }
        } else if (eqName.contains("甘孜")) { // 如果 eqName 包含“甘孜”
            if (latitude <= 31) {
                position = "甘孜中南部";
            } else {
                position = "甘孜北部";
            }
        } else {
            // 默认返回 eqName 本身
            position = eqName;
        }
        System.out.println("地区：" + position);


        String geography;


        if (position.contains("阿坝") || position.contains("甘孜北部") || position.contains("雅安中北部")) {
            geography = "大部分属于川西北丘状高原山地区";
        } else if (position.contains("甘孜中南部")) {
            geography = "大部分属于川西高山高原区";
        } else if (position.contains("凉山") || position.contains("攀枝花") || position.contains("乐山") || position.contains("雅安南部")) {
            geography = "大部分属于川西南中高山地区";
        } else if (position.contains("成都") || position.contains("德阳") || position.contains("眉山")) {
            geography = "大部分属于成都平原区";
        } else if (position.contains("广元") || position.contains("绵阳") || position.contains("南充") || position.contains("遂宁") ||
                position.contains("资阳") || position.contains("内江") || position.contains("自贡") ||
                position.contains("宜宾") || position.contains("泸州")) {
            geography = "大部分属于盆中丘陵区";
        } else if (position.contains("巴中")) {
            geography = "大部分属于米仓山大巴山中山区";
        } else if (position.contains("达州") || position.contains("广安")) {
            geography = "大部分属于盆东平行岭谷区";
        } else {
            geography = "不在判断区范围";
        }


        System.out.println("地理：" + geography);


        // 查询雅安市所有乡镇
        List<YaanVillages> villageList0 = yaanVillagesMapper.selectList(null); //????不太确定YaanVillages表名，数据库没开

        // 用 Map 存储所有乡镇名称及其对应的震中距
        Map<String, Double> villageDistances0 = new HashMap<>();

        // 遍历所有乡镇，计算与上传经纬度的距离
        for (YaanVillages village : villageList0) {
            Geometry geom = village.getGeom();
            if (geom != null && geom instanceof Point) {
                // 获取乡镇的经纬度
                Point villagePoint = (Point) geom;
                double villageLat = villagePoint.getY(); // 纬度
                double villageLon = villagePoint.getX(); // 经度

                // 计算震中到乡镇点的距离（单位：米）
                double distanceToVillage = calculateDistance(latitude, longitude, villageLat, villageLon);

                // 存储乡镇名称及其震中距
                villageDistances0.put(village.getVillagesName(), distanceToVillage);
            }
        }

        // **对乡镇按照震中距从小到大排序，并取前 8 个**
        List<Map.Entry<String, Double>> sortedVillages0 = new ArrayList<>(villageDistances0.entrySet());
        sortedVillages0.sort(Map.Entry.comparingByValue()); // 按震中距升序排序

        // 取前 8 个乡镇
        int limit0 = Math.min(8, sortedVillages0.size()); // 防止乡镇数量不足 8 个
        List<Map.Entry<String, Double>> topVillages = sortedVillages0.subList(0, limit0);

        // **输出前 8 个乡镇及其震中距**
        System.out.println("最近的 8 个乡镇及其震中距：");
        for (Map.Entry<String, Double> entry : topVillages) {
            System.out.println("乡镇名称: " + entry.getKey() + "，震中距: " + entry.getValue() + " 米");
        }


        // 存储乡镇名称、震中距和计算后的烈度
        Map<String, Double> intensities0 = new LinkedHashMap<>();

        // 遍历计算每个乡镇的烈度
        for (Map.Entry<String, Double> entry : villageDistances0.entrySet()) {
            String townName = entry.getKey();  // 乡镇名称
            double distance = entry.getValue(); // 震中距（D）

            // 计算 X 的值
            double X = (eqMagnitude > 5.5) ? 0.4 : 0.6;

            // 计算烈度 I
            double intensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(distance + 24)) - X;

            // 确保烈度不小于 0
            intensity = Math.max(intensity, 0);

            // 将烈度值四舍五入为整数
            intensity = Math.round(intensity);

            // 存入 Map
            intensities0.put(townName, intensity);
        }

        // **输出每个乡镇的烈度**
        for (Map.Entry<String, Double> entry : intensities0.entrySet()) {
            System.out.println("乡镇名称: " + entry.getKey() + "，震中距: " + villageDistances0.get(entry.getKey()) + " km，烈度: " + String.format("%.2f", entry.getValue()));
        }

        // **将 Map 转换成 List 并排序**
        List<Map.Entry<String, Double>> sortedList0 = new ArrayList<>(intensities0.entrySet());
        sortedList0.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue())); // 按烈度降序排序

        // **输出排序后的乡镇烈度信息**
        System.out.println("乡镇名称 | 烈度");
        System.out.println("--------------------");
        for (Map.Entry<String, Double> entry : sortedList0) {
            System.out.printf("%s | %.2f%n", entry.getKey(), entry.getValue());
        }


        //---------------涉及我市行政村（社区）xxx万户，常住人口约xx万人。------------------------------------------


//        List<YaanVillageAndCommunity> quantity = yaanVillageAndCommunityMapper.selectList(null);
//
//
//        Map<String, Double> villageAndCommunityDistances = new LinkedHashMap<>();
//
//        // 获取所有村（社区）的户数（风普）和常住人口（风普），并一一对应存储
//        List<Map<String, Object>> data = new ArrayList<>();
//        for (YaanVillageAndCommunity villageAndCommunity : quantity) {
//
//
//            Geometry geom = villageAndCommunity.getGeom();
//            if (geom != null && geom instanceof Point) {
//                // 获取政府县区的经纬度
//                Point villageAndCommunityPoint = (Point) geom;
//                double villageAndCommunityLat = villageAndCommunityPoint.getY(); // 纬度
//                double villageAndCommunityLon = villageAndCommunityPoint.getX(); // 经度
//
//                // 计算震中到县区点的距离（单位：米）
//                double distanceToCountyTown = calculateDistance(latitude, longitude, villageAndCommunityLat, villageAndCommunityLon);
//
//                // 存储县区名称及其震中距
//                villageAndCommunityDistances.put(villageAndCommunity.getVillageOrCommunity(), distanceToCountyTown);
//            }
//
//
//            Map<String, Object> dataMap = new HashMap<>();
//            dataMap.put("villageOrCommunity", villageAndCommunity.getVillageOrCommunity()); // 村（社区）名称
//            dataMap.put("numberOfHouseholdsOrFengpu", villageAndCommunity.getNumberOfHouseholdsOrFengpu()); // 户数
//            dataMap.put("residentPopulationFengpu", villageAndCommunity.getResidentPopulationFengpu()); // 常住人口
//            data.add(dataMap);
//        }
//
//        System.out.println("打印所有村（社区）的户数（风普）和常住人口（风普）前7个数据:" );
//        // 打印前7个数据（如果数据不足7个，则打印全部）
//        int seven = Math.min(7, data.size()); // 确保不会超出索引范围
//        for (int i = 0; i < seven; i++) {
//            System.out.println("第"+i+"个："+data.get(i));
//        }
//
//        // 存储乡镇名称、震中距和计算后的烈度
//        Map<String, Double> all = new LinkedHashMap<>();
//
//        // 遍历计算每个乡镇的烈度
//        for (Map.Entry<String, Double> entry : villageAndCommunityDistances.entrySet()) {
//            String townName = entry.getKey();  // 乡镇名称
//            double distance = entry.getValue(); // 震中距（D）
//
//            // 计算 X 的值
//            double X = (eqMagnitude > 5.5) ? 0.4 : 0.6;
//
//            // 计算烈度 I
//            double intensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(distance + 24)) - X;
//
//            // 确保烈度不小于 0
//            intensity = Math.max(intensity, 0);
//
//            // 将烈度值四舍五入为整数
//            intensity = Math.round(intensity);
//
//            // 存入 Map
//            all.put(townName, intensity);
//        }
//
//
//        System.out.println("打印所有村（社区）的户数（风普）和常住人口（风普）前7个数据的烈度:");
//        List<String> keys = new ArrayList<>(all.keySet()); // 将 Map 的键转换为列表
//        int sevens = Math.min(11, keys.size()); // 确保不会超出索引范围
//        for (int i = 0; i < sevens; i++) {
//            String key = keys.get(i); // 获取第 i 个键
//            Double intensity = all.get(key); // 通过键获取烈度
//            System.out.println("第" + (i + 1) + "个：" + key + " 的烈度为 " + intensity);
//        }
//
//        // 计算 6 度以上（包含 6 度）的总户数
//        double totalHouseholdsAbove6 = data.stream()
//                .filter(map -> {
//                    // 获取当前乡镇的名称
//                    String villageOrCommunity = (String) map.get("villageOrCommunity");
//
//                    // 获取该乡镇的烈度
//                    Double intensity = all.get(villageOrCommunity);
//
//                    // 确保烈度不为空，并且大于等于 6
//                    return intensity != null && intensity >= 6;
//                })
//                .mapToDouble(map -> {
//                    Object value = map.get("numberOfHouseholdsOrFengpu");
//                    if (value == null) return 0.0;
//                    return (value instanceof Number) ? ((Number) value).doubleValue() : 0.0;
//                })
//                .sum();
//
//        //使用 mapToDouble() 取出户数 numberOfHouseholdsOrFengpu。
//        //使用 sum() 计算总户数。
//
//        System.out.println("6 度以上的总户数：" + totalHouseholdsAbove6);  //L19
//
//
//
//        // 格式化输出
//        String formattedHouseholds;
//        if (totalHouseholdsAbove6 > 10000) {
//            formattedHouseholds = String.format("%.2f万户", totalHouseholdsAbove6 / 10000);
//        } else {
//            formattedHouseholds = (int) totalHouseholdsAbove6 + "户";
//        }
//
//        System.out.println("格式后 6 度以上的户数：" + formattedHouseholds);   //M19
//
//
//        // 计算 6 度以上（包含 6 度）的总常住人口
//        double totalPopulationAbove6 = data.stream()
//                .filter(map -> {
//                    // 获取当前乡镇名称
//                    String villageOrCommunity = (String) map.get("villageOrCommunity");
//
//                    // 获取该乡镇的烈度
//                    Double intensity = all.get(villageOrCommunity);
//
//                    // 确保烈度不为空，并且大于等于 6
//                    return intensity != null && intensity >= 6;
//                })
//                .mapToDouble(map -> {
//                    Object value = map.get("residentPopulationFengpu");
//                    if (value == null) return 0.0;
//                    return (value instanceof Number) ? ((Number) value).doubleValue() : 0.0;
//                })
//                .sum();
//
//        System.out.println("6 度以上的总常住人口：" + totalPopulationAbove6);  //N19
//
//
//        // 格式化输出
//        String formattedPopulation;
//        if (totalPopulationAbove6 > 10000) {
//            formattedPopulation = String.format("%.2f万人", totalPopulationAbove6 / 10000);
//        } else {
//            formattedPopulation = (int) totalPopulationAbove6 + "人";
//        }
//
//        System.out.println("格式后 6 度以上的常住人口：" + formattedPopulation);  //O19
//
//
//
//
//        // 计算 6 度以上（包含 6 度）的村（社区）数量
//        long numberOfCommunityAbove6 = data.stream()
//                .filter(map -> {
//                    // 获取当前村（社区）的名称
//                    String villageOrCommunity = (String) map.get("villageOrCommunity");
//
//                    // 获取该村（社区）的烈度
//                    Double intensity = all.get(villageOrCommunity);
//
//                    // 确保烈度不为空，并且大于等于 6
//                    return intensity != null && intensity >= 6;
//                })
//                .count();
//
//        System.out.println("6 度以上的村（社区）数量：" + numberOfCommunityAbove6);   //K19


        // 变量示例
        int numberOfCommunityAbove6 = 0; // 6 度及以上的行政村（社区）数量
        String formattedHouseholds = "1.53万户"; // 已格式化的户数
        String formattedPopulation = "2.35万人"; // 已格式化的常住人口

        // 生成 communityStatement
        String communityStatement;
        if (numberOfCommunityAbove6 == 0) {
            communityStatement = "";
        } else {
            communityStatement = String.format(
                    "我市地震烈度6度及以上范围内，共涉及我市%d个行政村（社区）%s，常住人口约%s。",
                    numberOfCommunityAbove6, formattedHouseholds, formattedPopulation
            );
        }

        // 输出结果
        System.out.println(communityStatement);


        //--------- 构建基础描述----------


        String result;

        // 先判断震中是否在四川
        if (!eqAddr.contains("四川")) {
            result = "震中不在四川境内，无法判断。";
        } else {
            // 先判断人口密度
            if (!"没有该地人口密度信息".equals(populationDensity)) {
                // 有人口密度信息
                if (!"不在判断区范围".equals(geography)) {
                    // 有地理范围信息，完整输出
                    result = String.format(
                            "地震%s，震区%s。震中%s人口密度约%s人/平方公里。",
                            aftershockConclusion, geography, countyOrDistrict, populationDensity
                    );
                } else {
                    // 没有地理范围信息，只输出人口密度部分
                    result = String.format(
                            "地震%s。震中%s人口密度约%s人/平方公里。",
                            aftershockConclusion, countyOrDistrict, populationDensity
                    );
                }
            } else {
                // 没有人口密度信息
                if (!"不在判断区范围".equals(geography)) {
                    // 有地理范围信息，去掉人口密度描述
                    result = String.format(
                            "地震%s，震区大部分属于%s。",
                            aftershockConclusion, geography
                    );
                } else {
                    // 没有地理范围信息，基础描述
                    result = String.format(
                            "地震%s。",
                            aftershockConclusion
                    );
                }
            }

            // 在结果后面拼接 communityStatement
            result += communityStatement;

        }


        System.out.println("第一段：" + result);


        //***********************************************************************************************

        //获取得到的event  是对应  eqid

        //生成文档后，相关人员自己填写的


        // --------遍历查询地震造成的损伤、损失----------------
        // 获取查询结果
        List<AssessmentResult> loss = assessmentResultMapper.selectList(null);

        // 初始化累加变量
        int totalDeath = 0; // 总死亡人数
        int injury = 0; //受伤人数
        BigDecimal totalEconomicLoss = BigDecimal.ZERO; // 总经济损失（万元）
        BigDecimal totalBuildingDamage = BigDecimal.ZERO; // 总建筑破坏面积（万平方米）


        System.out.println("event的值为：" + event);

        // 遍历匹配 eqid 并累加
        for (AssessmentResult material : loss) {
            if (event != null && event.equals(material.getEqid())) {
                // 打印匹配到的记录
                System.out.println("匹配到的数据: " + material);

                totalDeath += Optional.ofNullable(material.getDeath()).orElse(0);
                injury += Optional.ofNullable(material.getInjury()).orElse(0);

                totalEconomicLoss = totalEconomicLoss.add(
                        Optional.ofNullable(material.getEconomicLoss())
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .map(BigDecimal::new)
                                .orElse(BigDecimal.ZERO)
                );

                totalBuildingDamage = totalBuildingDamage.add(
                        Optional.ofNullable(material.getBuildingDamage())
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .map(BigDecimal::new)
                                .orElse(BigDecimal.ZERO)
                );
            }
        }


        // 输出结果
        System.out.println("****总死亡人数：" + totalDeath + " 人****");
        System.out.println("****总受伤人数：" + injury + " 人****");
        System.out.println("****总经济损失：" + totalEconomicLoss + " 万元****");
        BigDecimal totalEconomicLossTwo = totalEconomicLoss.divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP);
        System.out.println("****总经济损失：" + totalEconomicLossTwo + " 亿元****");
        System.out.println("****总建筑破坏面积：" + totalBuildingDamage + " 万平方米****");
        // 计算总建筑破坏面积（平方公里）
        BigDecimal totalBuildingDamageTwo = totalBuildingDamage.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        System.out.println("****总建筑破坏面积：" + totalBuildingDamageTwo + " 平方公里****");


        // 生成结果字符串
        String fuJinTownResult = generateResultString(totalDeath, injury, totalEconomicLossTwo.doubleValue(), totalBuildingDamageTwo.doubleValue());

        System.out.println(fuJinTownResult);


        //***********************************************************************************************


        //------------- 定义 类别(category) 变量-----------
        String category;  //D31

        // 判断 eqName 对应的字段
        if (eqName.contains("雅安市") ||
                eqName.contains("名山区") ||
                eqName.contains("宝兴县") ||
                eqName.contains("雨城区") ||
                eqName.contains("芦山县") ||
                eqName.contains("天全县") ||
                eqName.contains("荥经县") ||
                eqName.contains("汉源县") ||
                eqName.contains("石棉县")) {
            category = "雅安本地地震";
        } else if (eqName.contains("阿坝州") || eqName.contains("甘孜州") || eqName.contains("眉山市") ||
                eqName.contains("成都市") || eqName.contains("凉山州") || eqName.contains("乐山市")) {
            category = "邻近市州地震";
        } else {
            category = "外地地震";  // 其他情况归为外地地震   //？？？
        }

        System.out.println("属于哪个位置的地震（category）:" + category);
        //------------判断------------------

        int maxIntensity = 0;   //  最大烈度 初始为0       B20 无"度"字
        int outside = 0;//   辖区外地震雅安最大烈度     F19

        //如果是不是雅安市内的
        double big = 0; //外地地震雅安最大烈度8度及以上时，筛选7度及以上县个数   J26
        double middle = 0; //外地地震雅安最大烈度7度及以上时，筛选6度、7度县个数  K26
        double small = 0;  //外地地震雅安最大烈度6度及以上时，筛选5度、6度县个数   L26


        //震中附近乡镇计算
        if (eqName.contains("四川")) {

            if (eqAddr.contains("雅安市") ||
                    eqAddr.contains("名山区") ||
                    eqAddr.contains("宝兴县") ||
                    eqAddr.contains("雨城区") ||
                    eqAddr.contains("芦山县") ||
                    eqAddr.contains("天全县") ||
                    eqAddr.contains("荥经县") ||
                    eqAddr.contains("汉源县") ||
                    eqAddr.contains("石棉县")) {
                //显示文字，震中距离雅安市边界约distance公里，就将上传的经纬度latitude, longitude和雅安市行政边界表yaan_administrative_boundary计算每个震中距离找出最小值

                System.out.println("------------------eqAddr内有雅安市，进入雅安市的烈度计算---------------------:" + eqAddr);
                // ------------计算震中距离雅安市边界的最小距离----------------

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
                            double distanceToCountyTown = calculateDistance(latitude, longitude, boundaryLat, boundaryLon);
                            // 更新最小距离
                            if (distanceToCountyTown < minBoundaryDistance) {
                                minBoundaryDistance = distanceToCountyTown;
                            }
                        }
                    }
                }

                // 计算后的最小距离（单位：公里）
                double minDistanceInKm = minBoundaryDistance;  // 转换为公里


                //----------------------震中到乡镇点的距离--------------
                // 查询雅安市所有乡镇（yaan_villages表）
                List<YaanVillages> villageList = yaanVillagesMapper.selectList(null);

                // 初始化最小距离（公里）
                double minVillageDistance = Double.MAX_VALUE;
//                String nearestVillage = "";

                // 遍历所有乡镇，计算与上传经纬度的距离
                for (YaanVillages village : villageList) {
                    Geometry geom = village.getGeom();
                    if (geom != null && geom instanceof Point) {
                        // 获取乡镇的经纬度
                        Point villagePoint = (Point) geom;
                        double villageLat = villagePoint.getY(); // 纬度
                        double villageLon = villagePoint.getX(); // 经度

                        // 计算震中到乡镇点的距离（单位：米）
                        double distanceToVillage = calculateDistance(latitude, longitude, villageLat, villageLon);

                        // 更新最小距离
                        if (distanceToVillage < minVillageDistance) {
                            minVillageDistance = distanceToVillage;
                        }
                    }
                }


                System.out.println("计算取整后辖区外地震雅安最大烈度roundedIntensity1所用距离为:  ");
                System.out.println("距离雅安市边界的最小距离 minDistanceInKm: " + minDistanceInKm);
                System.out.println("计算取整后最大烈度（烈度衰减公式长轴计算结果）minVillageDistance所用距离为: ");
                System.out.println("震中到乡镇点的最小距离 roundedIntensity2:" + minVillageDistance);

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
                    intensity2 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minVillageDistance + 24)) - 0.4;

                } else {
                    // 震级小于或等于5.5时使用这个公式
                    intensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(0.1 + 24)) - 0.6;
                    intensity1 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minDistanceInKm + 24)) - 0.6;
                    intensity2 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minVillageDistance + 24)) - 0.6;
                }

                // 打印变量的值
                System.out.println("震中点最大烈度（烈度衰减长轴计算结果）: " + intensity);
                System.out.println("辖区外地震雅安最大烈度: " + intensity1);
                System.out.println("最大烈度（烈度衰减公式长轴计算结果）: " + intensity2);

                int roundedIntensity = (int) Math.round(intensity);
                int roundedIntensity1 = (int) Math.round(intensity1);
                int roundedIntensity2 = (int) Math.round(intensity2);

                // 打印变量的值
                System.out.println("取整后震中点最大烈度（烈度衰减长轴计算结果）: " + roundedIntensity);
                System.out.println("取整后辖区外地震雅安最大烈度: " + roundedIntensity1);
                System.out.println("取整后最大烈度（烈度衰减公式长轴计算结果）: " + roundedIntensity2);


                //得到我市最大烈度
                maxIntensity = roundedIntensity2;

                System.out.println("maxIntensity烈度选取：roundedIntensity2取整后最大烈度（烈度衰减公式长轴计算结果）: " + roundedIntensity2);

            }


            //不在雅安市地震
            if (!eqAddr.contains("雅安市")) {

                System.out.println("------------------eqAddr内没有雅安市，进入外地的烈度计算---------------------:" + eqAddr);


                // ------------计算震中距离雅安市边界的最小距离----------------


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

                            // 计算震中到边界的距离（单位：米）minBoundaryDistance1
                            double distanceToCountyTown1 = calculateDistance(latitude, longitude, boundaryLat, boundaryLon);
                            // 更新最小距离
                            if (distanceToCountyTown1 < minBoundaryDistance1) {
                                minBoundaryDistance1 = distanceToCountyTown1;
                            }
                        }
                    }
                }
                // 计算后的最小距离（单位：公里）
                double minDistanceInKm1 = minBoundaryDistance1;  // 转换为公里

                System.out.println("距离雅安市边界的最小距离 minDistanceInKm1: " + minDistanceInKm1);


                //*******************----------------------尝试烈度-------------------
                /////////////////////-----------
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
                        double distanceToCountyTown1 = calculateDistance(latitude, longitude, countyTownLat, countyTownLon);

                        // 单独处理雅安市政府和四川省政府的情况
                        if ("雅安市政府".equals(countyTown.getCountyTownName())) {
                            yaanCityGovDistance1 = distanceToCountyTown1; // 转换为公里
                        } else if ("四川省政府".equals(countyTown.getCountyTownName())) {
                            sichuanProvGovDistance1 = distanceToCountyTown1; // 转换为公里
                        } else {
                            // 正常更新最近的政府乡镇距离和名称  minCountyTownDistance1
                            if (distanceToCountyTown1 < minCountyTownDistance1) {
                                minCountyTownDistance1 = distanceToCountyTown1;
                                nearestCountyTown1 = countyTown.getCountyTownName(); // 获取最近的政府乡镇名称
                            }
                        }
                    }
                }

//                // 生成最终输出文字
//                String fuJinCountyTownResult1 = String.format(
//                        "距离雅安市"+nearestCountyTown1+"约%.1f公里，距离雅安市政府约%.1f公里，距离四川省政府约%.1f公里。",
//                        minCountyTownDistance1, yaanCityGovDistance1, sichuanProvGovDistance1
//                );
//                // 生成返回的政府乡镇距离结果
//                System.out.println(fuJinCountyTownResult1);

                //-------------------------------------------

                // 查询所有城市的经纬度（geom字段）
                List<YaanProvinceCity> cityList1 = yaanProvinceCityMapper.selectList(null);

                // 初始化最小距离
                double minCityDistance1 = Double.MAX_VALUE;
                String nearestCity1 = "";

                // 遍历所有城市，计算与上传经纬度的距离
                for (YaanProvinceCity city : cityList1) {
                    Geometry geom = city.getGeom();
                    System.out.println("geom:" + geom);
                    if (geom != null && geom instanceof Point) {
                        // 获取城市的经纬度
                        Point cityPoint = (Point) geom;

                        // 提取经纬度
                        double cityLat = cityPoint.getY(); // 纬度
                        double cityLon = cityPoint.getX(); // 经度

                        // 计算震中到城市点的距离（单位：米） minCityDistance1
                        double distanceToCity1 = calculateDistance(latitude, longitude, cityLat, cityLon);
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
//                System.out.println("最小距离（公里）：" + minCityDistanceInKm1);
//
//                // 生成返回的城市距离结果
//                String fuJinCityResult1 = String.format("距离"+ nearestCity1 +"政府约%.2f公里，", minCityDistanceInKm1);
//                System.out.println(fuJinCityResult1);


                // 查询雅安市所有乡镇（yaan_villages表）
                List<YaanVillages> villageList = yaanVillagesMapper.selectList(null);

                // 初始化最小距离
                double minVillageDistance = Double.MAX_VALUE;
                String nearestVillage = "";
                for (YaanVillages village : villageList) {
                    Geometry geom = village.getGeom();
                    if (geom != null && geom instanceof Point) {
                        // 获取乡镇的经纬度
                        Point villagePoint = (Point) geom;
                        double villageLat = villagePoint.getY(); // 纬度
                        double villageLon = villagePoint.getX(); // 经度

                        // 计算震中到乡镇点的距离（单位：米） minVillageDistance
                        double distanceToVillage = calculateDistance(latitude, longitude, villageLat, villageLon);

                        // 更新最小距离
                        if (distanceToVillage < minVillageDistance) {
                            minVillageDistance = distanceToVillage;
                            nearestVillage = village.getVillagesName();  // 获取最近的乡镇名称
                        }
                    }
                }


                System.out.println("取整后震中到城市点的最小距离minCityDistance1: " + minCityDistance1);  //震中区最大地震烈度达%d度
                System.out.println("取整后雅安市行政边界最小距离minDistanceInKm1: " + minDistanceInKm1);
                System.out.println("取整后最近的政府乡镇距离minCountyTownDistance1: " + minCountyTownDistance1);
                System.out.println("取整后雅安市所有乡镇距离minCountyTownDistance1: " + minVillageDistance);
                //*************-----------------------------------------------------


                double intensity;  //辖区外地震雅安最大烈度
                double intensity1;  //
                double intensity2; //
                double intensity3;

                //震级：eqMagnitude
                //主要受影响区域
                if (eqMagnitude > 5.5) {
                    // 震级大于5.5时使用这个公式
                    intensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minCityDistance1 + 24)) - 0.4;  //计算震中到城市点的最小距离（单位：米） minCityDistance1
                    intensity1 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minDistanceInKm1 + 24)) - 0.4; //计算雅安市行政边界最小距离（单位：米）minBoundaryDistance1
                    intensity2 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minCountyTownDistance1 + 24)) - 0.4;  //正常更新最近的政府乡镇距离  minCountyTownDistance1
                    intensity3 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minVillageDistance + 24)) - 0.4;  //雅安市所有乡镇距离乡镇最小距离  minCountyTownDistance1

                } else {
                    // 震级小于或等于5.5时使用这个公式
                    intensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minCityDistance1 + 24)) - 0.6;  //计算震中到城市点的最小距离（单位：米） minCityDistance1
                    intensity1 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minDistanceInKm1 + 24)) - 0.6; //计算雅安市行政边界最小距离（单位：米）minBoundaryDistance1
                    intensity2 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minCountyTownDistance1 + 24)) - 0.6; //正常更新最近的政府乡镇距离  minCountyTownDistan
                    intensity3 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minVillageDistance + 24)) - 0.6;  //雅安市所有乡镇距离乡镇最小距离  minCountyTownDistance1

                }
                System.out.println("震中到城市点的最小距离的烈度: " + intensity);  //震中区最大地震烈度达%d度
                System.out.println("雅安市行政边界最小距离烈度(辖区外地震雅安最大烈度):  " + intensity1);
                System.out.println("最近的政府乡镇的烈度: " + intensity2);
                System.out.println("雅安市震中到乡镇点的最小距离: " + intensity3);

                // 四舍五入取整
                int roundedIntensity = (int) Math.round(intensity);
                int roundedIntensity1 = (int) Math.round(intensity1);
                int roundedIntensity2 = (int) Math.round(intensity2);
                int roundedIntensity3 = (int) Math.round(intensity3);

                // 打印变量的值
                System.out.println("取整后震中到城市点的最小距离的烈度: " + roundedIntensity);  //震中区最大地震烈度达%d度
                System.out.println("取整后雅安市行政边界最小距离烈度(取整后辖区外地震雅安最大烈度:): " + roundedIntensity1);
                System.out.println("取整后最近的政府乡镇的烈度: " + roundedIntensity2);
                System.out.println("取整后雅雅安市震中到乡镇点的最小距离: " + roundedIntensity3);


                //得到我市最大烈度
                maxIntensity = roundedIntensity3;
                System.out.println("maxIntensity烈度选取：roundedIntensity3取整后雅安市所有乡镇距离乡镇最小距离: " + roundedIntensity3);

                //得到我市最大烈度
                outside = roundedIntensity1;
                System.out.println("outside烈度选取：roundedIntensity3取整后雅安市所有乡镇距离乡镇最小距离: " + roundedIntensity1);


                // 如果最大烈度大于等于 6 度，计算所有乡镇的震中距以及烈度
                if (maxIntensity >= 6) {
                    // 查询雅安市所有乡镇（yaan_villages表）
                    villageList = yaanVillagesMapper.selectList(null);

                    // 初始化统计变量
                    big = 0;    // 7度及以上的县区个数
                    middle = 0; // 6度及以上的县区个数
                    small = 0;  // 5度及以上的县区个数

                    // 遍历所有乡镇，计算与上传经纬度的距离并计算烈度
                    for (YaanVillages village : villageList) {
                        Geometry geom = village.getGeom();
                        if (geom != null && geom instanceof Point) {
                            // 获取乡镇的经纬度
                            Point villagePoint = (Point) geom;
                            double villageLat = villagePoint.getY(); // 纬度
                            double villageLon = villagePoint.getX(); // 经度

                            // 计算震中到乡镇点的距离（单位：米）
                            double distanceToVillage = calculateDistance(latitude, longitude, villageLat, villageLon);

                            // 计算震中到乡镇的烈度
                            double allIntensity3;
                            if (eqMagnitude > 5.5) {
                                allIntensity3 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(distanceToVillage + 24)) - 0.4;
                            } else {
                                allIntensity3 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(distanceToVillage + 24)) - 0.6;
                            }

                            // 根据烈度筛选县区并计数
                            if (allIntensity3 >= 8) {
                                big++; // 7度及以上的县区
                            } else if (allIntensity3 >= 7) {
                                middle++; // 6度及以上的县区
                            } else if (allIntensity3 >= 6) {
                                small++; // 5度及以上的县区
                            }
                        }
                    }


                    // 输出筛选结果
                    System.out.println("1.外地地震雅安最大烈度8度及以上时，筛选7度及以上县个数: double big = " + big);
                    System.out.println("1.外地地震雅安最大烈度7度及以上时，筛选6度、7度县个数: double middle = " + middle);
                    System.out.println("1.外地地震雅安最大烈度6度及以上时，筛选5度、6度县个数: double small = " + small);
                }


            }

        }

        System.out.println("最大烈度为: maxIntensity = " + maxIntensity);

        String maxIntensityWithUnit = maxIntensity + "度";  //B20 有"度"字
        System.out.println("maxIntensityWithUnit将最大烈度加上-度-字 ：" + maxIntensityWithUnit);

        // 输出筛选结果
        System.out.println("外地地震雅安最大烈度8度及以上时，筛选7度及以上县个数: double big = " + big);
        System.out.println("外地地震雅安最大烈度7度及以上时，筛选6度、7度县个数: double middle = " + middle);
        System.out.println("外地地震雅安最大烈度6度及以上时，筛选5度、6度县个数: double small = " + small);

        //---------最终影响-------
        String influence = ""; // 初始化影响的字符串  E31

        // 判断震中点最大烈度并输出影响描述
        if (maxIntensity == 0 || maxIntensity == 1 || maxIntensity == 2) {
            influence = "无影响";
        } else if (maxIntensity == 3) {
            influence = "影响较小";
        } else if (maxIntensity == 4 || maxIntensity == 5) {
            influence = "有一定影响";
        } else if (maxIntensity == 6) {
            influence = "影响较大";
        } else if (maxIntensity == 7 || maxIntensity == 8 || maxIntensity == 9) {
            influence = "影响非常大";
        } else if (maxIntensity == 10 || maxIntensity == 11 || maxIntensity == 12) {
            influence = "影响巨大，部分区域是毁灭性的";
        } else {
            influence = "无影响"; // 如果输入的值不在有效范围内
        }

        // 输出影响结果
        System.out.println("影响：" + influence);

        //-------灾害等级-------

        String disasterLevel = "";  //C31

        if ("没有该地人口密度信息".equals(populationDensity)) {
            disasterLevel = "";
        } else {
            // 将字符串类型的populationDensity转换为int
            int populationDensityInt = Integer.parseInt(populationDensity);  // 字符串转换为整数
            // 检查人口密度和震级，根据条件返回灾害等级
            if (populationDensityInt >= 200) {
                if (eqMagnitude >= 3.5 && eqMagnitude < 4.0) {
                    disasterLevel = "属有感地震。";
                } else if (eqMagnitude >= 4.0 && eqMagnitude < 5.0) {
                    disasterLevel = "按照地震灾害事件分级，属一般地震灾害。";
                } else if (eqMagnitude >= 5.0 && eqMagnitude < 6.0) {
                    disasterLevel = "按照地震灾害事件分级，属较大地震灾害。";
                } else if (eqMagnitude >= 6.0 && eqMagnitude < 7.0) {
                    disasterLevel = "按照地震灾害事件分级，属重大地震灾害。";
                } else if (eqMagnitude >= 7.0) {
                    disasterLevel = "按照地震灾害事件分级，属特别重大地震灾害。";
                } else {
                    disasterLevel = "";
                }
            } else {
                if (eqMagnitude >= 3.5 && eqMagnitude < 4.5) {
                    disasterLevel = "属有感地震。";
                } else if (eqMagnitude >= 4.5 && eqMagnitude < 5.5) {
                    disasterLevel = "按照地震灾害事件分级，属一般地震灾害。";
                } else if (eqMagnitude >= 5.5 && eqMagnitude < 6.5) {
                    disasterLevel = "按照地震灾害事件分级，属较大地震灾害。";
                } else if (eqMagnitude >= 6.5 && eqMagnitude < 7.5) {
                    disasterLevel = "按照地震灾害事件分级，属重大地震灾害。";
                } else if (eqMagnitude >= 7.5) {
                    disasterLevel = "按照地震灾害事件分级，属特别重大地震灾害。";
                } else {
                    disasterLevel = "";
                }
            }
        }
        ;


        System.out.println("灾害等级:" + disasterLevel);

        String panduan = String.format("本次地震是%s，%s综合判断：本次地震对我市%s。",
                category, disasterLevel, influence

        );
        System.out.println(panduan);


        //***********************************************************************************************


        String suggestion = "";   //B31

        // 判断 eqName 是否包含 "雅安市"
        if (eqName.contains("雅安市")) {
            // 针对雅安市的逻辑
            if (eqMagnitude < 3.5) {
                suggestion = "不启动地震应急响应";
            } else if ((eqAddr.contains("雨城区") || eqAddr.contains("名山区")) && eqMagnitude >= 3.5 && eqMagnitude < 4.0) {
                suggestion = "启动强有感地震应急响应";
            } else if (!(eqAddr.contains("雨城区") || eqAddr.contains("名山区")) && eqMagnitude >= 3.5 && eqMagnitude < 4.5) {
                suggestion = "启动强有感地震应急响应";
            } else if ((eqAddr.contains("雨城区") || eqAddr.contains("名山区")) && eqMagnitude >= 4.0 && eqMagnitude < 5.0) {
                suggestion = "启动市级地震灾害三级应急响应";
            } else if (!(eqAddr.contains("雨城区") || eqAddr.contains("名山区")) && eqMagnitude >= 4.5 && eqMagnitude < 5.5) {
                suggestion = "启动市级地震灾害三级应急响应";
            } else if ((eqAddr.contains("雨城区") || eqAddr.contains("名山区")) && eqMagnitude >= 5.0 && eqMagnitude < 6.0) {
                suggestion = "启动市级地震灾害二级应急响应";
            } else if (!(eqAddr.contains("雨城区") || eqAddr.contains("名山区")) && eqMagnitude >= 5.5 && eqMagnitude < 6.5) {
                suggestion = "启动市级地震灾害二级应急响应";
            } else if ((eqAddr.contains("雨城区") || eqAddr.contains("名山区")) && eqMagnitude >= 6.0) {
                suggestion = "启动市级地震灾害一级应急响应";
            } else if (!(eqAddr.contains("雨城区") || eqAddr.contains("名山区")) && eqMagnitude >= 6.5) {
                suggestion = "启动市级地震灾害一级应急响应";
            } else {
                suggestion = "应急响应无符合启动条件";
            }

        } else {
            // 判断 eqName 不包含 "雅安市"
            if (outside < 5) {
                if (eqMagnitude < 5.0) {
                    suggestion = "不启动地震应急响应";
                } else {
                    suggestion = "启动外地地震应急响应";
                }
            } else if ((outside == 6.0 && big == 0.0) || outside == 5.0 || big == 1.0) {
                suggestion = "启动强有感地震应急响应";
            } else if (big >= 2) {
                suggestion = "启动市级地震灾害一级应急响应";
            } else if (middle >= 2) {
                suggestion = "启动市级地震灾害二级应急响应";
            } else if (small >= 2) {
                suggestion = "启动市级地震灾害三级应急响应";
            } else {
                suggestion = "应急响应无符合启动条件";   //？？
            }

            // 处理大、中、小区域的响应

        }

        // 输出建议
        System.out.println("应急响应建议: " + suggestion);

        String response;

        if ("不启动地震应急响应".equals(suggestion)) {
            response = " ";
        } else {
            // 去掉suggestion中的“启动”
            String suggestionWithoutStart = suggestion.replace("启动", "");
            response = "建议按照《雅安市地震应急预案》" + suggestionWithoutStart + "处置措施开展应急处置。";
        }
        System.out.println("响应:" + response);  // Output: 按照《雅安市地震应急预案》市级地震灾害一级应急响应处置措施开展应急处置


        // 计算应急响应的详细内容
        String plan = generateResponse(cityOrState, suggestion, outside, big, middle, small, eqMagnitude, countyOrDistrict, populationDensity);

        // 检查 maxIntensity 是否小于 0，如果是，设置为 0
        if (maxIntensity < 0) {
            maxIntensity = 0;
        }

        System.out.println("最终的最大烈度是：" + maxIntensity);

        // 生成应急响应建议
        String jianyi;
        if ("不启动地震应急响应".equals(suggestion)) {
            jianyi = String.format(
                    "应急响应建议：建议我市不启动地震应急响应（主要依据：1、%s；2、震级:%.1f级；3、我市最大烈度：%d度。）",
                    category, eqMagnitude, maxIntensity
            );
        } else {
            jianyi = String.format(
                    "应急响应建议：建议我市%s，主要依据：1、%s；%s%s",
                    suggestion, category, plan, response
            );
        }

        // 输出结果
        System.out.println("最终应急响应建议: " + jianyi);


        //***********************************************************************************************

        //处置机构  G31
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
                double distanceToVillage1 = calculateDistance(latitude, longitude, villageLat, villageLon);

                // 计算震中到乡镇点的距离（单位：米）

                // 更新最小距离
                if (distanceToVillage1 < minVillageDistance1) {
                    minVillageDistance1 = distanceToVillage1;
                    nearestVillage1 = village.getVillagesName();  // 获取最近的乡镇名称
                }
            }
        }

        System.out.println("最近的雅安政府乡镇名称" + nearestVillage1);    //????不太确定YaanVillages表名，数据库没开


        //String  countyOrDistrict  县/区   输出eg:泸定县  F26  L3
        //String  cityOrState   市/州   输出eg:甘孜州   C26  k3
        //String  nearestVillage1  最近的雅安政府乡镇名称  输出eg:石棉县草科藏族乡  //G7


        String measure = "";

        // 将字符串转换为数字类型
        double populationDensityValue = Double.parseDouble(populationDensity);

        System.out.println("字符串转换为数字类型建议:人口密度 populationDensityValue  " + populationDensityValue);

        if (eqName.contains("雅安市")) {
            // 判断人口密度和震级
            if (populationDensityValue >= 200) {
                if (eqMagnitude < 5.0) {
                    measure = "以" + countyOrDistrict + "政府为主开展应急处置";
                } else if (eqMagnitude >= 5.0 && eqMagnitude < 6.0) {
                    measure = "以" + cityOrState + "政府为主开展应急处置";
                } else if (eqMagnitude >= 6.0) {
                    measure = "以省政府为主开展应急处置，并接受省抗震救灾指挥部的领导与指挥";
                } else {
                    measure = "无此应急响应情况，未定义应急处置建议";   //??
                }
            } else {
                if (eqMagnitude < 5.5) {
                    measure = "以" + countyOrDistrict + "政府为主开展应急处置";
                } else if (eqMagnitude >= 5.5 && eqMagnitude < 6.5) {
                    measure = "以" + cityOrState + "政府为主开展应急处置";
                } else if (eqMagnitude >= 6.5) {
                    measure = "以省政府为主开展应急处置，并接受省抗震救灾指挥部的领导与指挥";
                } else {
                    measure = "无此应急响应情况，未定义应急处置建议";  //??
                }
            }
        } else {
            System.out.println("Suggestion: " + suggestion); // 调试信息，查看 suggestion 值
            // 针对非雅安市
            switch (suggestion) {
                case "不启动地震应急响应":
                    measure = "以震中政府为主开展应急处置";
                    break;
                case "启动外地地震应急响应":
                    measure = "以" + countyOrDistrict + "政府为主开展应急处置";
                    break;
                case "启动强有感地震应急响应":
                case "启动市级地震灾害三级应急响应":
                    measure = "以" + String.valueOf(minVillageDistance1).substring(0, 3) + "政府为主开展应急处置";
                    break;
                case "启动市级地震灾害二级应急响应":
                    measure = "以雅安市政府为主开展应急处置";
                    break;
                case "启动市级地震灾害一级应急响应":
                    measure = "以省政府为主开展应急处置，并接受省抗震救灾指挥部的领导与指挥";
                    break;
                case "应急响应无符合启动条件":
                    measure = "应急响应无符合启动条件，未定义应急处置建议";
                    break;
                default:
                    measure = "无此应急响应情况，未定义应急处置建议";
            }
        }

        // 输出结果
        System.out.println("应急措施: " + measure);   //G31

        //1.指挥部建议  H31

        String earthquakeName = "“" + monthDay + "”" + newCountyOrDistrict + eqMagnitude + "级地震";  // N29

        String headquarters = earthquakeResponse(cityOrState, suggestion, countyOrDistrict, earthquakeName);

        System.out.println("1.指挥部建议 :" + headquarters);  // 输出相应的响应信息

        //2.灾情收集建议  I31  放在4下面，因为4有maximumIntensityPoint  **

        //3.应急支援建议  J31

        String support = supportFunction(cityOrState, eqMagnitude, cityOrState, suggestion);


        System.out.println("3.应急支援建议 :" + support);  // 输出相应的响应信息

        //4.交通处置建议  K31


        //(1) I29 有破坏县区（6-12度）

        // 模拟查询雅安市政府相关的县区镇（yaan_county_town表）
        List<YaanCountyTown> countyTownList1 = yaanCountyTownMapper.selectList(null);  //????不太确定YaanCountyTown表名，数据库没开

        // 用 Map 存储所有政府县区名称及其对应的震中距
        Map<String, Double> countyTownDistances = new LinkedHashMap<>();

        // 遍历所有政府相关的乡镇，计算与上传经纬度的距离
        for (YaanCountyTown countyTown : countyTownList1) {
            Geometry geom = countyTown.getGeom();
            if (geom != null && geom instanceof Point) {
                // 获取政府县区的经纬度
                Point countyTownPoint = (Point) geom;
                double countyTownLat = countyTownPoint.getY(); // 纬度
                double countyTownLon = countyTownPoint.getX(); // 经度

                // 计算震中到县区点的距离（单位：米）
                double distanceToCountyTown = calculateDistance(latitude, longitude, countyTownLat, countyTownLon);

                // 存储县区名称及其震中距
                countyTownDistances.put(countyTown.getCountyTownName(), distanceToCountyTown);
            }
        }

        // **输出所有县区的名称和对应的震中距离**
        for (Map.Entry<String, Double> entry : countyTownDistances.entrySet()) {
            System.out.println("县区名称: " + entry.getKey() + "，震中距: " + entry.getValue() + " 米");
        }

        // 存储乡镇名称、震中距和计算后的烈度
        Map<String, Double> intensities = new LinkedHashMap<>();

        // 遍历计算每个乡镇的烈度
        for (Map.Entry<String, Double> entry : countyTownDistances.entrySet()) {
            String townName = entry.getKey();  // 乡镇名称
            double distance = entry.getValue(); // 震中距（D）

            // 计算 X 的值
            double X = (eqMagnitude > 5.5) ? 0.4 : 0.6;

            // 计算烈度 I
            double intensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(distance + 24)) - X;

            // 确保烈度不小于 0
            intensity = Math.max(intensity, 0);

            // 将烈度值四舍五入为整数
            intensity = Math.round(intensity);

            // 存入 Map
            intensities.put(townName, intensity);
        }

        // **输出每个乡镇的烈度**
        for (Map.Entry<String, Double> entry : intensities.entrySet()) {
            System.out.println("县区名称: " + entry.getKey() + "，震中距: " + countyTownDistances.get(entry.getKey()) + " km，烈度: " + String.format("%.2f", entry.getValue()));
        }

        // **将 Map 转换成 List 并排序**
        List<Map.Entry<String, Double>> sortedList = new ArrayList<>(intensities.entrySet());
        sortedList.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue())); // 按烈度降序排序

        // **输出排序后的乡镇烈度信息**
        System.out.println("县区名称 | 烈度");
        System.out.println("--------------------");
        for (Map.Entry<String, Double> entry : sortedList) {
            System.out.printf("%s | %.2f%n", entry.getKey(), entry.getValue());
        }

        String destroy = generateDestroy(sortedList);
        System.out.println("有破坏县区（6-12度）：" + destroy);  // 输出相应的响应信息

        //(2) J29 最大烈度点

        // 查询雅安市所有乡镇
        List<YaanVillages> villageList = yaanVillagesMapper.selectList(null); //????不太确定YaanVillages表名，数据库没开

        // 用 Map 存储所有乡镇名称及其对应的震中距
        Map<String, Double> villageDistances = new HashMap<>();

        // 遍历所有乡镇，计算与上传经纬度的距离
        for (YaanVillages village : villageList) {
            Geometry geom = village.getGeom();
            if (geom != null && geom instanceof Point) {
                // 获取乡镇的经纬度
                Point villagePoint = (Point) geom;
                double villageLat = villagePoint.getY(); // 纬度
                double villageLon = villagePoint.getX(); // 经度

                // 计算震中到乡镇点的距离（单位：米）
                double distanceToVillage = calculateDistance(latitude, longitude, villageLat, villageLon);

                // 存储乡镇名称及其震中距
                villageDistances.put(village.getVillagesName(), distanceToVillage);
            }
        }

        // **对乡镇按照震中距从小到大排序，并取前 8 个**
        List<Map.Entry<String, Double>> sortedVillages = new ArrayList<>(villageDistances.entrySet());
        sortedVillages.sort(Map.Entry.comparingByValue()); // 按震中距升序排序

        // 取前 8 个乡镇
        int limit = Math.min(8, sortedVillages.size()); // 防止乡镇数量不足 8 个
        List<Map.Entry<String, Double>> top8Villages = sortedVillages.subList(0, limit);

        // **输出前 8 个乡镇及其震中距**
        System.out.println("最近的 8 个乡镇及其震中距：");
        for (Map.Entry<String, Double> entry : top8Villages) {
            System.out.println("乡镇名称: " + entry.getKey() + "，震中距: " + entry.getValue() + " 米");
        }


        // 存储乡镇名称、震中距和计算后的烈度
        Map<String, Double> intensities2 = new LinkedHashMap<>();

        // 遍历计算每个乡镇的烈度
        for (Map.Entry<String, Double> entry : villageDistances.entrySet()) {
            String townName = entry.getKey();  // 乡镇名称
            double distance = entry.getValue(); // 震中距（D）

            // 计算 X 的值
            double X = (eqMagnitude > 5.5) ? 0.4 : 0.6;

            // 计算烈度 I
            double intensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(distance + 24)) - X;

            // 确保烈度不小于 0
            intensity = Math.max(intensity, 0);

            // 将烈度值四舍五入为整数
            intensity = Math.round(intensity);

            // 存入 Map
            intensities2.put(townName, intensity);
        }

        // **输出每个乡镇的烈度**
        for (Map.Entry<String, Double> entry : intensities2.entrySet()) {
            System.out.println("乡镇名称: " + entry.getKey() + "，震中距: " + villageDistances.get(entry.getKey()) + " km，烈度: " + String.format("%.2f", entry.getValue()));
        }

        // **将 Map 转换成 List 并排序**
        List<Map.Entry<String, Double>> sortedList2 = new ArrayList<>(intensities2.entrySet());
        sortedList2.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue())); // 按烈度降序排序

        // **输出排序后的乡镇烈度信息**
        System.out.println("乡镇名称 | 烈度");
        System.out.println("--------------------");
        for (Map.Entry<String, Double> entry : sortedList2) {
            System.out.printf("%s | %.2f%n", entry.getKey(), entry.getValue());
        }

        // 计算最大烈度点
        String maximumIntensityPoint = generateDestroy2(sortedList, sortedList2, maxIntensity);

        // 输出结果
        System.out.println("最大烈度点：" + maximumIntensityPoint);


        //(3) 开始 4.交通处置建议 逻辑

        //String cityOrState   为  C26
        //Double eqMagnitude 为 I3
        //String  cityOrState  为  K3
        //String  countyOrDistrict 为 F26
        //String suggestion 为  B31
        //String category 为  D31
        //String destroy 为  I29
        //String maximumIntensityPoint 为  J29


        String transportation = transportationFunction(cityOrState, eqMagnitude, cityOrState,
                countyOrDistrict, suggestion,
                category, destroy, maximumIntensityPoint);


        System.out.println("4.交通处置建议:" + transportation);  // 输出相应的响应信息

        //2.灾情收集建议  I31  放在4下面，因为4有maximumIntensityPoint  **

        String disasterCollection = disasterCollectionFunction(maxIntensityWithUnit, maximumIntensityPoint);


        System.out.println("2.灾情收集建议 :" + disasterCollection);  // 输出相应的响应信息

        //5.危险源处置建议  L31

        String dangerSource = generateResponse(maxIntensityWithUnit);
        System.out.println("5.危险源处置建议：" + dangerSource);  // 输出相应的响应信息

        //6.灾情公开建议  M31

        String disasterMadepublic = "无";

        // 判断是否为市级地震灾害响应
        if (suggestion.equals("启动市级地震灾害三级应急响应") ||
                suggestion.equals("启动市级地震灾害二级应急响应") ||
                suggestion.equals("启动市级地震灾害一级应急响应")) {
            disasterMadepublic = "尽快向社会公开震情、灾情等，统筹“12345”政务服务便民热线，建立抗震救灾服务热线，及时回应抗震救灾相关咨询";
        } else {
            disasterMadepublic = "无";
        }

        System.out.println("6.灾情公开建议：" + disasterMadepublic);  // 输出相应的响应信息

        //7.舆情处置建议  N31

        String publicOpinion = "无";

        // 判断是否属于地震强度在3度及以上的情况
        if (maxIntensityWithUnit.equals("3度") || maxIntensityWithUnit.equals("4度") || maxIntensityWithUnit.equals("5度") ||
                maxIntensityWithUnit.equals("6度") || maxIntensityWithUnit.equals("7度") || maxIntensityWithUnit.equals("8度") ||
                maxIntensityWithUnit.equals("9度") || maxIntensityWithUnit.equals("10度") || maxIntensityWithUnit.equals("11度") ||
                maxIntensityWithUnit.equals("12度")) {
            publicOpinion = "做好舆情监控和引导工作，防止我市出现地震谣传";
        } else {
            publicOpinion = "无";
        }

        System.out.println("7.舆情处置建议：" + publicOpinion);  // 输出相应的响应信息

        //8.人员伤亡和财产损失估算  O31

        String impactMessage = generateImpactMessage(maxIntensityWithUnit);
        System.out.println("8.人员伤亡和财产损失估算：" + impactMessage);  // 输出相应的影响信息

        // 9.特殊时段处置建议  （没有结果就是空的） P31

        //(1)是否特殊日期判断
        // 将整数类型的年、月、日转换为字符串
        String yearStr = String.valueOf(year);  //  B3  String
        String monthStr = String.format("%02d", month);  // 格式化为两位数   //C3  String
        String dayStr = String.format("%02d", day);  // 格式化为两位数   //D3  String

        // 获取日期对应的事件  eg:国庆节   P29
        String events = getEventForDate(yearStr, monthStr, dayStr);

        //（2）开始特殊时段处置建议
        //int maxIntensity  为  B20
        //String maxIntensityWithUnit 加上 "度"的   B20
        // String cityOrState   为  C26
        //String events 为  P29
        // String suggestion 为  B31
        //Double eqMagnitude 为 I3

        String advice = generateAdvice(cityOrState, events, suggestion, maxIntensityWithUnit, eqMagnitude);  //P31
        System.out.println("9.特殊时段处置建议：" + advice);

        System.out.println("*****************************************");

        //----------应急处置建议开始拼接----------------------

        // 假设输入的各个单元格值
        // N31 - publicOpinion
        // M31- disasterMadepublic
        // L31- dangerSource
        // K31- transportation
        // J31- support
        // I31- disasterCollection
        // H31- headquarters
        // G31- measure
        // String P31 = advice


        // 调用处理方法
        String connect = generateSuggestion(advice, measure, headquarters, disasterCollection, support, transportation, dangerSource, disasterMadepublic, publicOpinion);
        System.out.println("**" + result);

        // 处置措施建议字符串
        String cuoshi = String.format(
                "处置措施建议：%s",
                connect
        );

        System.out.println("最终处置措施建议" + cuoshi);


        //***********************************************************************************************


        // 合并所有字符串
        String combinedResult1 = result + fuJinTownResult + panduan + jianyi + cuoshi;
        System.out.println("合并字段完成：" + combinedResult1);
        System.out.println("-----------------------------将要开始进行书写word文档阶段-----------------------");
        WordExporter(title, result, fuJinTownResult, panduan, jianyi, cuoshi, formattedTime, eqName, eqMagnitude, params);
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


    /**
     * 根据总死亡人数、总经济损失和总建筑破坏面积生成结果字符串
     */
    private static String generateResultString(int totalDeath, int injury, double totalEconomicLoss, double totalBuildingDamage) {
        // 处理死亡人数
        String deathResult = "死亡X～X（按±30%浮动）人";
        if (totalDeath > 0) {
//            int lowerDeath = (int) Math.round(totalDeath * 0.7); // 下限：-30%
//            int upperDeath = (int) Math.round(totalDeath * 1.3); // 上限：+30%
            deathResult = "死亡" + totalDeath + "（按±30%浮动）人";
        }

        // 处理死亡人数
        String injuryResult = "重伤X～X（按±30%浮动）人";
        if (injury > 0) {
//            int lowerInjury = (int) Math.round(injury * 0.7); // 下限：-30%
//            int upperInjury = (int) Math.round(injury * 1.3); // 上限：+30%
            injuryResult = "受伤" + injury + "（按±30%浮动）人";
        }

        // 处理建筑破坏面积
        String buildingDamageResult = "房屋损毁X～X（按±30%浮动）间";
        if (totalBuildingDamage > 0) {
//            double lowerBuildingDamage = Math.round(totalBuildingDamage * 0.7 * 100) / 100.0; // 下限：-30%，保留两位小数
//            double upperBuildingDamage = Math.round(totalBuildingDamage * 1.3 * 100) / 100.0; // 上限：+30%，保留两位小数/
            buildingDamageResult = "总建筑破坏面积" + totalBuildingDamage + "（按±30%浮动）平方公里";
        }

        // 处理经济损失
        String economicLossResult = "直接经济损失X～X亿元（按±30%浮动）";
        if (totalEconomicLoss > 0) {
//            double lowerEconomicLoss = Math.round(totalEconomicLoss * 0.7 * 100) / 100.0; // 下限：-30%，保留两位小数
//            double upperEconomicLoss = Math.round(totalEconomicLoss * 1.3 * 100) / 100.0; // 上限：+30%，保留两位小数
            economicLossResult = "直接经济损失" + totalEconomicLoss + "亿元（按±30%浮动）";
        }

        // 拼接最终结果
        return "预估全市" + deathResult + "，" + injuryResult + "，" + buildingDamageResult + "，" + economicLossResult + "。";
    }


    /**
     * 生成应急响应描述
     */
    private static String generateResponse(String cityOrState, String suggestion, int outside, double big, double middle, double small, double eqMagnitude, String countyOrDistrict, String populationDensity) {

        // 初始化响应描述
        StringBuilder response = new StringBuilder();

        // 非雅安市的情况
        if (!"雅安市".equals(cityOrState)) {
            if ("启动市级地震灾害一级应急响应".equals(suggestion)) {
                response.append(String.format(" 2、我市最大地震烈度：%d度；", outside));
                if (big >= 1) {
                    response.append(String.format("3、7度及以上涉及雅安 %d 个县（区）。", (int) big));
                }
            } else if ("启动市级地震灾害二级应急响应".equals(suggestion)) {
                response.append(String.format(" 2、我市最大地震烈度：%d度；", outside));
                if (middle >= 1) {
                    response.append(String.format("3、6度及以上涉及雅安 %d 个县（区）；", (int) middle));
                }
                if (big >= 1) {
                    response.append(String.format("4、7度及以上涉及雅安 %d 个县（区）。", (int) big));
                }
            } else if ("启动市级地震灾害三级应急响应".equals(suggestion)) {
                response.append(String.format(" 2、我市最大地震烈度：%d度；", outside));
                if (small >= 1) {
                    response.append(String.format("3、5度及以上涉及雅安 %d 个县（区）；", (int) small));
                }
                if (middle >= 1) {
                    response.append(String.format("4、6度及以上涉及雅安 %d 个县（区）。", (int) middle));
                }
            } else if ("启动强有感地震应急响应".equals(suggestion)) {
                response.append(String.format(" 2、我市最大地震烈度：%d度；", outside));
                if (small >= 1) {
                    response.append(String.format("3、5度及以上涉及雅安 %d 个县（区）。", (int) small));
                }
            } else if ("启动外地地震应急响应".equals(suggestion)) {
                response.append(String.format(" 2、震级：%.1f级；3、我市最大地震烈度：%d度。", eqMagnitude, outside));
            }
        } else {
            response.append(String.format(" 2、震级：%.1f级；3、震中%s人口密度约%s人/平方公里。", eqMagnitude, countyOrDistrict, populationDensity));
        }

        return response.toString();
    }


    //判断 1.指挥部建议  H31 /headquarters
    public static String earthquakeResponse(String cityOrState, String suggestion, String countyOrDistrict, String earthquakeName) {
        // 1. 不启动应急响应
        if (suggestion.equals("不启动地震应急响应")) {
            return "无";
        }

        // 2. 强有感地震应急响应
        if (suggestion.equals("启动强有感地震应急响应")) {
            if (cityOrState.equals("雅安市")) {
                return "根据工作需要，请市政府指派一位市领导带领相关部门前往" + countyOrDistrict + "指导抗震救灾工作";
            } else {
                return "无";
            }
        }

        // 3. 市级地震灾害三级应急响应
        if (suggestion.equals("启动市级地震灾害三级应急响应")) {
            if (cityOrState.equals("雅安市")) {
                return "市政府负责防震减灾工作副市长组织召开抗震救灾紧急会议；根据工作需要，请市政府指派一位市领导带领市应急管理局、市委宣传部、市卫生健康委、市自然资源和规划局、市水利局、市防震减灾服务中心等部门前往" + countyOrDistrict +
                        "，会同省应急厅、省地震局专家指导、协调、督促抗震救灾工作；立即向省委、省政府和省抗震救灾指挥机构报告震情、灾情和应急处置情况，并持续报告工作进展";
            } else {
                return "市政府负责防震减灾工作副市长组织召开抗震救灾紧急会议；根据工作需要，请市政府指派一位市领导带领市应急管理局、市委宣传部、市卫生健康委、市自然资源和规划局、市水利局、市防震减灾服务中心等部门前往我市受灾较重的县（区），会同省应急厅、省地震局专家指导、协调、督促抗震救灾工作；立即向省委、省政府和省抗震救灾指挥机构报告震情、灾情和应急处置情况，并持续报告工作进展";
            }
        }

        // 4. 市级地震灾害二级应急响应
        if (suggestion.equals("启动市级地震灾害二级应急响应")) {
            return "市政府常务副市长和市政府负责防震减灾工作副市长组织召开抗震救灾紧急会议；成立雅安市" + earthquakeName +
                    "抗震救灾指挥部，建立市政府市长任总指挥，市政府常务副市长和市政府负责防震减灾工作副市长任指挥长的指挥体系；立即向省委、省政府和省抗震救灾指挥机构报告震情、灾情和应急处置情况，并持续报告工作进展；组建市应对地震灾害指挥部后方协调中心";
        }

        // 5. 市级地震灾害一级应急响应
        if (suggestion.equals("启动市级地震灾害一级应急响应")) {
            return "市委、市政府主要领导组织召开抗震救灾紧急会议；成立雅安市" + earthquakeName +
                    "抗震救灾指挥部，建立市委书记和市政府市长任指挥长，市委、市人大、市政府、市政协和雅安军分区等有关领导任副指挥长并兼任有关工作组组长的指挥体系；立即向省委、省政府和省抗震救灾指挥机构报告震情、灾情和应急处置情况，并持续报告工作进展；组建市应对地震灾害指挥部后方协调中心";
        }

        // 6. 外地地震应急响应
        if (suggestion.equals("启动外地地震应急响应")) {
            return "无";
        }

        // 兜底返回值
        return "无";
    }


    //判断 2.灾情收集建议  I31 /disasterCollection
    public static String disasterCollectionFunction(String maxIntensityWithUnit, String maximumIntensityPoint) {
        // 低烈度（0-3度）：无灾情收集
        if (maxIntensityWithUnit.equals("0度") || maxIntensityWithUnit.equals("1度") ||
                maxIntensityWithUnit.equals("2度") || maxIntensityWithUnit.equals("3度")) {
            return "无";
        }

        // 中等烈度（4-6度）：重点关注老旧房屋
        if (maxIntensityWithUnit.equals("4度") || maxIntensityWithUnit.equals("5度") ||
                maxIntensityWithUnit.equals("6度")) {
            return "迅速组织收集核实我市灾情，重点关注" + maximumIntensityPoint + "老旧房屋情况";
        }

        // 高烈度（7-12度）：全面收集灾情并协调技术力量
        if (maxIntensityWithUnit.equals("7度") || maxIntensityWithUnit.equals("8度") ||
                maxIntensityWithUnit.equals("9度") || maxIntensityWithUnit.equals("10度") ||
                maxIntensityWithUnit.equals("11度") || maxIntensityWithUnit.equals("12度")) {
            return "迅速组织收集核实我市灾情情况；协调各技术力量对“信息孤岛”进行灾情分析";
        }

        // 兜底返回值
        return "无";
    }


    //判断  3.应急支援建议  J31 /support
    public static String supportFunction(String cityOrState, Double eqMagnitude, String neighboringCityOrState, String suggestion) {
        // 市级地震灾害 一级 / 二级 应急响应
        if (suggestion.equals("启动市级地震灾害一级应急响应") || suggestion.equals("启动市级地震灾害二级应急响应")) {
            return "迅速组织抢险救援队伍、医疗救护队伍、相应技术人员赶赴灾区开展救援抢险";
        }

        // 市级地震灾害 三级 应急响应
        if (suggestion.equals("启动市级地震灾害三级应急响应")) {
            if (cityOrState.equals("雅安市")) {
                return "视情况组织抢险救援队伍、医疗救护队伍、相应技术人员赶赴灾区开展救援抢险";
            } else if (eqMagnitude < 6.0) {
                return "根据情况组织抢险救援队伍、医疗救护队伍、相应技术人员赶赴我市灾区开展救援抢险";
            } else {
                return "根据情况组织抢险救援队伍、医疗救护队伍、相应技术人员赶赴我市灾区开展救援抢险，"
                        + "迅速联系" + neighboringCityOrState + "和省抗震救灾指挥部，根据需要和我市震情灾情组织必要的抢险救援队伍、"
                        + "医疗救护队伍、相应技术人员和物资对灾区进行支援（我市灾情未完全调查清楚前，至少保留2/3以上队伍和物资）";
            }
        }

        // 强有感地震应急响应
        if (suggestion.equals("启动强有感地震应急响应")) {
            return eqMagnitude < 6.0 ? "无" :
                    "迅速联系" + neighboringCityOrState + "和省抗震救灾指挥部，根据需要和我市震情灾情组织必要的抢险救援队伍、"
                            + "医疗救护队伍、相应技术人员和物资对灾区进行支援（我市灾情未完全调查清楚前，至少保留2/3以上队伍和物资）";
        }

        // 外地地震应急响应
        if (suggestion.equals("启动外地地震应急响应")) {
            return eqMagnitude < 6.0 ? "无" :
                    "迅速联系" + neighboringCityOrState + "和省抗震救灾指挥部，根据需要和我市震情灾情组织必要的抢险救援队伍、"
                            + "医疗救护队伍、相应技术人员和物资对灾区进行支援（我市灾情未完全调查清楚前，至少保留2/3以上队伍和物资）";
        }

        // 不启动应急响应
        if (suggestion.equals("不启动地震应急响应")) {
            return "无";
        }

        return "无"; // 兜底返回值
    }


    //判断  4.交通处置建议  （1） 前置  C7-C14 , E7-E14
    public static String generateDestroy(List<Map.Entry<String, Double>> sortedList) {
        // 确保 sortedList 至少有 8 个元素（对应 E7 到 E14）
        if (sortedList.size() < 8) {
            return "无";
        }

        // 获取最高烈度和最低烈度
        double maxIntensity = sortedList.get(0).getValue();  // E7
        double minIntensity = sortedList.get(7).getValue();  // E14

        // 1. 判断是否是“全境”
        if (maxIntensity > 5 && minIntensity > 5) {
            return "全境";
        }

        // 2. 选取烈度大于 5 的乡镇，并用 "、" 连接
        List<String> validTowns = new ArrayList<>();
        for (Map.Entry<String, Double> entry : sortedList) {
            if (entry.getValue() > 5) {
                validTowns.add(entry.getKey().substring(0, Math.min(3, entry.getKey().length()))); // 取前三个字符
            }
        }

        // 3. 如果没有符合条件的乡镇，则返回 "无"，否则返回拼接的字符串
        return validTowns.isEmpty() ? "无" : String.join("、", validTowns);
    }

    //判断  4.交通处置建议 （2） 前置  G7--G14 , I7---I14
    public static String generateDestroy2(List<Map.Entry<String, Double>> sortedList,
                                          List<Map.Entry<String, Double>> sortedList2,
                                          double maxIntensityWithUnit) {
        //注释：如果最大烈度点在乡镇，而非县城（一般本地地震），最大烈度点选择乡镇。
        //     如果全市都达到最大烈度，那么全境（判断技巧第一个E7等于最大烈度和E14等于最大烈度），
        //     如果县区的最大烈度等于最大烈度，最大烈度为**县、**县，否则为**乡镇。


        // 存储符合最大烈度的县区（只取前三个字符）
        List<String> maxIntensityCounties = new ArrayList<>();
        // 存储符合最大烈度的乡镇（完整名称）
        List<String> maxIntensityTowns = new ArrayList<>();

        // 遍历县区列表，筛选出达到最大烈度的县区
        for (Map.Entry<String, Double> entry : sortedList) {
            if (entry.getValue() == maxIntensityWithUnit) {
                maxIntensityCounties.add(entry.getKey().substring(0, Math.min(3, entry.getKey().length()))); // 取前三个字符
            }
        }

        // 遍历乡镇列表，筛选出达到最大烈度的乡镇
        for (Map.Entry<String, Double> entry : sortedList2) {
            if (entry.getValue() == maxIntensityWithUnit) {
                maxIntensityTowns.add(entry.getKey()); // 乡镇名称完整输出
            }
        }

        // **1. 如果所有县区和乡镇的烈度都等于最大烈度，则返回 "全境"**
        if (maxIntensityCounties.size() == sortedList.size() && maxIntensityTowns.size() == sortedList2.size()) {
            return "全境";
        }

        // **2. 优先选择乡镇作为最大烈度点**
        if (!maxIntensityTowns.isEmpty()) {
            return String.join("、", maxIntensityTowns);
        }

        // **3. 如果没有符合条件的乡镇，则选择县区**
        if (!maxIntensityCounties.isEmpty()) {
            return String.join("、", maxIntensityCounties);
        }

        // **4. 如果没有符合最大烈度的县区和乡镇，则返回 "无"**
        return "无";
    }


    //判断 4.交通处置建议  最终结果   k31/transportation
    public static String transportationFunction(String cityOrState, Double eqMagnitude, String neighboringCityOrState,
                                                String countyOrDistrict, String suggestion,
                                                String category, String destroy, String maximumIntensityPoint) {

        // 处理 "雅安市" 情况
        if (cityOrState.equals("雅安市")) {
            switch (suggestion) {
                case "启动市级地震灾害三级应急响应":
                    return "保障通往震中区域的国省干道畅通，确保救援车辆和物资顺利到达震中";
                case "启动市级地震灾害二级应急响应":
                    return "迅速对通往" + countyOrDistrict + "的国省干道进行必要的交通管制，保障救援车辆、机械和人员优先通行，"
                            + "其他救灾车辆和人员调节通行，根据需要启用直升机起降场地";
                case "启动市级地震灾害一级应急响应":
                    return "迅速对通往" + countyOrDistrict + "的国省干道进行交通管制，保障救援车辆、机械和人员优先通行，"
                            + "其他救灾车辆和人员调节通行，限制无关车辆和人员进入灾区，立即启用直升机起降场地";
                case "启动强有感地震应急响应":
                case "不启动地震应急响应":
                    return "无";
            }
        }

        // 处理 非"雅安市" 情况
        else {
            switch (suggestion) {
                case "启动市级地震灾害三级应急响应":
                    return "保障通往受灾较重区域的国省干道畅通，确保救援车辆和物资顺利到达震中";
                case "启动市级地震灾害二级应急响应":
                    return "迅速对通往" + maximumIntensityPoint + "的国省干道进行必要的交通管制，保障救援车辆、机械和人员优先通行，"
                            + "其他救灾车辆和人员调节通行，根据需要启用直升机起降场地";
                case "启动市级地震灾害一级应急响应":
                    return "迅速对通往" + destroy + "的国省干道进行交通管制，保障救援车辆、机械和人员优先通行，"
                            + "其他救灾车辆和人员调节通行，限制无关车辆和人员进入灾区，立即启用直升机起降场地";
                case "启动强有感地震应急响应":
                case "不启动地震应急响应":
                case "启动外地地震应急响应":
                    if (category.equals("邻近市州地震")) {
                        return (eqMagnitude >= 6.0)
                                ? "保障我市通往" + neighboringCityOrState + "的国省干道畅通，确保救援车辆和物资顺利过境"
                                : "无";
                    } else if (category.equals("外地地震")) {
                        return "无";
                    }
            }
        }

        return "无"; // 兜底返回值
    }


    //判断  5.危险源处置建议  dangerSource/L31
    public static String generateResponse(String maxIntensityWithUnit) {
        String response = "";

        // 判断震级为 7度 到 12度
        if (maxIntensityWithUnit.equals("7度") || maxIntensityWithUnit.equals("8度") ||
                maxIntensityWithUnit.equals("9度") || maxIntensityWithUnit.equals("10度") ||
                maxIntensityWithUnit.equals("11度") || maxIntensityWithUnit.equals("12度")) {
            response += "控制灾区危险源，封锁危险场所，核查我市地灾隐患点、防洪堤坝、危化企业等次生灾害源安全并及时处置";
        }

        // 判断震级为 4度 到 6度
        if (maxIntensityWithUnit.equals("4度") || maxIntensityWithUnit.equals("5度") ||
                maxIntensityWithUnit.equals("6度")) {
            response += "核查我市地灾隐患点、防洪堤坝、危化企业等次生灾害源安全并及时处置";
        }

        // 判断震级为 0度 到 3度
        if (maxIntensityWithUnit.equals("0度") || maxIntensityWithUnit.equals("1度") ||
                maxIntensityWithUnit.equals("2度") || maxIntensityWithUnit.equals("3度")) {
            response += "无";
        }

        // 如果没有有效响应，返回空字符串
        if (response.isEmpty()) {
            return "无";
        }

        return response;
    }


    //判断  8.人员伤亡和财产损失估算   impactMessage /O31的方法
    public static String generateImpactMessage(String maxIntensityWithUnit) {
        // 根据 B20 的值生成相应的提示信息
        switch (maxIntensityWithUnit) {
            case "0度":
            case "1度":
            case "2度":
            case "3度":
                return "不会造成人员伤亡和财产损失";
            case "4度":
                return "一般不会造成人员伤亡和财产损失";
            case "5度":
                return "一般不会造成人员伤亡，但可能会有少量财产损失";
            case "6度":
                return "一般不会造成人员死亡，但可能会有个别人员受伤和少量财产损失";
            case "7度":
                return "一般会造成少量人员伤亡和部分财产损失";
            case "8度":
                return "一般会造成少量人员死亡和大量人员受伤及财产损失";
            case "9度":
            case "10度":
            case "11度":
            case "12度":
                return "一般会造成大量人员伤亡和财产损失";
            default:
                return "未知最大烈度/最大烈度不是整数";
        }
    }

    //判断  9.特殊时段处置建议   advice 前置  判断是否为特殊节日
    public static String getEventForDate(String year, String month, String day) {
        // 事件映射表
        Map<String, Set<String>> eventMap = new HashMap<>();

        // 添加节假日和特殊时段
        addEvent(eventMap, "2023-01", new String[]{"21", "22", "23", "24", "25", "26", "27"}, "春节");
        addEvent(eventMap, "2022-12", new String[]{"31"}, "元旦节");
        addEvent(eventMap, "2023-01", new String[]{"01", "02"}, "元旦节");
        addEvent(eventMap, "2023-01", new String[]{"10", "11", "12", "13", "14"}, "四川两会时段");
        addEvent(eventMap, "2023-03", new String[]{"04", "05", "06", "07", "08", "09", "10", "11", "12", "13"}, "全国两会时段");
        addEvent(eventMap, "2023-04", new String[]{"05"}, "清明节");
        addEvent(eventMap, "2023-04", new String[]{"29", "30"}, "劳动节");
        addEvent(eventMap, "2023-05", new String[]{"01", "02", "03"}, "劳动节");
        addEvent(eventMap, "2023-06", new String[]{"22", "23", "24"}, "端午节");
        addEvent(eventMap, "2023-09", new String[]{"29", "30"}, "中秋节");
        addEvent(eventMap, "2023-10", new String[]{"01", "02", "03", "04", "05", "06"}, "国庆节");

        addEvent(eventMap, "2021-01", new String[]{"28", "29", "30", "31"}, "四川两会时段");
        addEvent(eventMap, "2021-02", new String[]{"01", "02"}, "四川两会时段");
        addEvent(eventMap, "2021-03", new String[]{"04", "05", "06", "07", "08", "09", "10"}, "全国两会时段");
        addEvent(eventMap, "2021-06", new String[]{"07", "08", "09"}, "高考时段");
        addEvent(eventMap, "2021-07", new String[]{"12", "13", "14"}, "雅安中考时段");
        addEvent(eventMap, "2021-08", new String[]{"16", "17", "18", "19"}, "雅安两会时段");

        addEvent(eventMap, "2022-01", new String[]{"01", "02", "03"}, "元旦节");
        addEvent(eventMap, "2022-01", new String[]{"09", "10", "11", "12"}, "雅安两会时段");
        addEvent(eventMap, "2022-01", new String[]{"17", "18", "19", "20", "21"}, "四川两会时段");
        addEvent(eventMap, "2022-01", new String[]{"31"}, "春节");
        addEvent(eventMap, "2022-02", new String[]{"01", "02", "03", "04", "05", "06"}, "春节");
        addEvent(eventMap, "2022-03", new String[]{"04", "05", "06", "07", "08", "09", "10"}, "全国两会时段");
        addEvent(eventMap, "2022-04", new String[]{"03", "04", "05"}, "清明节");
        addEvent(eventMap, "2022-04", new String[]{"30"}, "劳动节");
        addEvent(eventMap, "2022-05", new String[]{"01", "02", "03", "04"}, "劳动节");
        addEvent(eventMap, "2022-06", new String[]{"03", "04", "05"}, "端午节");
        addEvent(eventMap, "2022-09", new String[]{"10", "11", "12"}, "中秋节");
        addEvent(eventMap, "2022-10", new String[]{"01", "02", "03", "04", "05", "06", "07"}, "国庆节");

        addEvent(eventMap, "2024-06", new String[]{"07", "08", "09"}, "高考时段");
        addEvent(eventMap, "2024-06", new String[]{"14", "15", "16"}, "雅安中考时段");

        // 构造 key，查询事件
        String key = year + "-" + month;
        if (eventMap.containsKey(key)) {
            for (String entry : eventMap.get(key)) {
                if (entry.startsWith(day + "|")) {
                    return entry.split("\\|")[1]; // 获取事件名称
                }
            }
        }
        return "";
    }

    //判断  9.特殊时段处置建议  advice 前置 用于存储事件的映射关系
    private static void addEvent(Map<String, Set<String>> eventMap, String key, String[] days, String eventName) {
        eventMap.putIfAbsent(key, new HashSet<>());
        for (String day : days) {
            eventMap.get(key).add(day + "|" + eventName); // 使用 "日|事件" 形式存储
        }
    }


    //判断  9.特殊时段处置建议   advice/P31的方法
    public static String generateAdvice(String cityOrState, String event, String suggestion, String maxIntensity, double eqMagnitude) {
        StringBuilder advice = new StringBuilder();

        // 正值春节假日的判断
        if ("雅安市".equals(cityOrState) && "春节".equals(event) && eqMagnitude > 3.9) {
            advice.append("正值春节假日，应特别关注震中附近景区及人员密集场所情况。");
        }

        boolean b = "启动强有感地震应急响应".equals(suggestion) || "启动市级地震灾害三级应急响应".equals(suggestion) ||
                "启动市级地震灾害二级应急响应".equals(suggestion) || "启动市级地震灾害一级应急响应".equals(suggestion);

        if (!"雅安市".equals(cityOrState) && "春节".equals(event) && b) {
            advice.append("正值春节假日，应特别关注我市强有感以上区域景区及人员密集场所情况。");
        }

        // 正值冠状病毒疫情高发期的判断   （时间时间，没有判断出来）
        if ("雅安市".equals(cityOrState) && "冠状病毒疫情高发期".equals(event) && eqMagnitude > 3.9) {
            advice.append("正值新冠肺炎防控期，应特别关注我市市区、震中城区和震中附近乡镇人员动向情况，提醒应急处置人员和户外避险群众做好个人防护。");
        }

        if (!"雅安市".equals(cityOrState) && "冠状病毒疫情高发期".equals(event) && (
                "4度".equals(maxIntensity) || "5度".equals(maxIntensity) || "6度".equals(maxIntensity) ||
                        "7度".equals(maxIntensity) || "8度".equals(maxIntensity) || "9度".equals(maxIntensity) ||
                        "10度".equals(maxIntensity) || "11度".equals(maxIntensity) || "12度".equals(maxIntensity))) {
            advice.append("正值新冠肺炎防控期，应特别关注我市有感以上区域主要乡镇、街道人员动向情况，提醒应急处置人员和户外避险群众做好个人防护。");
        }

        // 正值清明节假日的判断
        if ("雅安市".equals(cityOrState) && "清明节".equals(event) && eqMagnitude > 3.9) {
            advice.append("正值清明节假日，应特别关注震中附近景区及人员密集场所情况。");
        }

        if (!"雅安市".equals(cityOrState) && "清明节".equals(event) && b) {
            advice.append("正值清明节假日，应特别关注我市强有感以上区域景区及人员密集场所情况。");
        }

        // 正值劳动节假日的判断
        if ("雅安市".equals(cityOrState) && "劳动节".equals(event) && eqMagnitude > 3.9) {
            advice.append("正值劳动节假日，应特别关注震中附近景区及人员密集场所情况。");
        }

        if (!"雅安市".equals(cityOrState) && "劳动节".equals(event) && b) {
            advice.append("正值劳动节假日，应特别关注我市强有感以上区域景区及人员密集场所情况。");
        }

        // 其他节假日及时段的判断
        // 以类似的方式继续添加更多的判断条件

        // 返回最终的建议信息
        return advice.toString();
    }

    //处置措施建议
    public static String generateSuggestion(String P31, String... values) {
        //--------------------开始连接--------------------------------
        StringBuilder suggestion = new StringBuilder();
        boolean hasValidMeasure = false; // 记录是否有有效措施
        int validCount = 0; // 记录有效数据的数量

        // 处理 G31-N31 的内容
        for (int i = 0; i < values.length; i++) {
            if (!"无".equals(values[i])) {
                if (validCount > 0) {
                    suggestion.append("；"); // 多个有效数据之间用分号分隔
                }
                suggestion.append(values[i]);
                hasValidMeasure = true; // 发现有效措施
                validCount++; // 有效数据数量加1
            }
        }

        // 若没有有效措施，则直接返回 P31
        if (!hasValidMeasure) {
            return P31;
        }

        // 若有有效措施，添加句号并拼接 P31
        suggestion.append("。"); // 最后一个有效数据后添加句号
        return suggestion.append(P31).toString();
    }

    //设置样式，生成文档
    private void WordExporter(String title, String result, String fuJinTownResult, String panduan, String jianyi, String cuoshi, String formattedTime, String eqName, Double eqMagnitude, EqEventTriggerDTO params) throws IOException {

        System.out.println("开始写word文档");

        // 创建一个 XWPFDocument 对象
        XWPFDocument document = new XWPFDocument();

        // 第一行：对内掌握，黑体三号，右对齐
        XWPFParagraph firstParagraph = document.createParagraph();
        firstParagraph.setAlignment(ParagraphAlignment.RIGHT); // 右对齐
        firstParagraph.setSpacingBefore((int) (16 * 20 * 0.5));//设置段前间距为0
        firstParagraph.setSpacingAfter(0); // 段后0行距
        XWPFRun firstRun = firstParagraph.createRun();
        firstRun.setText("对内掌握");
        firstRun.setFontFamily("黑体");  // 黑体
        firstRun.setFontSize(16);  // 三号字体

        // 空3行，黑体16号字体，段落居左
        for (int i = 0; i < 3; i++) {
            XWPFParagraph emptyParagraph = document.createParagraph();
            emptyParagraph.setAlignment(ParagraphAlignment.RIGHT); // 段落居右
            emptyParagraph.setSpacingBetween(1.85);// 设置1.85倍行距
            emptyParagraph.setSpacingBefore(0); // 段前0倍行距
            emptyParagraph.setSpacingAfter(0); // 段后0行距

            // 设置字体样式
            XWPFRun newRun = emptyParagraph.createRun();
            newRun.setText(" ");
            newRun.setFontFamily("黑体"); // 字体
            newRun.setFontSize(16); // 字号16
        }


        // 第二行：地震应急辅助决策信息，居中，方正小标宋简体，44号，红色
        XWPFParagraph secondParagraph = document.createParagraph();
        secondParagraph.setAlignment(ParagraphAlignment.CENTER); // 居中
        secondParagraph.setSpacingAfter(0); // 段后0行距
        XWPFRun secondRun = secondParagraph.createRun();
        secondRun.setText("地震应急辅助决策信息");
        secondRun.setFontFamily("方正小标宋简体");  // 方正小标宋简体
        secondRun.setFontSize(44);  // 44号字体
        secondRun.setColor("FF0000");  // 红色

        // 空1行，仿宋_GB2312的16号字体，段落居中
        XWPFParagraph emptyParagraphSong = document.createParagraph();
        emptyParagraphSong.setAlignment(ParagraphAlignment.CENTER); // 段落居中
        emptyParagraphSong.setSpacingBetween(2);// 设置2倍行距
        emptyParagraphSong.setSpacingBefore(0); // 段前0倍行距
        emptyParagraphSong.setSpacingAfter(0); // 段后0行距
        // 设置字体样式
        XWPFRun newRunSong = emptyParagraphSong.createRun();
        newRunSong.setText(" ");
        newRunSong.setFontFamily("仿宋_GB2312"); // 字体
        newRunSong.setFontSize(16); // 字号16


//        // 第三行：雅安市应急管理局 + 10个空格 + formattedTime
//        XWPFParagraph thirdParagraph = document.createParagraph();
//        thirdParagraph.setAlignment(ParagraphAlignment.LEFT); // 左对齐
//        XWPFRun thirdRun = thirdParagraph.createRun();
//        thirdRun.setText("雅安市应急管理局" + "          " + formattedTime);  // 10个空格 + formattedTime

        // 第三行：雅安市应急管理局 + 15个空格（用String.format控制空格数量）+ formattedTime
        // 注意：这里使用String.format来确保空格数量正确，但实际上在Word中，不同字体的空格宽度可能不同
        // 因此，这里的15个空格可能不是视觉上的精确15个空格宽度
        // 如果需要精确控制，可以考虑使用制表符或表格布局
        XWPFParagraph thirdParagraph = document.createParagraph();
        thirdParagraph.setAlignment(ParagraphAlignment.CENTER); // 段落居中（根据您的要求）
        thirdParagraph.setSpacingAfter(0); // 段后0行距
        XWPFRun thirdRun1 = thirdParagraph.createRun();
        thirdRun1.setText("雅安市应急管理局");
        thirdRun1.setFontFamily("仿宋_GB2312");  // 仿宋_GB2312字体
        thirdRun1.setFontSize(16);  // 三号字体

        // 添加空格（这里使用String.format确保空格数量，但注意视觉宽度可能不同）
        // 如果需要精确控制空格宽度，可以考虑使用制表符或其他方法
        // 添加空格（使用仿宋_GB2312字体，三号字体）
        String spaces = "               "; // 15个空格
        XWPFRun run = thirdParagraph.createRun(); // 创建一个新的Run
        run.setText(spaces); // 设置空格内容
        run.setFontFamily("仿宋_GB2312"); // 设置字体为仿宋_GB2312
        run.setFontSize(16); // 设置字号为三号字体（对应Word中的三号字体大小为16）


        XWPFRun thirdRun2 = thirdParagraph.createRun();
        thirdRun2.setText(formattedTime);
        thirdRun2.setFontFamily("仿宋_GB2312");  // 仿宋_GB2312字体（虽然这里可能继承自上一个run，但明确设置以确保一致性）
        thirdRun2.setFontSize(16);  // 三号字体

        // 添加模拟红线的文本行（这里使用加粗的下划线文本模拟，但注意这不是真正的红线）
        // 如果需要真正的红线（如矩形框），则需要使用Apache POI的绘图功能
        XWPFParagraph redLineParagraph = document.createParagraph();
        // 设置段落居中对齐
        redLineParagraph.setAlignment(ParagraphAlignment.CENTER); // 这行代码在您的原始代码中已经存在，但为了完整性再次提及
        redLineParagraph.setSpacingBetween(1.2);// 设置2倍行距
        redLineParagraph.setSpacingAfter(0); // 段后0行距
        XWPFRun redLineRun = redLineParagraph.createRun();
        redLineRun.setText("________________________________________"); // 下划线，长度根据需要调整
        redLineRun.setFontFamily("华文行楷");  // 华文行楷字体
        redLineRun.setFontSize(22);  // 2号字体（调整为22以接近Word中的2号）
        redLineRun.setBold(true);  // 加粗
        redLineRun.setColor("FF0000");  // 红色
        // 注意：这里的下划线是通过文本模拟的，如果需要真正的线条，请使用Apache POI的绘图功能


        // 空1行，华文行楷22号字体，段落居中
        XWPFParagraph emptyParagraphHua = document.createParagraph();
        emptyParagraphHua.setSpacingBetween(1);// 设置1倍行距
        emptyParagraphHua.setAlignment(ParagraphAlignment.CENTER); // 段落居中
        emptyParagraphHua.setSpacingBefore((int) (16 * 20 * 1.1)); // 段前1.1倍行距
        emptyParagraphHua.setSpacingAfter(0); // 段后0行距
        // 设置字体样式
        XWPFRun newRunHua = emptyParagraphHua.createRun();
        newRunHua.setText(" ");
        newRunHua.setFontFamily("华文行楷"); // 字体
        newRunHua.setFontSize(22); // 字号22


        // 添加标题段落
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER); // 段落居中
        titleParagraph.setSpacingAfter(0); // 段后0行距

        // 创建运行来设置标题文本
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(title);
        titleRun.setFontFamily("方正小标宋简体");  // 字体：方正小标宋简体
        titleRun.setFontSize(22);  // 字号：二号（在Apache POI中，字号是以半磅为单位的，所以二号大约是22*2=44磅的一半，即22）


        // 创建一个新的段落
        XWPFParagraph decisionInfoParagraph = document.createParagraph();
        decisionInfoParagraph.setAlignment(ParagraphAlignment.CENTER);        // 设置段落居中对齐
        emptyParagraphHua.setSpacingBefore((int) (16 * 20)); // 段前0.75倍行距
        decisionInfoParagraph.setSpacingAfter(0); // 段后0行距
        XWPFRun decisionInfoRun = decisionInfoParagraph.createRun();        // 创建一个运行来添加文本
        decisionInfoRun.setText("（辅助决策信息二）");
        decisionInfoRun.setFontFamily("方正小标宋简体");// 设置字体
        decisionInfoRun.setFontSize(22);     // 设置字号为二号（对应22磅）

        // 空1行
        XWPFParagraph emptyParagraph = document.createParagraph();
        emptyParagraph.setAlignment(ParagraphAlignment.RIGHT); // 段落居右
        emptyParagraph.setSpacingBetween(1.45);// 设置1.45倍行距
        // 设置字体样式
        XWPFRun newRunHei = emptyParagraph.createRun();
        newRunHei.setText(" ");
        newRunHei.setFontFamily("黑体"); // 字体
        newRunHei.setFontSize(16); // 字号16


        //*****************************************开始填入自动生成内容****************************************************************

        // 创建一个新的段落--------第一个标题
        XWPFParagraph earthquakeInfoParagraph = document.createParagraph();
        earthquakeInfoParagraph.setAlignment(ParagraphAlignment.BOTH);  // 设置段落为两端对齐
        //设置段落格式：单倍行距，段前段后为0
        earthquakeInfoParagraph.setSpacingBetween(1.5);// 设置1.5倍行距
        earthquakeInfoParagraph.setSpacingBefore(0);//设置段前间距为0
        earthquakeInfoParagraph.setSpacingAfter(0);//设置段后间距为0
        XWPFRun earthquakeInfoRun = earthquakeInfoParagraph.createRun();
        earthquakeInfoRun.setText("    " + "一、震区基本情况");  //前面有4个空格
        earthquakeInfoRun.setFontFamily("黑体");
        earthquakeInfoRun.setFontSize(16); // 设置字号为三号（对应16磅）


        // 第一段正文内容
        XWPFParagraph oneText = document.createParagraph();
        oneText.setAlignment(ParagraphAlignment.BOTH); // 两端对齐

        // 设置首行缩进为2字符宽
//        int indentSize = 16 * 2 * 20; // 每字符宽按字号16磅计算，2字符宽
//        oneText.setIndentationFirstLine(indentSize); // 设置首行缩进

        // 设置段前间距为0倍行距，段后间距为0
        oneText.setSpacingBetween(1.5);// 设置1.2倍行距
        oneText.setSpacingBefore(0); // 段前0倍行距
        oneText.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun = oneText.createRun();
        newRun.setText("    " + result); // 设置文本内容
        newRun.setFontFamily("仿宋_GB2312"); // 字体
        newRun.setFontSize(16); // 三号字体（16磅）


        // 创建一个新的段落--------第二个标题
        XWPFParagraph disasterInfoParagraph = document.createParagraph(); // 假设document是您的XWPFDocument对象
        disasterInfoParagraph.setAlignment(ParagraphAlignment.BOTH);         // 设置段落为两端对齐
        //设置段落格式：单倍行距，段前段后为0
        disasterInfoParagraph.setSpacingBetween(1.45);// 设置1.5倍行距
        disasterInfoParagraph.setSpacingBefore(0);//设置段前间距为0
        disasterInfoParagraph.setSpacingAfter(0);//设置段后间距为0
        XWPFRun disasterInfoRun = disasterInfoParagraph.createRun();
        disasterInfoRun.setText("    " + "二、雅安震情灾情");
        disasterInfoRun.setFontFamily("黑体");
        disasterInfoRun.setFontSize(16); // 设置字号为16磅（注意：三号字号通常对应16磅，但这里直接使用了16磅）


        // 第二段正文内容
        XWPFParagraph oneText2 = document.createParagraph();
        oneText2.setAlignment(ParagraphAlignment.BOTH); // 两端对齐

        // 设置首行缩进为2字符宽
//        int indentSize2 = 16 * 2 * 20; // 每字符宽按字号16磅计算，2字符宽
//        oneText2.setIndentationFirstLine(indentSize2); // 设置首行缩进

        // 设置段前间距为1.5倍行距，段后间距为0
        oneText2.setSpacingBetween(1.5);// 设置1.2倍行距
        oneText2.setSpacingBefore(0); // 段前0倍行距
//        oneText2.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距
        oneText2.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun2 = oneText2.createRun();
        newRun2.setText("    " + fuJinTownResult); // 设置文本内容
        newRun2.setFontFamily("仿宋_GB2312"); // 字体
        newRun2.setFontSize(16); // 三号字体（16磅）
        newRun2.setColor("FF0000"); // 字体颜色：红色


        // 创建一个新的段落--------第三个标题
        XWPFParagraph emergencyResponseParagraph = document.createParagraph();
        emergencyResponseParagraph.setAlignment(ParagraphAlignment.BOTH);       // 设置段落为两端对齐
        //设置段落格式：单倍行距，段前段后为0
        emergencyResponseParagraph.setSpacingBetween(1.75);// 设置1.75倍行距
        emergencyResponseParagraph.setSpacingBefore(0);//设置段前间距为0
        emergencyResponseParagraph.setSpacingAfter(0);//设置段后间距为0
        XWPFRun emergencyResponseRun = emergencyResponseParagraph.createRun();
        emergencyResponseRun.setText("    " + "三、应急处置建议");
        emergencyResponseRun.setFontFamily("黑体");
        emergencyResponseRun.setFontSize(16); // 设置字号为16磅（注意：三号字号通常对应16磅,这里直接指定了磅值）

        // 第三段正文内容
        XWPFParagraph oneText3 = document.createParagraph();
        oneText3.setAlignment(ParagraphAlignment.BOTH); // 两端对齐

        // 设置首行缩进为2字符宽
//        int indentSize3 = 16 * 2 * 20; // 每字符宽按字号16磅计算，2字符宽
//        oneText3.setIndentationFirstLine(indentSize3); // 设置首行缩进

        // 设置段前间距为1.5倍行距，段后间距为0
        oneText3.setSpacingBetween(1.5);// 设置1.5倍行距
        oneText3.setSpacingBefore(0); // 段前0倍行距
        // oneText3.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距（注释掉的代码保持一致）
        oneText3.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun3 = oneText3.createRun();
        newRun3.setText("    " + panduan); // 设置文本内容
        newRun3.setFontFamily("仿宋_GB2312"); // 字体
        newRun3.setFontSize(16); // 三号字体（16磅）


        // 第四段正文内容
        XWPFParagraph oneText4 = document.createParagraph();
        oneText4.setAlignment(ParagraphAlignment.BOTH); // 两端对齐

        // 设置首行缩进为2字符宽
//        int indentSize4 = 16 * 2 * 20; // 每字符宽按字号16磅计算，2字符宽
//        oneText4.setIndentationFirstLine(indentSize4); // 设置首行缩进

        // 设置段前间距为1.5倍行距，段后间距为0
        oneText4.setSpacingBetween(1.5);// 设置1.5倍行距
        oneText4.setSpacingBefore(0); // 段前0倍行距
        // oneText4.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距（注释掉的代码保持一致）
        oneText4.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun4 = oneText4.createRun();
        newRun4.setText("    " + jianyi); // 设置文本内容
        newRun4.setFontFamily("仿宋_GB2312"); // 字体
        newRun4.setFontSize(16); // 三号字体（16磅）


        // 第五段正文内容
        XWPFParagraph oneText5 = document.createParagraph();
        oneText5.setAlignment(ParagraphAlignment.BOTH); // 两端对齐

        // 设置首行缩进为2字符宽
//        int indentSize5 = 16 * 2 * 20; // 每字符宽按字号16磅计算，2字符宽
//        oneText5.setIndentationFirstLine(indentSize5); // 设置首行缩进

        // 设置段前间距为1.5倍行距，段后间距为0
        oneText5.setSpacingBetween(1.2);// 设置1.2倍行距
        oneText5.setSpacingBefore(0); // 段前0倍行距
        // oneText5.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距（注释掉的代码保持一致）
        oneText5.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun5 = oneText5.createRun();
        newRun5.setText("    " + cuoshi); // 设置文本内容
        newRun5.setFontFamily("仿宋_GB2312"); // 字体
        newRun5.setFontSize(16); // 三号字体（16磅）


        //*****************************************填入自动生成内容结束****************************************************************

        // 空1行，仿宋_GB2312的16号字体，段落居中
        XWPFParagraph emptyParagraphSong1 = document.createParagraph();
        emptyParagraphSong1.setAlignment(ParagraphAlignment.LEFT); // 段落居中
        emptyParagraphSong1.setSpacingBetween(2.5);// 设置2.5倍行距
        emptyParagraphSong1.setSpacingAfter(0); // 段后0行距
        // 设置字体样式
        XWPFRun newRunSong1 = emptyParagraphSong1.createRun();
        newRunSong1.setText(" ");
        newRunSong1.setFontFamily("仿宋_GB2312"); // 字体
        newRunSong1.setFontSize(16); // 字号16


        // 第六段正文内容
        XWPFParagraph oneText6 = document.createParagraph();
        oneText6.setAlignment(ParagraphAlignment.BOTH); // 两端对齐

        // 设置首行缩进为2字符宽
//        int indentSize6 = 16 * 2 * 20; // 每字符宽按字号16磅计算，2字符宽
//        oneText6.setIndentationFirstLine(indentSize6); // 设置首行缩进

        // 设置段前间距为1.5倍行距，段后间距为0
        oneText6.setSpacingBetween(1.2);// 设置1.2倍行距
        oneText6.setSpacingBefore(0);// 段前0倍行距
        // oneText6.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距（注释掉的代码保持一致）
        oneText6.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun6 = oneText6.createRun();
        newRun6.setText("    " + "附件：地震影响场分布图"); // 设置文本内容
        newRun6.setFontFamily("仿宋_GB2312"); // 字体
        newRun6.setFontSize(16); // 三号字体（16磅）

        // 空1行，仿宋_GB2312的16号字体，段落居中
        XWPFParagraph emptyParagraphSong2 = document.createParagraph();
        emptyParagraphSong2.setAlignment(ParagraphAlignment.LEFT); // 段落居中
        emptyParagraphSong2.setSpacingBetween(2.75);// 设置2.75倍行距
        emptyParagraphSong2.setSpacingAfter(0); // 段后0行距
        // 设置字体样式
        XWPFRun newRunSong2 = emptyParagraphSong2.createRun();
        newRunSong2.setText(" ");
        newRunSong2.setFontFamily("仿宋_GB2312"); // 字体
        newRunSong2.setFontSize(16); // 字号16

        // 第七段正文内容
        XWPFParagraph oneText7 = document.createParagraph();
        oneText7.setAlignment(ParagraphAlignment.BOTH); // 两端对齐

        // 设置首行缩进为2字符宽
//        int indentSize7 = 16 * 2 * 20; // 每字符宽按字号16磅计算，2字符宽
//        oneText7.setIndentationFirstLine(indentSize7); // 设置首行缩进

        // 设置段前间距为1.5倍行距，段后间距为0
        oneText7.setSpacingBetween(1.25);// 设置1.25倍行距
        oneText7.setSpacingBefore(0); // 段前0倍行距
// oneText7.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距（注释掉的代码保持一致）
        oneText7.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun7 = oneText7.createRun();
        newRun7.setText("    " + "雅安市应急管理局值班电话：0835-2220001，卫星电话：17406544731。"); // 设置文本内容
        newRun7.setFontFamily("仿宋_GB2312"); // 字体
        newRun7.setFontSize(16); // 三号字体（16磅）

        // 空1行，仿宋_GB2312的16号字体，段落居中
        XWPFParagraph emptyParagraphSong3 = document.createParagraph();
        emptyParagraphSong3.setAlignment(ParagraphAlignment.LEFT); // 段落居中
        // 模拟单倍行距（一般情况下1倍行距是240 TWIPS，适当设置行距）
        emptyParagraphSong3.setSpacingBetween(2.1);// 设置2.1倍行距
        // 设置字体样式
        XWPFRun newRunSong3 = emptyParagraphSong3.createRun();
        newRunSong3.setText(" ");
        newRunSong3.setFontFamily("仿宋_GB2312"); // 字体
        newRunSong3.setFontSize(16); // 字号16


        // 第八段正文内容
        XWPFParagraph oneText8 = document.createParagraph();
        oneText8.setAlignment(ParagraphAlignment.BOTH); // 两端对齐

        // 设置首行缩进为2字符宽
//        int indentSize8 = 16 * 2 * 20; // 每字符宽按字号16磅计算，2字符宽
//        oneText8.setIndentationFirstLine(indentSize8); // 设置首行缩进

        // 设置段前间距为1.5倍行距，段后间距为0
        oneText8.setSpacingBetween(1);// 设置1.2倍行距
        oneText8.setSpacingBefore(0); // 段前0倍行距
// oneText8.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距（注释掉的代码保持一致）
        oneText8.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun8 = oneText8.createRun();
        newRun8.setText("    " + "（本期送：市政府领导、局领导、局机关各科室。）"); // 设置文本内容
        newRun8.setFontFamily("仿宋_GB2312"); // 字体
        newRun8.setFontSize(16); // 三号字体（16磅）


        // 空1行，仿宋_GB2312的16号字体，段落居中
        XWPFParagraph emptyParagraphSong4 = document.createParagraph();
        emptyParagraphSong4.setAlignment(ParagraphAlignment.LEFT); // 段落居左
        // 模拟单倍行距（一般情况下1倍行距是240 TWIPS，适当设置行距）
        emptyParagraphSong4.setSpacingBefore(240); // 设置段前行距为240 TWIPS（1倍行距）
        emptyParagraphSong4.setSpacingAfter(240);  // 设置段后行距为240 TWIPS（1倍行距）

        // 启用孤行控制
        emptyParagraphSong4.setWordWrap(true); // 启用孤行控制

        // 设置字体样式
        XWPFRun newRunSong4 = emptyParagraphSong4.createRun();
        newRunSong4.setText(" "); // 设置文本内容
        newRunSong4.setFontFamily("仿宋_GB2312"); // 字体
        newRunSong4.setFontSize(16); // 字号16

        //-----------------------------第二页-----段前分页-------------------------------

        // 第九段正文内容
        XWPFParagraph oneText9 = document.createParagraph();
        oneText9.setAlignment(ParagraphAlignment.BOTH); // 两端对齐

        // 设置段前分页
        oneText9.setPageBreak(true); // 设置段前分页

        // 设置首行缩进为2字符宽
//        int indentSize9 = 16 * 2 * 20; // 每字符宽按字号16磅计算，2字符宽
//        oneText9.setIndentationFirstLine(indentSize9); // 设置首行缩进

        // 设置段前间距为1.5倍行距，段后间距为0
        oneText9.setSpacingBetween(1.2);// 设置1.2倍行距
        oneText9.setSpacingBefore(0); // 段前0倍行距
// oneText9.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距（注释掉的代码保持一致）
        oneText9.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun9 = oneText9.createRun();
        newRun9.setText("附件"); // 设置文本内容
        newRun9.setFontFamily("黑体"); // 字体
        newRun9.setFontSize(16); // 三号字体（16磅）


        // 第十段正文内容
        XWPFParagraph oneText10 = document.createParagraph();
        oneText10.setAlignment(ParagraphAlignment.CENTER); // 居中对齐

        // 设置段前间距为1.5倍行距，段后间距为0
        oneText10.setSpacingBetween(1.2);// 设置1.2倍行距
        oneText10.setSpacingBefore(0); // 段前1.5倍行距
// oneText10.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距（注释掉的代码保持一致）
        oneText10.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun10 = oneText10.createRun();
        newRun10.setText("地震影响场分布图"); // 设置文本内容
        newRun10.setFontFamily("方正小标宋简体"); // 字体
        newRun10.setFontSize(20); // 20号

        // 空1行，仿宋_GB2312的16号字体，段落居中
        XWPFParagraph emptyParagraphSong5 = document.createParagraph();
        emptyParagraphSong5.setAlignment(ParagraphAlignment.CENTER); // 段落居左
        // 模拟单倍行距（一般情况下1倍行距是240 TWIPS，适当设置行距）
        emptyParagraphSong5.setSpacingBefore(240); // 设置段前行距为240 TWIPS（1倍行距）
        emptyParagraphSong5.setSpacingAfter(240);  // 设置段后行距为240 TWIPS（1倍行距）

        // 设置字体样式
        XWPFRun newRunSong5 = emptyParagraphSong4.createRun();
        newRunSong5.setText(" "); // 设置文本内容
        newRunSong5.setFontFamily("仿宋_GB2312"); // 字体
        newRunSong5.setFontSize(16); // 字号16


        //-----------------------------第二页-----结束-------------------------------

        System.out.println("写word文档结束");

        // 构造文件路径
        String fileName = formattedTime + eqName + "发生" + eqMagnitude + "级地震（辅助决策信息二）.docx";
//        String filePath = "C:/Users/Smile/Desktop/" + fileName;
//        String filePath = "D:/桌面夹/桌面/demo/" + fileName;
//        String filePath = "/data/image" + fileName;
        // 查询灾情报告图片的存储方式 D:\EqProduct\209c053a-44ad-4924-95b3-086dfce0b57e\1\本地产品\灾情报告

        String filePath = Constants.PROMOTION_DOWNLOAD_PATH +
                "/EqProduct/" + params.getEvent()
                + "/1/本地产品/灾情报告/"
                + fileName;
        // 设置页面边距
        setPageMargins(document,filePath);

        // 写入文件
        writeToDocument(document, filePath);
    }


    //设置文件页边距，纸张大小
    public void setPageMargins(XWPFDocument document,String filePath) throws IOException {
        // 获取页面属性
        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        CTPageMar pageMar = sectPr.addNewPgMar();

        // 设置页面边距（单位为 TWIPS，1厘米 ≈ 567 TWIPS）
        pageMar.setTop(BigInteger.valueOf((long) (3.7 * 567)));    // 上边距 3.7厘米
        pageMar.setBottom(BigInteger.valueOf((long) (3.5 * 567))); // 下边距 3.5厘米
        pageMar.setLeft(BigInteger.valueOf((long) (2.8 * 567)));   // 左边距 2.8厘米
        pageMar.setRight(BigInteger.valueOf((long) (2.6 * 567)));  // 右边距 2.6厘米

        // 设置装订线为 0厘米（可选）
        pageMar.setGutter(BigInteger.valueOf(0));

        // 设置纸张方向为纵向并设置A4大小
        CTPageSz pageSize = sectPr.getPgSz();
        if (pageSize == null) {
            pageSize = sectPr.addNewPgSz();
        }
        pageSize.setOrient(STPageOrientation.PORTRAIT); // 纵向
        pageSize.setW(BigInteger.valueOf((long) (21 * 567))); // 宽度 21厘米
        pageSize.setH(BigInteger.valueOf((long) (29.7 * 567))); // 高度 29.7厘米


        //         保存修改
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            if (parentDir.mkdirs()) {
                document.getDocument().save(file);

            } else {
                System.err.println("目录创建失败: " + parentDir.getAbsolutePath());
                return;
            }
        }

    }


    public void writeToDocument(XWPFDocument document, String filePath) {

        // 创建父目录
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            if (parentDir.mkdirs()) {
                System.out.println("目录创建成功: " + parentDir.getAbsolutePath());
            } else {
                System.err.println("目录创建失败: " + parentDir.getAbsolutePath());
                return;
            }
        }

        // 写入文件
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            document.write(out);
            System.out.println("文件写入成功: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}


