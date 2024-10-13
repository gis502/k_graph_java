package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.entity.BuildingDamage;
import com.ruoyi.system.domain.entity.EconomicLoss;
import com.ruoyi.system.domain.entity.SeismicIntensityCircle;
import com.ruoyi.system.service.PersonDes2019Service;
import com.ruoyi.system.service.SeismicIntensityCircleService;
import com.ruoyi.system.service.SichuanPopdensityPointService;
import com.ruoyi.system.service.YaanJsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.system.mapper.EconomicLossMapper;
import com.ruoyi.system.service.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/damageassessment")
public class DamageAssessmentController {

    @Autowired
    private SeismicIntensityCircleService seismicIntensityCircleService;
    @Autowired
    private PersonDes2019Service personDes2019Service;
    @Autowired
    private YaanJsonService yaanJsonService;
    @Autowired
    private BuildingDamageService buildingDamageService;
    @Autowired
    private EconomicLossService economicLossService;

    @PostMapping("/saveIntensityCircle")
    @Log(title = "灾损评估-烈度圈", businessType = BusinessType.INSERT)
    public float saveIntensityCircle(@RequestBody List<Map<String, Object>> savecircles) {
        String eqid = (String) savecircles.get(0).get("eqid");
//        System.out.println(eqid);
        List<SeismicIntensityCircle> circledata = seismicIntensityCircleService.selectCircleByEqid(eqid);
//        System.out.println(circledata);
//
        if(circledata.size()!=0){
//            System.out.println("数据库中该地震烈度圈已存在");
            return 1;
        }
        else{
            for (Map<String, Object> circle : savecircles) {
                SeismicIntensityCircle seismicIntensityCircle = new SeismicIntensityCircle();

                seismicIntensityCircle.setEqid(circle.get("eqid"));
                seismicIntensityCircle.setIntensity((Integer) circle.get("intensity"));
                seismicIntensityCircle.setGeom((String) circle.get("geom"));
                seismicIntensityCircleService.addCircle(seismicIntensityCircle);
            }
//            System.out.println("烈度圈存储完成");
            return 1;
        }
//        return 1;
    }

    @PostMapping("/getPersonDes")
    public Map<String, Object> getPersonDes(@RequestBody String eqid) {
        eqid = eqid.replace("\"", "");
        Map<String, Object> response = new HashMap<>();
        //震中烈度
        Integer Centerintensity=seismicIntensityCircleService.selectCenterintensityByEqid(eqid);
        //查烈度圈最大圈外圈
        String circlestr = seismicIntensityCircleService.selectBigOutCircleByEqid(eqid);
        // 使用正则表达式匹配并提取所需的部分
        String pattern = "CURVEPOLYGON\\(CIRCULARSTRING\\((.*?)\\)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(circlestr);
        //总伤亡人数评估
        double casualAll=0;
        String Outcir="";
        if (m.find()) {
            Outcir = "CURVEPOLYGON(CIRCULARSTRING(" + m.group(1) + "))";  //取烈度圈最外面一圈

            int count=personDes2019Service.getCountinCirle(Outcir); //是否有人口密度栅格落入烈度圈范围
            if(count==0){casualAll=0;}
            else{
                double des=personDes2019Service.getavgdesinCirle(Outcir); //烈度圈中方平均人口密度
                if(Centerintensity>8){  //烈度大于八度。人员密度中原来基础上加上150（公式）
                    des=des+150;
                }
                double centerIntensityLog = Math.log(Centerintensity); //中心烈度
                double peopleDesLog = Math.log(des); //烈度圈中方平均人口密度
                //总人数 带入公式
                casualAll= Math.round(
                        Math.exp(
                                Math.exp(
                                        3.1571892494732325 * centerIntensityLog +
                                                0.34553795677042193 * peopleDesLog -
                                                6.954773954657806
                                )
                        )
                );
            }
        }
        else {
            casualAll=0;
        }
        response.put("casualAll", casualAll);


        //每个行政区权重计算
        List<String> arrName=new ArrayList<>(Arrays.asList("雨城区", "名山区", "荥经县", "汉源县", "石棉县", "天全县", "芦山县", "宝兴县")); //行政区
        double totalWeightedRatio = 0; // 所有区域的加权比率总和
        Map<String, Double> weightedRatios = new HashMap<>(); // 用于存储每个区县的加权比率
        Map<String, Double> populationRatios = new HashMap<String, Double>() {{  //人员占比
            put("雨城区", 25.71);
            put("名山区", 17.75);
            put("荥经县", 9.17);
            put("汉源县", 19.91);
            put("石棉县", 7.95);
            put("天全县", 9.20);
            put("芦山县", 6.96);
            put("宝兴县", 3.35);
        }};

        //
        if(casualAll!=0){
//            double sum=0;

            for (String item : arrName) {
                String itemAreaStr=yaanJsonService.getAreaStr(item);
                String intersectionArea=yaanJsonService.getintersectionArea(itemAreaStr,Outcir);  //行政区和烈度圈相交地面
                if(intersectionArea=="POLYGON EMPTY"){  //不相交
//                    response.put(item, 0);
                    weightedRatios.put(item, 0.0);
                }

                else{ //烈度圈与行政区相交
                    double areaRatio=yaanJsonService.computeIntersectionRatio(intersectionArea,Outcir); //行政区和烈度圈相交比率
                    System.out.println("areaRatio");
                    System.out.println(areaRatio);
                    double populationRatio = populationRatios.getOrDefault(item, 0.0); //人员占比
                    System.out.println("populationRatio");
                    System.out.println(populationRatio);

                    double weightedRatio = areaRatio * populationRatio;
                    System.out.println("weightedRatio");
                    System.out.println(weightedRatio);
                    weightedRatios.put(item, weightedRatio); // 存储每个区县的人员*面积比率
                    System.out.println("weightedRatios");
                    System.out.println(weightedRatios);
                    totalWeightedRatio += weightedRatio;
                }
            }
        }
        System.out.println("totalWeightedRatio");
        System.out.println(totalWeightedRatio);
        //每个区县人员
        if(totalWeightedRatio!=0){
            double sum=0;
            for (String item : arrName) {
                double weightedRatio = weightedRatios.getOrDefault(item, 0.0); //人员占比
                     double itemcasual=Math.round(weightedRatio*casualAll/totalWeightedRatio);  //比率乘以总人数
                     sum+=itemcasual;
                    response.put(item, itemcasual);
            }
            if(sum==0){response.put("yaancasual", "无");}
            else if(sum>casualAll){response.put("casualAll", sum);}
            else {response.put("yaancasual", sum);}
        }


        System.out.println(response);
        return response;
    }

    @GetMapping("/getBuildingDamageByEqid")
    public List<BuildingDamage> getBuildingDamageByEqid(@RequestParam("eqid") String eqid) {
        return buildingDamageService.selectBuildingDamageByEqid(eqid);
    }

    @GetMapping("/getEconomicLossByEqid")
    public List<EconomicLoss> getEconomicLossByEqid(@RequestParam("eqid") String eqid) {
        return economicLossService.selectEconomicLossByEqid(eqid);
    }

}


