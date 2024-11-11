package com.ruoyi.web.controller.system;

import com.ruoyi.web.controller.common.DrivingRouteRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/system")
public class RouteController {

    @Value("${amap.key}")
    private String amapApiKey;

    private final RestTemplate restTemplate;

    public RouteController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/driving")
    public ResponseEntity<Map<String, Object>> getDrivingRoute(@RequestBody DrivingRouteRequest request) {

        // 将数组转换为字符串，格式为 "经度,纬度"
        String origin = request.getOrigin()[0] + "," + request.getOrigin()[1];
        String destination = request.getDestination()[0] + "," + request.getDestination()[1];
        // 构建请求 URL
        String url = UriComponentsBuilder.fromHttpUrl("https://restapi.amap.com/v3/direction/driving")
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .queryParam("extensions", "base")
                .queryParam("strategy", "0")
                .queryParam("avoidpolygons", request.getAvoidpolygons())
                .queryParam("key", amapApiKey)
                .toUriString();

        // 使用 ParameterizedTypeReference
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // 返回高德 API 返回的原始数据
        return response;
    }
}