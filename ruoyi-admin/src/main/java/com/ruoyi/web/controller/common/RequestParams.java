package com.ruoyi.web.controller.common;

import lombok.Data;

@Data
public class RequestParams {
    private Integer pageNum;
    private Integer pageSize;
    private String queryValue;
}
