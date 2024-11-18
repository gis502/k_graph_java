package com.ruoyi.system.domain.bto;

import com.ruoyi.system.domain.vo.FormVO;
import lombok.Data;

import java.util.UUID;

@Data
public class RequestBTO {

    // 搜索条件
    private String[] fields;
    private String [] ids;
    private String userId;
    private String flag;
    private String queryEqId;
    private String requestParams;
    private long currentPage;
    private long pageSize;

    private FormVO formVO;
    private Integer condition; // 判断是搜索功能还是筛选功能

}
