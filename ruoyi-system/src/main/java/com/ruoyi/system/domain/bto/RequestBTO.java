package com.ruoyi.system.domain.bto;

import lombok.Data;

import java.util.UUID;

@Data
public class RequestBTO {
    private String[] fields;
    private String [] ids;
    private String userId;
    private String flag;
    private String requestParams;
    private long currentPage;
    private long pageSize;
}
