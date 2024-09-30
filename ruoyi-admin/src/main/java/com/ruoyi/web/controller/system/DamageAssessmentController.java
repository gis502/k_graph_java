package com.ruoyi.web.controller.system;



import com.ruoyi.system.domain.entity.SeismicIntensityCircle;
import com.ruoyi.system.service.PersonDes2019Service;
import com.ruoyi.system.service.SeismicIntensityCircleService;
import com.ruoyi.system.service.SichuanPopdensityPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/damageassessment")
public class DamageAssessmentController {

    @Autowired
    private SeismicIntensityCircleService seismicIntensityCircleService;



    @Autowired
    private PersonDes2019Service personDes2019Service;
    @PostMapping("/saveIntensityCircle")
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
        response.put("centerintensity", Centerintensity);

        //查烈度圈最大圈外圈
        String circlestr = seismicIntensityCircleService.selectBigOutCircleByEqid(eqid);
        // 使用正则表达式匹配并提取所需的部分
        String pattern = "CURVEPOLYGON\\(CIRCULARSTRING\\((.*?)\\)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(circlestr);
        if (m.find()) {
            String result = "CURVEPOLYGON(CIRCULARSTRING(" + m.group(1) + "))";

            int count=personDes2019Service.getCountinCirle(result);
            if(count==0){response.put("peopledes", 0);}

            else{
                double des=personDes2019Service.getavgdesinCirle(result);
                response.put("peopledes", des);
            }
        }
        else {
            response.put("peopledes", 0);
        }

        System.out.println(response);
        return response;
    }

}


