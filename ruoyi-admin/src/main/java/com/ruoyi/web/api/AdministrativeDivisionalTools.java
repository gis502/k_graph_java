package com.ruoyi.web.api;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class AdministrativeDivisionalTools {
    @Autowired
    private RestTemplate restTemplate;

    // 高德 API 的密钥
    private static final String API_KEY = "90e9c670a02b159e0474c0f1e612eee6";
    public AdministrativeDivisionalTools(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    /**
     * @description: 根据地址拆分获取省市区信息,只返回省和市，区和县将与市一起处理
     *
     * @author : ljw
     * @param fullAddress 完整地址
     * @return 包含省和市信息的数组，格式：[ 省, 市, 区]
     * @throws Exception 可能抛出的异常，如网络请求异常或 JSON 解析错误
     */
    public String[] getProvinceCityDistrict(String fullAddress) throws Exception {
        // 构建请求 URL
        String url = String.format(
                "https://restapi.amap.com/v3/geocode/geo?address=%s&key=%s",
                fullAddress.trim(), API_KEY); // 不再进行 URLEncoder.encode() 处理
//        URLEncoder.encode(fullAddress, "UTF-8"), API_KEY);

        // 打印请求的 URL，方便调试
        log.debug("请求 高德 url : {}", url);

        // 避免请求过快导致风控
        Thread.sleep(1000);

        // 发起 GET 请求
        String response = restTemplate.getForObject(url, String.class);


        // 解析 JSON 响应
        if (response == null || response.isEmpty()) {
            throw new Exception("请求高德 API 返回数据为空");
        }

        // 记录原始的响应信息
        log.debug("高德 API 响应: {}", response);




        JSONObject jsonResponse = new JSONObject(response);
        if ("1".equals(jsonResponse.getString("status"))) {
            JSONObject geocode = jsonResponse.getJSONArray("geocodes").getJSONObject(0);

            String province = geocode.optString("province", "");
            String city = geocode.optString("city", "");
            String district = geocode.optString("district", "");

            // 如果区/县为空，尝试从城市中提取
            if (district.isEmpty() && !city.isEmpty()) {
                district = extractDistrictFromCity(city);
            }


            // 记录日志
            log.info("解析结果 -> 省: {}, 市: {}, 区/县: {}", province, city, district);


            // 记录日志
            log.info("Province: {}, City: {}, District: {}", province, city, district);

            /*
             * 从完整地址中去除省、市、区的部分，得到剩余地址。
             */
            String remainingAddress = fullAddress
                    .replace(province, "")
                    .replace(city, "")
                    .replace(district, "")
                    .trim();

            // 返回省、市、区和剩余地址
            return new String[]{province, city, district, remainingAddress};
        } else {
            throw new Exception("高德 API 返回错误，信息: " + jsonResponse.getString("info"));
        }
    }

    private String extractDistrictFromCity(String city) {
        // 假设城市名称中包含区/县信息（如“北京市朝阳区”）
        if (city.contains("市")) {
            String[] parts = city.split("市");
            if (parts.length > 1) {
                return parts[1].trim(); // 返回区/县部分
            }
        }
        return ""; // 如果无法提取，返回空字符串
    }

}
