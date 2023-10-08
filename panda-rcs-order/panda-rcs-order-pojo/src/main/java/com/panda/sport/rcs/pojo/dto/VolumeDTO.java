package com.panda.sport.rcs.pojo.dto;

import java.io.Serializable;

public class VolumeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String  orderNo;

    private Long uid;

    private  Integer  sportId;

    private  Integer vipLevel;

    private  int userTagLevel;

    private Long tenantId;


    private  Integer deviceType;

    public  VolumeDTO(String  orderNo,Long uid,Integer  sportId,Integer vipLevel,int userTagLevel,Long tenantId,Integer deviceType){
        this.orderNo=orderNo;
        this.uid=uid;
        this.sportId=sportId;
        this.vipLevel=vipLevel;
        this.userTagLevel=userTagLevel;
        this.tenantId=tenantId;
        this.deviceType=deviceType;
    }
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String  orderNo) {
        this.orderNo=orderNo;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long  uid) {
        this.uid=uid;
    }

    public Integer getSportId() {
        return sportId;
    }

    public void setSportId(Integer  sportId) {
        this.sportId=sportId;
    }

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer  vipLevel) {
        this.vipLevel=vipLevel;
    }

    public int getUserTagLevel() {
        return userTagLevel;
    }

    public void setUserTagLevel(Integer  userTagLevel) {
        this.userTagLevel=userTagLevel;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long  tenantId) {
        this.tenantId=tenantId;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer  deviceType) {
        this.deviceType=deviceType;
    }
}
