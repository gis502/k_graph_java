package com.ruoyi.system.domain.bto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PlotRequestBTO {
    private String[] fields;
    private String eqTitle;
    private List<Map<String, Object>> headersAndContent;
    private String fileName;
}

