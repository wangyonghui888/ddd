package com.panda.sport.data.rcs.dto;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;

/**
 * @author :  holly
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.data.rcs.dto
 * @Description :
 * @Date: 2019-10-08 17:08
 */
public class SettleItem {

    private static final long serialVersionUID = 3470224029895086736L;
    /**
     * 1风控拒单
     * 2业务拒单
     */
    private Integer operatorType;

    /**
     * 数据源给的赛果
     */
    private String sourceResult;

    /**
     * 表id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long uid;


    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 商户id
     */
    private Long merchantId;


    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    /**
     * @Description  结算注单号
     * @Param
     * @Author  max
     * @Date  12:39 2019/11/2
     * @return
     **/
    private String betNo;


    /**
     * 结算金额(最终要除以100，并按照四舍六入五成双取2位小数)
     */
    private Long settleAmount;

    /**
     * 派彩状态 0 未派彩，1已派彩
     */
    private Integer payoutStatus = 0;


    /**
     * 是否已派彩 0：未派彩 1：已派彩
     */
    private Integer settleStatus;

    /**
     * 结算类型(1:自动结算，0：手工结算)
     */
    private Integer settleType = 1;

    /**
     * 结算时间
     */
    private Long settleTime;

    /**
     * 最终赔率
     */
    private String oddFinally;

    /**
     * @Description  赔率
     * @Param
     * @Author  max
     * @Date  14:17 2019/11/2
     * @return
     **/

    private Double oddsValue;

    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改人
     */
    private String modifyUser;

    /**
     * 修改时间
     */
    private Long modifyTime;

    /**
     * 0:未删除，1已删除
     */
    private Integer delFlag;

    /**
     * 备注（如赛事延期、比分有误等）
     */
    private String remark;

    /**
     * 注单本金 说明该值是乘以100之后的金额
     */
    private Long betAmount;

    /**
     * 结算时的最新比分-获取结算订单信息
     */
    private String settleScore;

    /**
     *
     * 注项结算结果0-无结果  2-走水  3-输 4-赢 5-赢一半 6-输一半 7-赛事取消，8-赛事延期 9-拒单
     */
    private Integer outCome;

    /**
     * 订单结算状态 0：未结算  1：已结算  2：结算异常
     */
    private Integer orderStatus;
    /**
     * 串关类型(1：单关(默认)  )
     */
    private Integer seriesType;

    /**
     * 渠道编码，风控自己使用
     * MTS，PA
     */
    private Integer channelCode;
    
    private String riskChannel;
    
    private Boolean isSuccess;
    
    @TableField(exist = false)
    private Long  betTime;


    /**
     * @Description   已结算的注单号
     * @Param
     * @Author  max
     * @Date  11:00 2019/11/2
     * @return
     **/
    private List<OrderDetailPO> orderDetailRisk;

    public String getSourceResult() {
        return sourceResult;
    }

    public void setSourceResult(String sourceResult) {
        this.sourceResult = sourceResult;
    }

    public Long getBetTime() {
		return betTime;
	}

	public void setBetTime(Long betTime) {
		this.betTime = betTime;
	}

	public Long getId() {
        return id;
    }

    public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRiskChannel() {
		return riskChannel;
	}

	public void setRiskChannel(String riskChannel) {
		this.riskChannel = riskChannel;
	}

	public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public Integer getOperatorType() {
		return operatorType;
	}

	public void setOperatorType(Integer operatorType) {
		this.operatorType = operatorType;
	}

	public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getBetNo() {
        return betNo;
    }

    public void setBetNo(String betNo) {
        this.betNo = betNo;
    }

    public Long getSettleAmount() {
        return settleAmount;
    }

    public void setSettleAmount(Long settleAmount) {
        this.settleAmount = settleAmount;
    }

    public Integer getPayoutStatus() {
        return payoutStatus;
    }

    public void setPayoutStatus(Integer payoutStatus) {
        this.payoutStatus = payoutStatus;
    }

    public Integer getSettleStatus() {
        return settleStatus;
    }

    public void setSettleStatus(Integer settleStatus) {
        this.settleStatus = settleStatus;
    }

    public Integer getSettleType() {
        return settleType;
    }

    public void setSettleType(Integer settleType) {
        this.settleType = settleType;
    }

    public Long getSettleTime() {
        return settleTime;
    }

    public void setSettleTime(Long settleTime) {
        this.settleTime = settleTime;
    }

    public String getOddFinally() {
        return oddFinally;
    }

    public void setOddFinally(String oddFinally) {
        this.oddFinally = oddFinally;
    }

    public Double getOddsValue() {
        return oddsValue;
    }

    public void setOddsValue(Double oddsValue) {
        this.oddsValue = oddsValue;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(Long betAmount) {
        this.betAmount = betAmount;
    }

    public String getSettleScore() {
        return settleScore;
    }

    public void setSettleScore(String settleScore) {
        this.settleScore = settleScore;
    }

    public Integer getOutCome() {
        return outCome;
    }

    public void setOutCome(Integer outCome) {
        this.outCome = outCome;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getSeriesType() {
        return seriesType;
    }

    public void setSeriesType(Integer seriesType) {
        this.seriesType = seriesType;
    }

    public List<OrderDetailPO> getOrderDetailRisk() {
        return orderDetailRisk;
    }

    public void setOrderDetailRisk(List<OrderDetailPO> orderDetailRisk) {
        this.orderDetailRisk = orderDetailRisk;
    }

	public Integer getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(Integer channelCode) {
		this.channelCode = channelCode;
	}
    /**
     * 是否为派奖标记
     */
    private Integer isSettled;

    public Integer getIsSettled() {
        return isSettled;
    }

    public void setIsSettled(Integer isSettled) {
        this.isSettled = isSettled;
    }
}
