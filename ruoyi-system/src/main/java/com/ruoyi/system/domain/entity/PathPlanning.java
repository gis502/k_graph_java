package com.ruoyi.system.domain.entity;

import lombok.Data;

import java.util.List;

@Data
public class PathPlanning {

    /**
     * 起始点经纬度
     */
    private String from;

    /**
     * 终点经纬度
     */
    private String end;

    /**
     * 避开区域的经纬度列表，每个区域为一组经纬度对
     */
    private List<List<double[]>> areas; // 将避开区域改为 List 结构


    /**
     * 请求高德api的key
     */
    private String key;
}
