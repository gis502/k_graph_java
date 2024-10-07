package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
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
            Outcir = "CURVEPOLYGON(CIRCULARSTRING(" + m.group(1) + "))";

            int count=personDes2019Service.getCountinCirle(Outcir);
            if(count==0){casualAll=0;}
            else{
                double des=personDes2019Service.getavgdesinCirle(Outcir);
                if(Centerintensity>8){
                    des=des+150;
                }
                double centerIntensityLog = Math.log(Centerintensity);
                double peopleDesLog = Math.log(des);
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

        //雅安市各县  按面积占比乘以总数
        if(casualAll!=0){
            double sum=0;

            List<String> arrName=new ArrayList<>(Arrays.asList("雨城区", "名山区", "荥经县", "汉源县", "石棉县", "天全县", "芦山县", "宝兴县"));
            for (String item : arrName) {
                String itemAreaStr=yaanJsonService.getAreaStr(item);
                String intersectionArea=yaanJsonService.getintersectionArea(itemAreaStr,Outcir);
                if(intersectionArea=="POLYGON EMPTY"){
                    response.put(item, 0);
                }
                else{
                     double ratio=yaanJsonService.computeIntersectionRatio(intersectionArea,Outcir);
                     double itemcasual=Math.round(ratio*casualAll);
                     sum+=itemcasual;
                    response.put(item, itemcasual);
                }
            }
            if(sum==0){response.put("yaancasual", "无");}
            else if(sum>casualAll){response.put("casualAll", sum);}
            else {response.put("yaancasual", sum);}
        }

        return response;
    }

}


