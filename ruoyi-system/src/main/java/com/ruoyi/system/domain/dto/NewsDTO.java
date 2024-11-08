package com.ruoyi.system.domain.dto;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class NewsDTO {
    private String id;
    private String url;
    private String title;
    private LocalDateTime publishTime;
    private String content;
    private String image;
    private String sourceName;
    private String sourceLogo;


}
