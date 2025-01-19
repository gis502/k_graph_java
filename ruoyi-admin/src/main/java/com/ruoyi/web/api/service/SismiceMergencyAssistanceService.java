package com.ruoyi.web.api.service;


import com.ruoyi.system.domain.dto.EqEventTriggerDTO;
import lombok.SneakyThrows;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: xiaodemos
 * @date: 2024-1-18 16:45
 * @description: 地震触发业务逻辑
 */
public class SismiceMergencyAssistanceService {
    @SneakyThrows
    public void file(EqEventTriggerDTO params, String eqqueueId){
        System.out.println("前端构建文本传的参数" + params);
        String eqName = params.getEqName();  //
        String eqTime = params.getEqTime();
        String eqAddr = params.getEqAddr();
        Double latitude = params.getLatitude();
        Double longitude = params.getLongitude();
        Double eqMagnitude = params.getEqMagnitude();
        Double eqDepth = params.getEqDepth();


        // 创建 SimpleDateFormat 对象，用于解析原始时间字符串
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 将输入的时间字符串解析为 Date 对象
        Date parsedDate = inputFormat.parse(eqTime);

        // 获取月份和日期
        SimpleDateFormat monthFormat = new SimpleDateFormat("M"); // 月份，不补零
        SimpleDateFormat dayFormat = new SimpleDateFormat("d");   // 日期，不补零

        String month = monthFormat.format(parsedDate); // 获取月份
        String day = dayFormat.format(parsedDate);     // 获取日期


        // 创建 SimpleDateFormat 对象，用于格式化为目标格式
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");

        // 拼接成所需格式
        String monthDate = month + "·" + day;

        // 格式化 Date 对象为目标格式的字符串
        String formattedTime = outputFormat.format(parsedDate);
        String result = String.format(
                "地震所在位置位于%s年“%s”%s%.1f级地震余震活动区范围，震区大部分属于川西高山高原区。震中泸定县人口密度约39人/平方公里。",
                formattedTime,monthDate,eqAddr,eqMagnitude
//                "中国地震台网正式(CC)测定：%s在%s（北纬%.2f度，东经%.2f度）发生%.1f级地震，震源深度%.1f公里。",
//                formattedTime, eqAddr, latitude, longitude, eqMagnitude, eqDepth
        );
        //*****************************************************

        String fuJinTownResult = " 预估全市死亡X～X（按±30%浮动）人，重伤X～X（按±30%浮动）人，房屋损毁X～X（按±30%浮动）间，直接经济损失X～X亿元（按±30%浮动）。";
        System.out.println(fuJinTownResult);

        //*****************************************************

        String panduan = "本次地震是邻近市州地震，综合判断：本次地震对我市无影响。";


        //----------烈度--------------


        // 初始化最小距离
        double minBoundaryDistance = Double.MAX_VALUE;


        // 计算后的最小距离（单位：公里）
        double minDistanceInKm = minBoundaryDistance;  // 转换为公里


        //初步评估
        // 公式部分
        double intensity1; //辖区外地震雅安最大烈度
        if (eqMagnitude > 5.5) {
            // 震级大于5.5时使用这个公式
            intensity1 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minDistanceInKm + 24)) - 0.4;
        } else {
            // 震级小于或等于5.5时使用这个公式
            intensity1 = 7.3568 + 1.278 * eqMagnitude - (5.0655 * Math.log10(minDistanceInKm + 24)) - 0.6;

        }

        int roundedIntensity1 = (int) Math.round(intensity1);


        String jianyi = String.format(
                "应急响应建议：建议我市不启动地震应急响应（主要依据：1、邻近市州地震；2、震级:%.1f；3、我市最大烈度：%d度。",
                eqMagnitude, roundedIntensity1
        );

        // 处置措施建议字符串
        String cuoshi = "处置措施建议：以震中政府为主开展应急处置。";



    }
}
