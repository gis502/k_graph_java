package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-应急处置信息-人员信息-救援行动类_失踪人员表
    */
@Data
@TableName(value = "rescue_action_missing_persons")
public class RescueActionMissingPersons {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 失联位置
     */
    @TableField(value = "missing_location")
    private String missingLocation;

    /**
     * 新增失踪人数：人
     */
    @TableField(value = "new_missing_count")
    private Integer newMissingCount;

    /**
     * 累计失踪人数：人
     */
    @TableField(value = "total_missing_count")
    private Integer totalMissingCount;

    /**
     * 失联原因：埋压、淹溺、其他
     */
    @TableField(value = "missing_reason")
    private String missingReason;

    /**
     * 搜寻队伍
     */
    @TableField(value = "search_team")
    private String searchTeam;

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

    /**
     * 记录时间
     */
    @TableField(value = "record_time")
    private Date recordTime;
}