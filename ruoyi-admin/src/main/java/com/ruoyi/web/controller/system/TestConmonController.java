//package com.ruoyi.web.controller.system;
//import com.ruoyi.system.domain.dto.*;
//import com.ruoyi.web.api.IdentityAuthentication;
//import com.ruoyi.web.api.ThirdPartyCommonApi;
//import com.ruoyi.web.api.ThirdPartyHttpClients;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.web.bind.annotation.*;
//import javax.annotation.Resource;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author: xiaodemos
// * @date: 2024-11-23 14:36
// * @description: 用于测试接口
// */
//
//@RestController
//@RequestMapping("/test")
//public class TestConmonController {
//    @Autowired
//    private ThirdPartyHttpClients httpClientService;
//    @Resource
//    @Qualifier("cacheTemplate")
//    private RedisTemplate<String,String> cacheTemplate;
//    @Resource
//    private IdentityAuthentication identityAuthentication;
//    @Resource
//    private ThirdPartyCommonApi thirdPartyCommonApi;
//    @Resource
////    private DisasterDamageService disasterDamageService;
//    @GetMapping("/get/resp")
////    public String GetResp(){
////        EqEventGetResultTownDTO params = new EqEventGetResultTownDTO();
////        params.setEvent("T2024103010295151330100");
//////        String res = thirdPartyCommonAPI.getSeismicEventGetGetResultTownByGet(params);
////        return res;
////    }
//
//    @PostMapping("/post/resp")
////    public String PostResp(@RequestBody EqEventGetPageDTO params){
////
////        // String seismicEventByGet = thirdPartyCommonAPI.getSeismicEventByGet(params);
////
////        return seismicEventByGet;
////    }
//
//    //假设是地震触发接口
//    @PostMapping("/saveearth")
//    public Map<String, List<Object>> saveearth(@RequestBody EqEventTriggerDTO params){
////        String eqqueueId = thirdPartyCommonAPI.getSeismicTriggerByPost(params);
////        //地震基础信息
////
////
////        //灾情报告
////        EqEventGetReportDTO eqEventGetReportDTO = new EqEventGetReportDTO();
////        eqEventGetReportDTO.setEqqueueId(eqqueueId);
////        //专题图
////        EqEventGetMapDTO eqEventGetMapDTO = new EqEventGetMapDTO();
////        eqEventGetMapDTO.setEqqueueId(eqqueueId);
////
////        List report = disasterDamageService.getReport(eqEventGetReportDTO);
////
////        List map = disasterDamageService.getMap(eqEventGetMapDTO);
////
////        HashMap<String, List<Object>> res = new HashMap<>();
////
////        res.put("report",report);
////        res.put("map",map);
//
//        return res;
//    }
//}
