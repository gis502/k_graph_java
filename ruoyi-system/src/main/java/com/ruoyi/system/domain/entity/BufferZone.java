package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "buffer_zone")
public class BufferZone {
    /**
     * 唯一标识符
     */
    @TableField(value = "uuid")
    private Object uuid;

    /**
     * 标绘ID
     */
    @TableField(value = "plot_id")
    private String plotId;

    /**
     * 缓存区面积（m²）
     */
    @TableField(value = "area")
    private String area;

    /**
     * 容纳人数
     */
    @TableField(value = "capacity")
    private String capacity;

    /**
     * 基本保障（食宿,通电,通水,通网）
     */
    @TableField(value = "basic_support")
    private String basicSupport;

    /**
     * 联系人员
     */
    @TableField(value = "contact_person")
    private String contactPerson;

    /**
     * 联系电话
     */
    @TableField(value = "contact_phone")
    private String contactPhone;
}