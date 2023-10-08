package com.panda.sport.rcs.common.vo.api.request;

import com.panda.sport.rcs.common.bean.PageBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel(value = " 根据IP查询投注统计信息 vo", description = "")
public class IpListReqVo  extends PageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private Long[] ids;

    @ApiModelProperty(value = "开始时间")
    private Long startFinalTime;

    @ApiModelProperty(value = "结束时间")
    private Long endFinalTime;

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "IP标签")
    private Integer[] tagId;

    @ApiModelProperty(value = "最小累计盈亏")
    private BigDecimal profitAmount;

    @ApiModelProperty(value = "最小近7日输赢金额")
    private BigDecimal sevenDaysProfitAmount;

    /**
     * 排序字段名称
     */
    @ApiModelProperty(value = "排序字段名称")
    private String sortName;

    /**
     * 排序类型 1正序 2倒序
     */
    @ApiModelProperty(value = "排序类型 1正序 2倒序")
    private Integer sortType;

    public Integer getSortType() {
        return sortType;
    }

    public void setSortType(Integer sortType) {
        this.sortType = sortType;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public Long[] getIds() {
        return ids;
    }

    public void setId(Long[] ids) {
        this.ids = ids;
    }

    public Long getStartFinalTime() {
        return startFinalTime;
    }

    public void setStartFinalTime(Long startFinalTime) {
        this.startFinalTime = startFinalTime;
    }

    public Long getEndFinalTime() {
        return endFinalTime;
    }

    public void setEndFinalTime(Long endFinalTime) {
        this.endFinalTime = endFinalTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }

    public Integer[] getTagId() {
        return tagId;
    }

    public void setTagId(Integer[] tagId) {
        this.tagId = tagId;
    }

    public BigDecimal getSevenDaysProfitAmount() {
        return sevenDaysProfitAmount;
    }

    public void setSevenDaysProfitAmount(BigDecimal sevenDaysProfitAmount) {
        this.sevenDaysProfitAmount = sevenDaysProfitAmount;
    }
}
