package com.ruoyi.web.controller.system;



import com.ruoyi.system.domain.SeismicIntensityCircle;
import com.ruoyi.system.service.SeismicIntensityCircleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/damageassessment")
public class DamageAssessmentController {

    @Autowired
    private SeismicIntensityCircleService seismicIntensityCircleService;


    @PostMapping("/saveIntensityCircle")
    public int saveIntensityCircle(@RequestBody List<Map<String, Object>> savecircles) {
        String eqid = (String) savecircles.get(0).get("eqid");
//        System.out.println(eqid);

        List<SeismicIntensityCircle> circledata = seismicIntensityCircleService.selectCircleByEqid(eqid);
        System.out.println(circledata);

        if(circledata.size()!=0){
//            System.out.println("数据库中该地震烈度圈已存在");
            return 1;
        }
        else{
            for (Map<String, Object> circle : savecircles) {
                SeismicIntensityCircle seismicIntensityCircle = new SeismicIntensityCircle();

                seismicIntensityCircle.setEqid(circle.get("eqid"));
                seismicIntensityCircle.setIntensity((Integer) circle.get("intensity"));
                seismicIntensityCircle.setGeom(circle.get("geom"));
                seismicIntensityCircleService.addCircle(seismicIntensityCircle);
            }
//            System.out.println("烈度圈存储完成");
            return 1;
        }
//        return 1;
    }
}


