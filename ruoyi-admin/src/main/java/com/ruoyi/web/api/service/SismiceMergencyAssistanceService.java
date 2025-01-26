package com.ruoyi.web.api.service;


import com.ruoyi.system.domain.dto.EqEventTriggerDTO;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.mapper.*;
import lombok.SneakyThrows;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;


import java.math.BigInteger;

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


    @SneakyThrows
    public void file(EqEventTriggerDTO params, String eqqueueId){
        System.out.println("前端构建文本传的参数" + params);
        String eqName = params.getEqName();  //名字
        String eqTime = params.getEqTime();  //时间
        String eqAddr = params.getEqAddr();  //地址
        Double latitude = params.getLatitude();  //纬度
        Double longitude = params.getLongitude();  //经度
        Double eqMagnitude = params.getEqMagnitude();  //震级
        Double eqDepth = params.getEqDepth();  //深度



        //*****************************************************
        //---------文档标题----------
        eqName = eqName.replace("undefined", "");

        String title = eqName + "发生" + eqMagnitude + "级地震";

        //*****************************************************

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
        String lushan ; // 假设数据来自外部输入
        String wenchuan ;
        String jiuzhaigou;
        String changning ;
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


        //*****************************************************


        //---------时间------------
        // 创建 SimpleDateFormat 对象，用于解析原始时间字符串
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 将输入的时间字符串解析为 Date 对象
        Date parsedDate = inputFormat.parse(eqTime);

        // 1.获取月份和日期
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy"); // 设置年份格式

        SimpleDateFormat monthFormat = new SimpleDateFormat("M"); // 月份，不补零
        SimpleDateFormat dayFormat = new SimpleDateFormat("d");   // 日期，不补零

        String year = yearFormat.format(parsedDate); // 格式化为年份字符串

        String month = monthFormat.format(parsedDate); // 获取月份
        String day = dayFormat.format(parsedDate);     // 获取日期

        // 拼接成所需格式
        String monthDate = month + "·" + day;


        // 2.创建 SimpleDateFormat 对象，用于格式化为目标格式
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM月dd日HH时mm分");

        // 格式化 Date 对象为目标格式的字符串
        String formattedTime = outputFormat.format(parsedDate);



        // --------遍历查询雅安市人口密度----------------
        List<YaanResidentPopulationDensity> density = yaanResidentPopulationDensityMapper.selectList(null);

        // 用于存储匹配到的人口密度
        String populationDensity = "没有该地人口密度信息";
        String countyOrDistrict = "县/区";
        // 遍历查询匹配的 cityAndCounty 字段
        for (YaanResidentPopulationDensity populationData : density) {
            if (eqName != null && eqName.contains(populationData.getCityAndCounty())) {
                populationDensity = populationData.getPopulationDensity();
                countyOrDistrict = populationData.getCountyOrDistrict();
                break; // 找到匹配项后，停止遍历
            }
        }


        System.out.println("人口密度：" + populationDensity);
        System.out.println("县/区：" + countyOrDistrict);  // 输出：泸定县

        String newCountyOrDistrict = " " ;
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

        //--------- 构建基础描述----------



        String result;

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
        System.out.println(result);

        //*****************************************************
        //生成文档后，相关人员自己填写的

        String fuJinTownResult = "预估全市死亡X～X（按±30%浮动）人，重伤X～X（按±30%浮动）人，房屋损毁X～X（按±30%浮动）间，直接经济损失X～X亿元（按±30%浮动）。";
        System.out.println(fuJinTownResult);

        //*****************************************************

        //------------- 定义 类别(category) 变量-----------
        String category;

        // 判断 eqName 对应的字段
        if (eqName.contains("雅安市")) {
            category = "雅安本地地震";
        } else if (eqName.contains("阿坝州") || eqName.contains("甘孜州") || eqName.contains("眉山市") ||
                eqName.contains("成都市") || eqName.contains("凉山州") || eqName.contains("乐山市")) {
            category = "邻近市州地震";
        } else {
            category = "外地地震";  // 其他情况归为外地地震   //？？？
        }

        System.out.println( "属于哪个位置的地震（category）:"+ category);
        //------------判断------------------

        int maxIntensity = 0;   //  最大烈度 初始为0

        //如果是不是雅安市内的
        double   big = 0; //外地地震雅安最大烈度8度及以上时，筛选7度及以上县个数
        double   middle = 0; //外地地震雅安最大烈度7度及以上时，筛选6度、7度县个数
        double   small = 0;  //外地地震雅安最大烈度6度及以上时，筛选5度、6度县个数



        //震中附近乡镇计算
        if (eqName.contains("四川")) {

            if (eqAddr.contains("雅安市")) {
                //显示文字，震中距离雅安市边界约distance公里，就将上传的经纬度latitude, longitude和雅安市行政边界表yaan_administrative_boundary计算每个震中距离找出最小值

                System.out.println( "------------------eqAddr内有雅安市，进入雅安市的烈度计算---------------------:"+ eqAddr);
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
                            double distanceToCountyTown = calculateDistance(latitude, longitude,  boundaryLat, boundaryLon);
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

                // 初始化最小距离
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
                        double distanceToVillage =  calculateDistance(latitude, longitude,  villageLat, villageLon);

                        // 更新最小距离
                        if (distanceToVillage < minVillageDistance) {
                            minVillageDistance = distanceToVillage;
                        }
                    }
                }


                System.out.println("计算取整后辖区外地震雅安最大烈度roundedIntensity1所用距离为:  " );
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
                    intensity2 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10( minVillageDistance + 24)) - 0.4;

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
                System.out.println("取整后雅安市行政边界最小距离minBoundaryDistance1: " + minBoundaryDistance1);
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
                    intensity1 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minBoundaryDistance1 + 24)) - 0.4; //计算雅安市行政边界最小距离（单位：米）minBoundaryDistance1
                    intensity2 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minCountyTownDistance1 + 24)) - 0.4;  //正常更新最近的政府乡镇距离  minCountyTownDistance1
                    intensity3 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minVillageDistance + 24)) - 0.4;  //雅安市所有乡镇距离乡镇最小距离  minCountyTownDistance1

                } else {
                    // 震级小于或等于5.5时使用这个公式
                    intensity = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minCityDistance1 + 24)) - 0.6;  //计算震中到城市点的最小距离（单位：米） minCityDistance1
                    intensity1 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minBoundaryDistance1 + 24)) - 0.6; //计算雅安市行政边界最小距离（单位：米）minBoundaryDistance1
                    intensity2 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minCountyTownDistance1 + 24)) - 0.6; //正常更新最近的政府乡镇距离  minCountyTownDistan
                    intensity3 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minVillageDistance + 24)) - 0.6;  //雅安市所有乡镇距离乡镇最小距离  minCountyTownDistance1

                }
                System.out.println("震中到城市点的最小距离的烈度: " + intensity);  //震中区最大地震烈度达%d度
                System.out.println("雅安市行政边界最小距离烈度: " + intensity1);
                System.out.println("最近的政府乡镇的烈度: " + intensity2);
                System.out.println("雅安市所有乡镇距离乡镇最小距离: " + intensity3);

                // 四舍五入取整
                int roundedIntensity = (int) Math.round(intensity);
                int roundedIntensity1 = (int) Math.round(intensity1);
                int roundedIntensity2 = (int) Math.round(intensity2);
                int roundedIntensity3 = (int) Math.round(intensity3);

                // 打印变量的值
                System.out.println("取整后震中到城市点的最小距离的烈度: " + roundedIntensity);  //震中区最大地震烈度达%d度
                System.out.println("取整后雅安市行政边界最小距离烈度: " + roundedIntensity1);
                System.out.println("取整后最近的政府乡镇的烈度: " + roundedIntensity2);
                System.out.println("取整后雅安市所有乡镇距离乡镇最小距离: " + roundedIntensity3);


                //得到我市最大烈度
                maxIntensity = roundedIntensity3;
                System.out.println("maxIntensity烈度选取：roundedIntensity3雅安市所有乡镇距离乡镇最小距离: " + roundedIntensity3);


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

        // 输出筛选结果
        System.out.println("外地地震雅安最大烈度8度及以上时，筛选7度及以上县个数: double big = " + big);
        System.out.println("外地地震雅安最大烈度7度及以上时，筛选6度、7度县个数: double middle = " + middle);
        System.out.println("外地地震雅安最大烈度6度及以上时，筛选5度、6度县个数: double small = " + small);

        //---------最终影响-------
        String influence = ""; // 初始化影响的字符串

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
            influence = "无效的烈度值"; // 如果输入的值不在有效范围内
        }

        // 输出影响结果
        System.out.println("影响：" + influence);



        String panduan =String.format("本次地震是%s，综合判断：本次地震对我市%s。",
                category,influence

        );
        System.out.println(panduan);

        //*****************************************************


        String suggestion = "";

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
            if (maxIntensity < 5) {
                if (eqMagnitude < 5.0) {
                    suggestion = "不启动地震应急响应";
                } else {
                    suggestion = "启动外地地震应急响应";
                }
            }else if ((maxIntensity == 6.0 && big == 0.0) || maxIntensity == 5.0 || big == 1.0) {
                suggestion = "启动强有感地震应急响应";
            }else if (big >= 2) {
                suggestion = "启动市级地震灾害一级应急响应";
            } else if (middle >= 2) {
                suggestion = "启动市级地震灾害二级应急响应";
            } else if (small >= 2) {
                suggestion = "启动市级地震灾害三级应急响应";
            }else{
                suggestion = "应急响应无符合启动条件";   //？？
            }

            // 处理大、中、小区域的响应

        }

        // 输出建议
        System.out.println("应急响应建议: " + suggestion);


        String jianyi = String.format(
                "应急响应建议：建议我市%s（主要依据：1、%s；2、震级:%.1f；3、我市最大烈度：%d度。",
                suggestion,category,eqMagnitude,maxIntensity
        );


        System.out.println("终应急响应建议---建议jianyi ： " + jianyi);

        //*****************************************************


        String measure = "";

        // 将字符串转换为数字类型
        double populationDensityValue = Double.parseDouble(populationDensity);

        System.out.println("字符串转换为数字类型建议:人口密度 populationDensityValue  " + populationDensityValue);

        if (eqName.contains("雅安市")) {
            // 判断人口密度和震级
            if (populationDensityValue >= 200) {
                if (eqMagnitude < 5.0) {
                    measure = "以" + eqAddr + "政府为主开展应急处置";
                } else if (eqMagnitude >= 5.0 && eqMagnitude < 6.0) {
                    measure = "以" + eqName + "政府为主开展应急处置";
                } else if (eqMagnitude >= 6.0) {
                    measure = "以省政府为主开展应急处置，并接受省抗震救灾指挥部的领导与指挥";
                }else{
                    measure = "未定义的应急处置建议";   //??
                }
            } else {
                if (eqMagnitude < 5.5) {
                    measure = "以" + eqAddr + "政府为主开展应急处置";
                } else if (eqMagnitude >= 5.5 && eqMagnitude < 6.5) {
                    measure = "以" + eqName + "政府为主开展应急处置";
                } else if (eqMagnitude >= 6.5) {
                    measure = "以省政府为主开展应急处置，并接受省抗震救灾指挥部的领导与指挥";
                }else{
                    measure = "未定义的应急处置建议";  //??
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
                    measure = "以" + eqAddr + "政府为主开展应急处置";
                    break;
                case "启动强有感地震应急响应":
                case "启动市级地震灾害三级应急响应":
                    measure = "以" + String.valueOf(maxIntensity).substring(0, 3) + "政府为主开展应急处置";
                    break;
                case "启动市级地震灾害二级应急响应":
                    measure = "以雅安市政府为主开展应急处置";
                    break;
                case "启动市级地震灾害一级应急响应":
                    measure = "以省政府为主开展应急处置，并接受省抗震救灾指挥部的领导与指挥";
                    break;
                case "应急响应无符合启动条件":
                    measure = "应急响应无符合启动条件，未定义的应急处置建议";
                    break;
                default:
                    measure = "未定义的应急处置建议";
            }
        }

        // 输出结果
        System.out.println("应急措施: " + measure);

        // 处置措施建议字符串
        String cuoshi = String.format(
                "处置措施建议：%s。",
                measure
        );

        System.out.println(cuoshi);

        // 合并所有字符串
        String combinedResult1 = result + fuJinTownResult + panduan + jianyi + cuoshi ;
        System.out.println("合并字段完成：" + combinedResult1);
        System.out.println("-----------------------------将要开始进行书写word文档阶段-----------------------");
        WordExporter(title,result,fuJinTownResult,panduan,jianyi,cuoshi,formattedTime);
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

    private void WordExporter( String title,String result,String fuJinTownResult,String panduan,String jianyi,String cuoshi,String formattedTime) {

        System.out.println("开始写word文档");

        // 创建一个 XWPFDocument 对象
        XWPFDocument document = new XWPFDocument();

        // 设置页面边距
        setPageMargins(document);

        // 第一行：对内掌握，黑体三号，右对齐
        XWPFParagraph firstParagraph = document.createParagraph();
        firstParagraph.setAlignment(ParagraphAlignment.RIGHT); // 右对齐
        firstParagraph.setSpacingAfter(0); // 段后0行距
        XWPFRun firstRun = firstParagraph.createRun();
        firstRun.setText("对内掌握");
        firstRun.setFontFamily("黑体");  // 黑体
        firstRun.setFontSize(16);  // 三号字体

        // 空3行，黑体16号字体，段落居左
        for (int i = 0; i < 3; i++) {
            XWPFParagraph emptyParagraph = document.createParagraph();
            emptyParagraph.setAlignment(ParagraphAlignment.RIGHT); // 段落居右
            emptyParagraph.setSpacingBefore((int) (16 * 20 * 0.75)); // 段前1.5倍行距
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
        XWPFParagraph emptyParagraphSong  = document.createParagraph();
        emptyParagraphSong .setAlignment(ParagraphAlignment.CENTER); // 段落居中
        emptyParagraphSong.setSpacingAfter(0); // 段后0行距
        // 设置字体样式
        XWPFRun newRunSong = emptyParagraphSong .createRun();
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
        String spaces = "               "; // 这里实际上是15个空格，但视觉宽度可能因字体而异
        thirdParagraph.createRun().setText(spaces); // 创建一个新的run来添加空格，但不设置字体（继承段落默认字体）

        XWPFRun thirdRun2 = thirdParagraph.createRun();
        thirdRun2.setText(formattedTime);
        thirdRun2.setFontFamily("仿宋_GB2312");  // 仿宋_GB2312字体（虽然这里可能继承自上一个run，但明确设置以确保一致性）
        thirdRun2.setFontSize(16);  // 三号字体

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
        // 注意：这里的下划线是通过文本模拟的，如果需要真正的线条，请使用Apache POI的绘图功能



        // 空1行，华文行楷22号字体，段落居中
        XWPFParagraph emptyParagraphHua = document.createParagraph();
        emptyParagraphHua.setAlignment(ParagraphAlignment.CENTER); // 段落居中
        emptyParagraphHua.setSpacingBefore((int) (16 * 20 *1.25 )); // 段前1.25倍行距
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
        emptyParagraphHua.setSpacingBefore((int) (16 * 20 * 0.75)); // 段前0.75倍行距
        decisionInfoParagraph.setSpacingAfter(0); // 段后0行距
        XWPFRun decisionInfoRun = decisionInfoParagraph.createRun();        // 创建一个运行来添加文本
        decisionInfoRun.setText("（辅助决策信息二）");
        decisionInfoRun.setFontFamily("方正小标宋简体");// 设置字体
        decisionInfoRun.setFontSize(22);     // 设置字号为二号（对应22磅）

        // 空1行
        XWPFParagraph emptyParagraph = document.createParagraph();
        emptyParagraph.setAlignment(ParagraphAlignment.RIGHT); // 段落居右
        // 模拟单倍行距（一般情况下1倍行距是240 TWIPS，适当设置行距）
        emptyParagraph.setSpacingBefore(240); // 设置段前行距为240 TWIPS（1倍行距）
        emptyParagraph.setSpacingAfter(240);  // 设置段后行距为240 TWIPS（1倍行距）
        // 设置字体样式
        XWPFRun newRunHei = emptyParagraph.createRun();
        newRunHei.setText(" ");
        newRunHei.setFontFamily("黑体"); // 字体
        newRunHei.setFontSize(16); // 字号16



        //*****************************************开始填入自动生成内容****************************************************************

        // 创建一个新的段落--------第一个标题
        XWPFParagraph earthquakeInfoParagraph = document.createParagraph();
        earthquakeInfoParagraph.setAlignment(ParagraphAlignment.BOTH);  // 设置段落为两端对齐
        earthquakeInfoParagraph.setSpacingAfter(0);    // 设置段后间距为0行（即0磅）
        XWPFRun earthquakeInfoRun = earthquakeInfoParagraph.createRun();
        earthquakeInfoRun.setText("    "+ "一、震区基本情况");  //前面有4个空格
        earthquakeInfoRun.setFontFamily("黑体");
        earthquakeInfoRun.setFontSize(16); // 设置字号为三号（对应16磅）




        // 第一段正文内容
        XWPFParagraph oneText = document.createParagraph();
        oneText.setAlignment(ParagraphAlignment.BOTH); // 两端对齐

        // 设置首行缩进为2字符宽
//        int indentSize = 16 * 2 * 20; // 每字符宽按字号16磅计算，2字符宽
//        oneText.setIndentationFirstLine(indentSize); // 设置首行缩进

        // 设置段前间距为1.5倍行距，段后间距为0
        oneText.setSpacingBefore(0); // 段前0倍行距
//        oneText.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距
        oneText.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun = oneText.createRun();
        newRun.setText("    "+result); // 设置文本内容
        newRun.setFontFamily("仿宋_GB2312"); // 字体
        newRun.setFontSize(16); // 三号字体（16磅）

//        // 设置正文内容
//        char[] chars = result.toCharArray();
//        for (char c : chars) {
//            XWPFRun run = oneText.createRun();
//            if (Character.isDigit(c)) {
//                run.setFontFamily("Times New Roman"); // 数字字体
//            } else {
//                run.setFontFamily("仿宋_GB2312"); // 其他文本字体
//            }
//            run.setFontSize(16); // 三号字体
//            run.setText(String.valueOf(c)); // 写入字符
//        }


        // 创建一个新的段落--------第二个标题
        XWPFParagraph disasterInfoParagraph = document.createParagraph(); // 假设document是您的XWPFDocument对象
        disasterInfoParagraph.setAlignment(ParagraphAlignment.BOTH);         // 设置段落为两端对齐
        disasterInfoParagraph.setSpacingAfter(0);    // 设置段后间距为0行（即0磅）
        XWPFRun disasterInfoRun = disasterInfoParagraph.createRun();
        disasterInfoRun.setText("    "+ "二、雅安震情灾情");
        disasterInfoRun.setFontFamily("黑体");
        disasterInfoRun.setFontSize(16); // 设置字号为16磅（注意：三号字号通常对应16磅，但这里直接使用了16磅）


        // 第二段正文内容
        XWPFParagraph oneText2 = document.createParagraph();
        oneText2.setAlignment(ParagraphAlignment.BOTH); // 两端对齐

        // 设置首行缩进为2字符宽
//        int indentSize2 = 16 * 2 * 20; // 每字符宽按字号16磅计算，2字符宽
//        oneText2.setIndentationFirstLine(indentSize2); // 设置首行缩进

        // 设置段前间距为1.5倍行距，段后间距为0
        oneText2.setSpacingBefore(0); // 段前0倍行距
//        oneText2.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距
        oneText2.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun2 = oneText2.createRun();
        newRun2.setText("    "+fuJinTownResult); // 设置文本内容
        newRun2.setFontFamily("仿宋_GB2312"); // 字体
        newRun2.setFontSize(16); // 三号字体（16磅）
        newRun2.setColor("FF0000"); // 字体颜色：红色


        // 创建一个新的段落--------第三个标题
        XWPFParagraph emergencyResponseParagraph = document.createParagraph();
        emergencyResponseParagraph.setAlignment(ParagraphAlignment.BOTH);       // 设置段落为两端对齐
        emergencyResponseParagraph.setSpacingAfter(0);    // 设置段后间距为0行（即0磅）
        XWPFRun emergencyResponseRun = emergencyResponseParagraph.createRun();
        emergencyResponseRun.setText("    "+ "三、应急处置建议");
        emergencyResponseRun.setFontFamily("黑体");
        emergencyResponseRun.setFontSize(16); // 设置字号为16磅（注意：三号字号通常对应16磅,这里直接指定了磅值）

        // 第三段正文内容
        XWPFParagraph oneText3 = document.createParagraph();
        oneText3.setAlignment(ParagraphAlignment.BOTH); // 两端对齐

        // 设置首行缩进为2字符宽
//        int indentSize3 = 16 * 2 * 20; // 每字符宽按字号16磅计算，2字符宽
//        oneText3.setIndentationFirstLine(indentSize3); // 设置首行缩进

        // 设置段前间距为1.5倍行距，段后间距为0
        oneText3.setSpacingBefore(0); // 段前0倍行距
        // oneText3.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距（注释掉的代码保持一致）
        oneText3.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun3 = oneText3.createRun();
        newRun3.setText("    "+panduan); // 设置文本内容
        newRun3.setFontFamily("仿宋_GB2312"); // 字体
        newRun3.setFontSize(16); // 三号字体（16磅）


        // 第四段正文内容
        XWPFParagraph oneText4 = document.createParagraph();
        oneText4.setAlignment(ParagraphAlignment.BOTH); // 两端对齐

        // 设置首行缩进为2字符宽
//        int indentSize4 = 16 * 2 * 20; // 每字符宽按字号16磅计算，2字符宽
//        oneText4.setIndentationFirstLine(indentSize4); // 设置首行缩进

        // 设置段前间距为1.5倍行距，段后间距为0
        oneText4.setSpacingBefore(0); // 段前0倍行距
        // oneText4.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距（注释掉的代码保持一致）
        oneText4.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun4 = oneText4.createRun();
        newRun4.setText("    "+jianyi); // 设置文本内容
        newRun4.setFontFamily("仿宋_GB2312"); // 字体
        newRun4.setFontSize(16); // 三号字体（16磅）



        // 第五段正文内容
        XWPFParagraph oneText5 = document.createParagraph();
        oneText5.setAlignment(ParagraphAlignment.BOTH); // 两端对齐

        // 设置首行缩进为2字符宽
//        int indentSize5 = 16 * 2 * 20; // 每字符宽按字号16磅计算，2字符宽
//        oneText5.setIndentationFirstLine(indentSize5); // 设置首行缩进

        // 设置段前间距为1.5倍行距，段后间距为0
        oneText5.setSpacingBefore(0); // 段前0倍行距
        // oneText5.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距（注释掉的代码保持一致）
        oneText5.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun5 = oneText5.createRun();
        newRun5.setText("    "+cuoshi); // 设置文本内容
        newRun5.setFontFamily("仿宋_GB2312"); // 字体
        newRun5.setFontSize(16); // 三号字体（16磅）


        //*****************************************开始填入自动生成内容结束****************************************************************

        // 空1行，仿宋_GB2312的16号字体，段落居中
        XWPFParagraph emptyParagraphSong1  = document.createParagraph();
        emptyParagraphSong1 .setAlignment(ParagraphAlignment.LEFT); // 段落居中
        emptyParagraphSong1.setSpacingAfter(0); // 段后0行距
        // 设置字体样式
        XWPFRun newRunSong1 = emptyParagraphSong1 .createRun();
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
        oneText6.setSpacingBefore(0);// 段前0倍行距
        // oneText6.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距（注释掉的代码保持一致）
        oneText6.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun6 = oneText6.createRun();
        newRun6.setText("    "+"附件：地震影响场分布图"); // 设置文本内容
        newRun6.setFontFamily("仿宋_GB2312"); // 字体
        newRun6.setFontSize(16); // 三号字体（16磅）

        // 空1行，仿宋_GB2312的16号字体，段落居中
        XWPFParagraph emptyParagraphSong2  = document.createParagraph();
        emptyParagraphSong2 .setAlignment(ParagraphAlignment.LEFT); // 段落居中
        emptyParagraphSong2.setSpacingAfter(0); // 段后0行距
        // 设置字体样式
        XWPFRun newRunSong2 = emptyParagraphSong2 .createRun();
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
        oneText7.setSpacingBefore(0); // 段前0倍行距
// oneText7.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距（注释掉的代码保持一致）
        oneText7.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun7 = oneText7.createRun();
        newRun7.setText("    "+"雅安市应急管理局值班电话：0835-2220001，卫星电话：17406544731。"); // 设置文本内容
        newRun7.setFontFamily("仿宋_GB2312"); // 字体
        newRun7.setFontSize(16); // 三号字体（16磅）

        // 空1行，仿宋_GB2312的16号字体，段落居中
        XWPFParagraph emptyParagraphSong3  = document.createParagraph();
        emptyParagraphSong3 .setAlignment(ParagraphAlignment.LEFT); // 段落居中
        // 模拟单倍行距（一般情况下1倍行距是240 TWIPS，适当设置行距）
        emptyParagraphSong3.setSpacingBefore(240); // 设置段前行距为240 TWIPS（1倍行距）
        emptyParagraphSong3.setSpacingAfter(240);  // 设置段后行距为240 TWIPS（1倍行距）
        // 设置字体样式
        XWPFRun newRunSong3 = emptyParagraphSong3 .createRun();
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
        oneText8.setSpacingBefore(0); // 段前0倍行距
// oneText8.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距（注释掉的代码保持一致）
        oneText8.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun8 = oneText8.createRun();
        newRun8.setText("    "+"（本期送：市政府领导、局领导、局机关各科室。）"); // 设置文本内容
        newRun8.setFontFamily("仿宋_GB2312"); // 字体
        newRun8.setFontSize(16); // 三号字体（16磅）



        // 空1行，仿宋_GB2312的16号字体，段落居中
        XWPFParagraph emptyParagraphSong4  = document.createParagraph();
        emptyParagraphSong4 .setAlignment(ParagraphAlignment.LEFT); // 段落居左
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
        oneText10.setSpacingBefore(0); // 段前1.5倍行距
// oneText10.setSpacingBefore((int) (16 * 20 * 1.5)); // 段前1.5倍行距（注释掉的代码保持一致）
        oneText10.setSpacingAfter(0); // 段后0行距

        // 设置字体样式
        XWPFRun newRun10 = oneText10.createRun();
        newRun10.setText("地震影响场分布图"); // 设置文本内容
        newRun10.setFontFamily("方正小标宋简体"); // 字体
        newRun10.setFontSize(20); // 20号

        // 空1行，仿宋_GB2312的16号字体，段落居中
        XWPFParagraph emptyParagraphSong5  = document.createParagraph();
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
        String fileName = formattedTime + "级地震（辅助决策信息二）.docx";
//        String filePath = "C:/Users/Smile/Desktop/" + fileName;
        String filePath = "C:/Users/Smile/Desktop/" + fileName;

        System.out.println("word文档已在桌面");

        // 写入文件
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            document.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPageMargins(XWPFDocument document) {
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

    }

}


