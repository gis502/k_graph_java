package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
    * 震后-社会反应动态信息-国内外媒体宣传信息-新闻
    */
@Data
@TableName(value = "news")
@Builder
public class News {
    /**
     * 序号
     */
    @TableId(value = "id", type = IdType.NONE)
    private Object id;

    /**
     * 新闻网址
     */
    @TableField(value = "url")
    private String url;

    /**
     * 新闻标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 发布时间
     */
    @TableField(value = "publish_time")
    private LocalDateTime publishTime;

    /**
     * 地震ID
     */
    @TableField(value = "earthquake_id")
    private String earthquakeId;

    /**
     * 内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 图片
     */
    @TableField(value = "image")
    private String image;

    /**
     * 来源（网站中文名）
     */
    @TableField(value = "source_name")
    private String sourceName;

    /**
     * 标志（来源网站标志）
     */
    @TableField(value = "source_logo")
    private String sourceLogo;
}
