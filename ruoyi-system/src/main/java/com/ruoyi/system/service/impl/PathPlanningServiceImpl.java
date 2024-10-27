package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.entity.PathPlanning;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class PathPlanningServiceImpl {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 解析器

    @Autowired
    public PathPlanningServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Object[] getRouteFromAMap(PathPlanning routeRequest) throws Exception {
        // 构造高德 API 请求 URL
        StringBuilder urlBuilder = new StringBuilder("https://restapi.amap.com/v3/direction/driving?");
        urlBuilder.append("origin=").append(routeRequest.getFrom())
                .append("&destination=").append(routeRequest.getEnd())
                .append("&output = json")//指定返回数据格式为 JSON
                .append("&extensions=base&strategy=0&key=").append(routeRequest.getKey());

        // 只在 avoidArea 不为空时添加该参数
        if (routeRequest.getAreas() != null && !routeRequest.getAreas().isEmpty()) {
            String avoidArea = convertAreasToString(routeRequest.getAreas());
            urlBuilder.append("&avoidpolygons=").append(avoidArea);
        }

        // 发送请求并获取响应
        String response = restTemplate.getForObject(urlBuilder.toString(), String.class);

        // 处理返回的数据
        return processRouteData(response);
    }

    private String convertAreasToString(List<List<double[]>> areas) {
        StringBuilder avoidArea = new StringBuilder();
        for (List<double[]> area : areas) {
            for (double[] coord : area) {
                avoidArea.append(String.format("%f,%f;", coord[0], coord[1]));
            }
            avoidArea.setLength(avoidArea.length() - 1); // 移除末尾分号
            avoidArea.append("|");
        }
        if (avoidArea.length() > 0) {
            avoidArea.setLength(avoidArea.length() - 1); // 移除末尾管道符
        }
        return avoidArea.toString();
    }

    private Object[] processRouteData(String jsonData) throws Exception {
        JsonNode data = objectMapper.readTree(jsonData);
        List<String> pathInstructions = new ArrayList<>();
        StringBuilder path = new StringBuilder();
        int totalDistance = 0;

        // 获取路径数据
        JsonNode steps = data.path("route").path("paths").get(0).path("steps");
        for (JsonNode step : steps) {
            pathInstructions.add(step.path("instruction").asText());
            path.append(step.path("polyline").asText()).append(";");
            totalDistance += step.path("distance").asInt();
        }

        // 处理路径段
        List<double[]> pathSegments = new ArrayList<>();
        for (String segment : path.toString().split(";")) {
            String[] coords = segment.split(",");
            if (coords.length == 2) {
                double[] coord = gcj02towgs84(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
                pathSegments.add(coord);
            }
        }

        // 封装数据到 Object 数组中
        return new Object[]{
                pathSegments.toArray(new double[0][]), // 路径段
                totalDistance,                          // 总距离
                pathInstructions,                       // 路径指令
                data.path("route").path("paths").get(0).path("duration").asDouble() / 60, // 车行时间
                (totalDistance * 0.7) / 60             // 人行时间
        };

    }
    private double[] gcj02towgs84(double longitude, double latitude) {
        // 首先检查输入坐标是否在中国范围内
        if (outOfChina(longitude, latitude)) {
            return new double[]{longitude, latitude}; // 不在中国范围内直接返回
        }

        double[] d = transform(longitude, latitude);
        double latitudeWGS84 = latitude - d[1];
        double longitudeWGS84 = longitude - d[0];

        return new double[]{longitudeWGS84, latitudeWGS84};
    }

    private boolean outOfChina(double lat, double lon) {
        // 定义中国的边界
        return lon < 72.004 || lon > 137.8347 || lat < 0.8293 || lat > 55.8271;
    }

    private double[] transform(double longitude, double latitude) {
        double[] d = new double[2];
        double x = longitude - 105.0;
        double y = latitude - 35.0;
        double radX = x / 180.0 * Math.PI;
        double radY = y / 180.0 * Math.PI;

        double magic = Math.sin(radY);
        magic = 1 - 0.006693421622965943 * magic * magic;
        double sqrtMagic = Math.sqrt(magic);

        d[0] = (x * 180.0 / 6378137 * Math.cos(radY) * (1.0 - 0.006693421622965943 * Math.sin(radY) * Math.sin(radY) / magic) / sqrtMagic);
        d[1] = (y * 180.0 / 6378137 * (1.0 - 0.006693421622965943 * Math.sin(radY) * Math.sin(radY) / magic) / sqrtMagic);

        return d;
    }
}
