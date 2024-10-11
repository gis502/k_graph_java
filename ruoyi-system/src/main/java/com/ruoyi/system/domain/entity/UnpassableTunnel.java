package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-生命线修复信息-交通设施破坏类-不可通行隧道
    */
@Data
@TableName(value = "unpassable_tunnel")
public class UnpassableTunnel {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 隧道名称
     */
    @TableField(value = "tunnel_name")
    private String tunnelName;

    /**
     * 所在线路
     */
    @TableField(value = "line")
    private String line;

    /**
     * 隧道类型
     */
    @TableField(value = "tunnel_type")
    private String tunnelType;

    /**
     * 人员伤亡
     */
    @TableField(value = "casualties")
    private String casualties;

    /**
     * 破坏形式
     */
    @TableField(value = "damage_form")
    private String damageForm;

    /**
     * 处置队伍
     */
    @TableField(value = "response_team")
    private String responseTeam;

    /**
     * 处置措施
     */
    @TableField(value = "response_measures")
    private String responseMeasures;

    /**
     * 先期处置阶段
     */
    @TableField(value = "initial_response_stage")
    private String initialResponseStage;

    /**
     * 开始处置日期
     */
    @TableField(value = "start_response_date")
    private Date startResponseDate;

    /**
     * 预计完成处置日期
     */
    @TableField(value = "expected_completion_date")
    private Date expectedCompletionDate;

    /**
     * 实际完成处置日期
     */
    @TableField(value = "actual_completion_date")
    private Date actualCompletionDate;

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
     * 标绘id
     */
    @TableField(value = "plot_id")
    private String plotId;
}
