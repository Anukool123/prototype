package com.mlizhi.modules.spec.dao.model;

import java.util.Date;

public class DetectModel {
    private int detectType;
    private double detectValue;
    private Long id;
    private Date insertTime;
    private int nurserType;
    private int partType;
    private String userId;

    public DetectModel(Long id) {
        this.id = id;
    }

    public DetectModel(Long id, int partType, int detectType, int nurserType, double detectValue, String userId, Date insertTime) {
        this.id = id;
        this.partType = partType;
        this.detectType = detectType;
        this.nurserType = nurserType;
        this.detectValue = detectValue;
        this.insertTime = insertTime;
        this.userId = userId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPartType() {
        return this.partType;
    }

    public void setPartType(int partType) {
        this.partType = partType;
    }

    public int getDetectType() {
        return this.detectType;
    }

    public void setDetectType(int detectType) {
        this.detectType = detectType;
    }

    public int getNurserType() {
        return this.nurserType;
    }

    public void setNurserType(int nurserType) {
        this.nurserType = nurserType;
    }

    public double getDetectValue() {
        return this.detectValue;
    }

    public void setDetectValue(double detectValue) {
        this.detectValue = detectValue;
    }

    public Date getInsertTime() {
        return this.insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String toString() {
        return "DetectModel [id=" + this.id + ", partType=" + this.partType + ", detectType=" + this.detectType + ", nurserType=" + this.nurserType + ", detectValue=" + this.detectValue + ", insertTime=" + this.insertTime + ", userId=" + this.userId + "]";
    }
}
