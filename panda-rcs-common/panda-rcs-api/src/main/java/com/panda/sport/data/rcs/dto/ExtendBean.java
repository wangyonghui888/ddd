package com.panda.sport.data.rcs.dto;

import com.panda.sport.rcs.pojo.RcsBusinessPlayPaidConfig;
import com.panda.sport.rcs.pojo.RcsBusinessSingleBetConfig;
import com.panda.sport.rcs.pojo.RcsBusinessUserPaidConfig;
import lombok.Data;

import java.io.Serializable;
public class ExtendBean implements Serializable{
	
    /**
    * 下注金额
    * */
    private Long orderMoney;

    /**
     * 赛事id
     */
    private String matchId;
    
    /**
     * 玩法id  -1表示其他玩法
     */
    private String playId;
    
    /**
     * 如果当前玩法盘口计算需要当前比分计算最后结果
     * 盘口 = 盘口 + 当前比分拼接
     */
    private String handicap;
    
    /**
     * 商户id
     */
    private String busId;

    /**
     * 盘口id
     */
    private String marketId;

    /**体育类型
     * 
     */
    private String sportId;

    /**
     * 是否滚球  0:赛前 1：滚球
     */
    private String isScroll;

    /**
     * 赛事阶段  1：全场  2：上半场  3：下半场 
     */
    private String playType;
    
    /**
     * 选项id
     */
    private String selectId;

    /**
     * 联赛级别
     */
    private Integer tournamentLevel;
    
    /**
     * mts使用
     */
    private Long mtsAmount;


    /**
     * 联赛Id
     */
    private Long tournamentId;
    
    /**
     * 当前比分
     */
    private String currentScore;
    
    /**
         * 用户ID
     */
    private String userId;
    
    /**
     * 赔率
     */
    private String odds;
    
    
    /**
     * 订单id
     */
    private String itemId;
    
    /**
     * 注订单id
     */
    private String orderId;
    
    /**
     * 当前玩法是否跟比分相关
     */
    private Boolean isRelationScore;
    
    /**
     * 当前订单保存维度类型
     */
    private String dimeType;
    
    /**
     * 当前订单保存维度对应值
     */
    private String dimeVal;
    
    /**
     * 派奖金额
     */
    private Long settleAmount;
    
    
    /**
     * 结算时间
     */
    private Long settleTime;
    
    /**
     * 结算差值时间
     */
    private Long profit;
    
    /**
     * 比分矩阵类型
     * 0:可用于比分推算的
     * 1：不可用比分推算的
     * 2：红黄牌比分推算
     * 3：角球比分矩阵推算
     */
    private Integer recType;
    
    /**
     * 对应类型的值
     */
    private String recVal;
    
    /**
     * 当前订单最大赔付值
     * 第一次查询的时候，为0
     */
    private Long currentMaxPaid;
    
    /**
     * 限额类型，1-标准模式，2-信用模式
     */
    private Integer limitType;

    /**
     * 赛事所属时间期号
     */
    private String dateExpect;
    
    /**
     * 玩法配置
     */
    private RcsBusinessPlayPaidConfig playConfig;
    
    /**
     * 用户玩法配置
     */
    private RcsBusinessUserPaidConfig userConfig;

    /**
     * 单关配置
     */
    private RcsBusinessSingleBetConfig singleBetConfig;
    
    /**
     * 校验结果
     * 注单状态  0:待处理(初始化) 1：成功  2：失败 3:已取消
     */
    private Integer validateResult;
    
    
    /**
     *单关还是串关
     * 串关类型(0:单注(默认) 1:双式投注,例如1/2  2:三式投注,例如1/2/3   3:N串1,例如4串1   4:N串F,例如5串26 )
     */
    private Integer seriesType;
    
    
    /**
     * 当前玩法归类
     */
    private Integer currentPlayType;
    
    /**
     * 对应的itemBean
     */
    private OrderItem itemBean;
    /**
     * MTS 下单时 说明符
     */
    private String specifiers;

    /**
     * MTS 下单时 SR赛事ID
     */
    private String thirdTemplateSourceId;

    /**
     * 数据源
     */
    private String dataSourceCode;

    /**
     * MTS 第三方赛事ID
     */
    private String thirdMatchSourceId;
    
    /**
     * 是否冠军盘  0否 1是
     */
    private Integer isChampion ;

    /**
     * 订单风控验证渠道  1 : 内部风控  2 : mts
     */
    private String riskChannel;

    /**
     * 子玩法标识
     */
    private String subPlayId;

    /**
     * 一级标签 用户等级
     */
    private int userTagLevel ;

    /**
     * gts第三方 赛事盘口投注项等 原始组装字符串 从上游下发而来
     */
    private String gtsThirdData ;

    public int getUserTagLevel() {
        return userTagLevel;
    }

    public void setUserTagLevel(int userTagLevel) {
        this.userTagLevel = userTagLevel;
    }

    public String getSubPlayId() {
        return subPlayId;
    }

    public void setSubPlayId(String subPlayId) {
        this.subPlayId = subPlayId;
    }

    public Long getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(Long orderMoney) {
        this.orderMoney = orderMoney;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public Integer getIsChampion() {
		return isChampion;
	}

	public void setIsChampion(Integer isChampion) {
		this.isChampion = isChampion;
	}

	public String getPlayId() {
        return playId;
    }

    public void setPlayId(String playId) {
        this.playId = playId;
    }

    public String getHandicap() {
        return handicap;
    }

    public Long getMtsAmount() {
		return mtsAmount;
	}

	public void setMtsAmount(Long mtsAmount) {
		this.mtsAmount = mtsAmount;
	}

	public void setHandicap(String handicap) {
        this.handicap = handicap;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public String getSportId() {
        return sportId;
    }

    public void setSportId(String sportId) {
        this.sportId = sportId;
    }

    public String getIsScroll() {
        return isScroll;
    }

    public void setIsScroll(String isScroll) {
        this.isScroll = isScroll;
    }

    public String getPlayType() {
        return playType;
    }

    public void setPlayType(String playType) {
        this.playType = playType;
    }

    public String getSelectId() {
        return selectId;
    }

    public void setSelectId(String selectId) {
        this.selectId = selectId;
    }

    public Integer getTournamentLevel() {
        return tournamentLevel;
    }

    public void setTournamentLevel(Integer tournamentLevel) {
        this.tournamentLevel = tournamentLevel;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Integer getLimitType() {
		return limitType;
	}

	public void setLimitType(Integer limitType) {
		this.limitType = limitType;
	}

	public String getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(String currentScore) {
        this.currentScore = currentScore;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOdds() {
        return odds;
    }

    public void setOdds(String odds) {
        this.odds = odds;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Boolean getRelationScore() {
        return isRelationScore;
    }

    public void setRelationScore(Boolean relationScore) {
        isRelationScore = relationScore;
    }

    public Boolean getIsRelationScore() {
        return isRelationScore;
    }

    public void setIsRelationScore(Boolean relationScore) {
        isRelationScore = relationScore;
    }


    public String getDimeType() {
        return dimeType;
    }

    public void setDimeType(String dimeType) {
        this.dimeType = dimeType;
    }

    public String getDimeVal() {
        return dimeVal;
    }

    public void setDimeVal(String dimeVal) {
        this.dimeVal = dimeVal;
    }

    public Long getSettleAmount() {
        return settleAmount;
    }

    public void setSettleAmount(Long settleAmount) {
        this.settleAmount = settleAmount;
    }

    public Long getSettleTime() {
        return settleTime;
    }

    public void setSettleTime(Long settleTime) {
        this.settleTime = settleTime;
    }

    public Long getProfit() {
        return profit;
    }

    public void setProfit(Long profit) {
        this.profit = profit;
    }

    public Integer getRecType() {
        return recType;
    }

    public void setRecType(Integer recType) {
        this.recType = recType;
    }

    public String getRecVal() {
        return recVal;
    }

    public void setRecVal(String recVal) {
        this.recVal = recVal;
    }

    public Long getCurrentMaxPaid() {
        return currentMaxPaid;
    }

    public void setCurrentMaxPaid(Long currentMaxPaid) {
        this.currentMaxPaid = currentMaxPaid;
    }

    public String getDateExpect() {
        return dateExpect;
    }

    public void setDateExpect(String dateExpect) {
        this.dateExpect = dateExpect;
    }

    public RcsBusinessPlayPaidConfig getPlayConfig() {
        return playConfig;
    }

    public void setPlayConfig(RcsBusinessPlayPaidConfig playConfig) {
        this.playConfig = playConfig;
    }

    public RcsBusinessUserPaidConfig getUserConfig() {
        return userConfig;
    }

    public void setUserConfig(RcsBusinessUserPaidConfig userConfig) {
        this.userConfig = userConfig;
    }

    public RcsBusinessSingleBetConfig getSingleBetConfig() {
        return singleBetConfig;
    }

    public void setSingleBetConfig(RcsBusinessSingleBetConfig singleBetConfig) {
        this.singleBetConfig = singleBetConfig;
    }

    public Integer getValidateResult() {
        return validateResult;
    }

    public void setValidateResult(Integer validateResult) {
        this.validateResult = validateResult;
    }

    public Integer getSeriesType() {
        return seriesType;
    }

    public void setSeriesType(Integer seriesType) {
        this.seriesType = seriesType;
    }

    public Integer getCurrentPlayType() {
        return currentPlayType;
    }

    public void setCurrentPlayType(Integer currentPlayType) {
        this.currentPlayType = currentPlayType;
    }

    public OrderItem getItemBean() {
        return itemBean;
    }

    public void setItemBean(OrderItem itemBean) {
        this.itemBean = itemBean;
    }

    public String getSpecifiers() {
        return specifiers;
    }

    public void setSpecifiers(String specifiers) {
        this.specifiers = specifiers;
    }

    public String getThirdTemplateSourceId() {
        return thirdTemplateSourceId;
    }

    public void setThirdTemplateSourceId(String thirdTemplateSourceId) {
        this.thirdTemplateSourceId = thirdTemplateSourceId;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getThirdMatchSourceId() {
        return thirdMatchSourceId;
    }

    public void setThirdMatchSourceId(String thirdMatchSourceId) {
        this.thirdMatchSourceId = thirdMatchSourceId;
    }

    public String getRiskChannel() {
        return riskChannel;
    }

    public void setRiskChannel(String riskChannel) {
        this.riskChannel = riskChannel;
    }

    public String getGtsThirdData() {
        return gtsThirdData;
    }

    public void setGtsThirdData(String gtsThirdData) {
        this.gtsThirdData = gtsThirdData;
    }
}
