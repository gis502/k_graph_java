package com.ruoyi.system.domain.bto;


import lombok.Data;

@Data
public class QueryBTO {

    private String requestParams;
//    private String pageSizes;
    private int currentPage;
    private int pageSize;
}

