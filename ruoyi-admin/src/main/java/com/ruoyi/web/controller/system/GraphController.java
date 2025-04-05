package com.ruoyi.web.controller.system;

import org.neo4j.driver.types.Relationship;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/graph")
public class GraphController {

    private final Neo4jClient neo4jClient;

    public GraphController(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @GetMapping("/getGraph")
    public List getFullGraph() {
        String query = "MATCH (n)-[r]->(m) RETURN n, r, m";
        return new ArrayList<>(neo4jClient.query(query)
                .fetchAs(Map.class)
                .mappedBy((typeSystem, record) -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("source", record.get("n").asNode().asMap());
                    Relationship rel = record.get("r").asRelationship();
                    Map<String, Object> relMap = new HashMap<>(rel.asMap());
                    relMap.put("type", rel.type()); // 可选：添加关系类型
                    result.put("target", record.get("m").asNode().asMap());
                    result.put("value", relMap);
                    return result;
                })
                .all());
    }
}



