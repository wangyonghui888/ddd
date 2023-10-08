package com.panda.sport.rcs.mgr.mq.bean;


import com.panda.sport.rcs.pojo.AmountTypeVo;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @Describtion
 * @Auther jstyDC
 * @Date 2022-02-2022/2/21 21:19
 */
public class HideOrderDTO implements Serializable {

    private AmountTypeVo amountTypeVo;



    public HideOrderDTO(AmountTypeVo amountTypeVo, String orderNo, Integer deviceType, BigDecimal volumePercentage, Integer vipLeve){
        this.amountTypeVo=amountTypeVo;
        this.orderNo=orderNo;
        this.deviceType=deviceType;
        this.volumePercentage=volumePercentage;
        this.vipLeve=vipLeve;
    }
    /**
     * 单号
     * */
    private String orderNo;

    /***
     * 货量
     */
    private BigDecimal volumePercentage;


    /***
     * 设备类型
     */
    private Integer deviceType;

    /**
     * Vip 类型
     * */
    private Integer vipLeve;


    public AmountTypeVo getAmountTypeVo() {
        return amountTypeVo;
    }

//    public void setAmountTypeVo(AmountTypeVo amountTypeVo) {
//        this.amountTypeVo = amountTypeVo;
//    }

    public BigDecimal getVolumePercentage() {
        return volumePercentage;
    }

//    public void setVolumePercentage(BigDecimal volumePercentage) {
//        this.volumePercentage = volumePercentage;
//    }

    public String getOrderNo() {
        return orderNo;
    }


    public Integer getVipLeve() {
        return vipLeve;
    }


    public Integer getDeviceType() {
        return deviceType;
    }

//    public void setOrderNo(String orderNo) {
//        this.orderNo = orderNo;
//    }
}
