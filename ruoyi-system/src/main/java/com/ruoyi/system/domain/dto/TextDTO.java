package com.ruoyi.system.domain.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TextDTO {
    private String document;
    private List<String> documents = new ArrayList<>();
    private int topN;
    private String language = "zh";
    private Map<String, Object> options = new HashMap<>();
}
