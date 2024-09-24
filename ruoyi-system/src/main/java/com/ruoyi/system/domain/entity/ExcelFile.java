package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
    * 导出功能-字段
    */
@Data
@TableName(value = "excel_file")
public class ExcelFile {
    @TableId(value = "id", type = IdType.NONE)
    private Short id;

    @TableField(value = "file_name")
    private String fileName;

    @TableField(value = "file_column")
    private String fileColumn;
}