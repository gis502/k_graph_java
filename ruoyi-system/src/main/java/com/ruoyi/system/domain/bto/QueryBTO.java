package com.ruoyi.system.domain.bto;


import lombok.Data;

@Data
public class QueryBTO {

    private String flag;
    private String requestParams;
    private long currentPage;
    private long pageSize;
}

