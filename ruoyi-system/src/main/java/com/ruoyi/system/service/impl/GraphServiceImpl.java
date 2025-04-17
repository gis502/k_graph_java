package com.ruoyi.system.service.impl;

import com.ruoyi.system.service.GraphService;
import org.neo4j.driver.types.Relationship;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class GraphServiceImpl implements GraphService {
    private final Neo4jClient neo4jClient;

    public GraphServiceImpl(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @Override
    public List<Map<String, Object>> getGraphData(List<Map<String, String>> inputList) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Map<String, String> input : inputList) {
            for (String value : input.values()) {
                // 用 name 属性模糊查询，不考虑字段名
                String query = String.format(
                        "MATCH (n)-[r]->(m) " +
                                "WHERE (n.name CONTAINS '%s' OR m.name CONTAINS '%s' OR type(r) CONTAINS '%s') " +
                                "RETURN n, r, m", value, value, value);

                List<Map<String, Object>> queryResults = queryGraphData(query);
                resultList.addAll(queryResults);
            }
        }
        return resultList;
    }

    // 查询并映射结果
    private ArrayList queryGraphData(String query) {
        return new ArrayList<>(neo4jClient.query(query)
                .fetchAs(Map.class)
                .mappedBy((typeSystem, record) -> {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("source", record.get("n").asNode().asMap());
                    Relationship rel = record.get("r").asRelationship();
                    Map<String, Object> relMap = new HashMap<>(rel.asMap());
                    relMap.put("type", rel.type());
                    resultMap.put("target", record.get("m").asNode().asMap());
                    resultMap.put("value", relMap);
                    return resultMap;
                })
                .all());
    }


}
