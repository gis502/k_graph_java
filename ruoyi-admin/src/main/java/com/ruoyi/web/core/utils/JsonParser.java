package com.ruoyi.web.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.system.domain.dto.ResultEventGetMapDTO;
import com.ruoyi.system.domain.dto.ResultEventGetReportDTO;
import com.ruoyi.system.domain.dto.ResultEventGetResultTownDTO;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * @author: xiaodemos
 * @date: 2024-12-06 11:09
 * @description: 字符串解析成json对象
 */


public class JsonParser {


    public static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 通用的 JSON 解析方法
     * @param jsonString 需要反序列化的 JSON 字符串
     * @param clazz      目标类型的 Class 对象
     * @param <T>        目标类型的类型参数
     * @return 反序列化后的对象，如果解析失败则返回 null
     */
    public static  <T> T parseJson(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @param jsonString 第三方接口取到的json数据
     * @author: xiaodemos
     * @date: 2024/11/27 1:10
     * @description: 取到json字符串中的file字段 （可以优化到一个方法中，每次解析的时候传入一个flag值来进行整体解析或者是按key值解析）
     * @return: 返回file字段中的文件路径
     */
    public static String parseJsonToFileField(String jsonString) {
        // 创建ObjectMapper实例
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 解析JSON字符串为JsonNode对象
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // 获取 "data" 对象下的 "file" 字段
            JsonNode dataNode = rootNode.path("data");  // 获取data节点
            String filePath = dataNode.path("file").asText();  // 获取file字段并转换为String

            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param jsonString 第三方接口取到的json数据
     * @author: xiaodemos
     * @date: 2024/11/27 6:12
     * @description: 对字符串进行解析，获取eqqueueid
     * @return: 返回 eqqueueid
     */
    public static String parseJsonToEqQueueId(String jsonString) {
        // 创建ObjectMapper实例
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 解析JSON字符串为JsonNode对象
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // 获取 "data" 对象下的 "eqqueueId" 字段
            JsonNode dataNode = rootNode.path("data");  // 获取data节点

            String filePath = dataNode.path("eqqueueId").asText();  // 获取file字段并转换为String

            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @author:  xiaodemos
     * @date:  2024/12/10 9:25
     * @description: 删除第三方的数据，是否删除成功
     * @param jsonString 第三方接口取到的json数据
     * @return: 返回解析的第三方bool值
     */
    public static boolean parseJsonToBooleanField(String jsonString) {
        // 创建ObjectMapper实例
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 解析JSON字符串为JsonNode对象
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // 获取 "data" 对象下的 "eqqueueId" 字段
            boolean flag = rootNode.path("data").asBoolean();// 获取data节点

            return flag;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String parseJsonToObjectField(String jsonString) {
        // 创建ObjectMapper实例
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 解析JSON字符串为JsonNode对象
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // 获取 "data" 对象下的 "eqqueueId" 字段
            String flag = rootNode.path("data").asText();// 获取data节点

            return flag;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }


}
