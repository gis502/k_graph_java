package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@TableName(value = "earthquake_list")
public class EarthquakeList implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    @TableId(value = "eqid")
    private String eqid;
    private String earthquakeName;
    private String providingDepartment;
    private String geom;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime occurrenceTime;
    private String magnitude;
    private String depth;
    private String intensity;
    private String epicenterName;
    private String city;
    private String province;

    public String getEqid() {
        return eqid;
    }

    public void setEqid(String eqid) {
        this.eqid = eqid;
    }

    public String  getEarthquakeName(String earthquakeName) {
        return earthquakeName;
    }

    public void setEarthquakeName(String earthquakeName) {
        this.earthquakeName = earthquakeName;
    }

    public String getProvidingDepartment(String providingDepartment) {
        return providingDepartment;
    }

    public void setProvidingDepartment(String providingDepartment) {
        this.providingDepartment = providingDepartment;
    }

    public String getGeom(String geom) {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
    }

    public LocalDateTime getOccurrenceTime() {
        return occurrenceTime;
    }

    public void setOccurrenceTime(LocalDateTime occurrenceTime) {
        this.occurrenceTime = occurrenceTime;
    }

    public String getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(String magnitude) {
        this.magnitude = magnitude;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public String getEpicenterName() {
        return epicenterName;
    }

    public void setEpicenterName(String epicenterName) {
        this.epicenterName = epicenterName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Override
    public String toString() {
        return "EarthquakeList{" +
                "eqid='" + eqid + '\'' +
                ", earthquakeName='" + earthquakeName + '\'' +
                ", providingDepartment" + providingDepartment + '\'' +
                ", geom='" + geom + '\'' +
                ", occurrenceTime=" + occurrenceTime +
                ", magnitude='" + magnitude + '\'' +
                ", depth='" + depth + '\'' +
                ", intensity='" + intensity + '\'' +
                ", epicenterName='" + epicenterName + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                '}';
    }
}
