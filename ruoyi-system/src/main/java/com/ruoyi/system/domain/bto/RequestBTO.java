package com.ruoyi.system.domain.bto;

import lombok.Data;

@Data
public class RequestBTO {
    private String[] fields;
    private Integer[] ids;
    private String userId;
    private String flag;
    private String requestParams;
    private long currentPage;
    private long pageSize;
}
