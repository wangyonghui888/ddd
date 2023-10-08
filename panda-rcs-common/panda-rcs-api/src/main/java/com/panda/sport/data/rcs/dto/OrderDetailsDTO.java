package com.panda.sport.data.rcs.dto;

/**
 * @author :  max
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.data.rcs.dto
 * @Description :
 * @Date: 2019-11-12
 */
public class OrderDetailsDTO {

    /*
     * 投注类型ID(对应上游的投注项ID),传给风控的
     */
    private Long playOptionsId;

    /**
     * 自动编号
     */
    private Long id;

    /**
     * 注单编号
     */
    private String betNo;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 运动种类编号
     */
    private Long sportId;

    /**
     * 运动种类名称
     */
    private String sportName;

    /**
     * 玩法ID
     */
    private Long playId;

    /**
     * 玩法名称
     */
    private String playName;

    /**
     * 赛事编号
     */
    private Long matchId;

    /**
     * 赛事名称
     */
    private String matchName;

    /**
     * 赛事类型：1 ：早盘 ，2： 滚球盘，3： 冠军盘
     */
    private Integer matchType;

    /**
     * 下注时间
     */
    private Long betTime;

    /**
     * 盘口id
     */
    private Long marketId;

    /**
     * 盘口类型(OU:欧盘 HK:香港盘 US:美式盘 ID:印尼盘 MY:马来盘 GB:英式盘）
     */
    private String marketType;

    /**
     * 盘口值
     */
    private String marketValue;


    /**
     * 盘口值
     */
    private String marketValueNew;

    /**
     * 对阵信息
     */
    private String matchInfo;

    /**
     * 注单金额，指的是下注本金2位小数，投注时x100
     */
    private Long betAmount;

    /**
     * 注单赔率,固定2位小数 【欧洲赔率】
     */
    private Double oddsValue;

    /**
     * 最终盘口类型
     */
    private String marketTypeFinally;

    /**
     * 最终赔率,固定2位小数 (结算时用到)
     */
    private String oddFinally;

    /**
     * 是否自动接收最高赔率（1：是，0：否）
     */
    private Integer acceptBetOdds;

    /**
     * 最高可赢金额(注单金额*注单赔率)
     */
    private Long maxWinAmount;

    /**
     * 注单状态(0:无效 1:有效  2:待确认)
     */
    private Integer isValid;

    /**
     * 基准比分(下注时已产生的比分)
     */
    private String scoreBenchmark;

    /**
     * 玩法投注类型(投注时下注的玩法选项)，规则引擎用
     */
    private String playOptions;

    /**
     * 投注类型范围（所有投注的可能性-范围玩法时有值）
     */
    private String playOptionsRange;

    /**
     * 0:未删除，1 已删除
     */
    private Integer delFlag;

    /**
     * 赛事阶段id
     */
    private Long matchProcessId;

    /**
     * 备注 （注单为什么无效？)
     */
    private String remark;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 修改人
     */
    private String modifyUser;

    /**
     * 修改时间
     */
    private Long modifyTime;

    /**
     * 联赛id
     */
    private Long tournamentId;

    /**
     * 是否需要和风控赛果进行对比
     */
    private Integer result;

    /*
     *投注项结算状态0未结算 1已结算
     */
    private Integer betStatus;

    /*
     *投注项结算结果0：有效，1：暂停，2：停用，3：已结算，4：已取消，5：移交
     */
    private Integer betResult;

    /*
     *串关值(单关(默认) 、双式投注,例如1/2  、三式投注,例如1/2/3   、N串1,例如4串1   、N串F,例如5串26 )
     * @return
     */
    private String seriesValue;

    /**
     * 负盘附加值
     */
    private Double addition;

    /**
     * 订单风控验证渠道  0 : 内部风控  1 : mts
     */
    private Integer riskChannel;


    //增加的字段
    /**
     * 赛事阶段  1：全场  2：上半场  3：下半场
     */
    private String playType;

    /**
     * 联赛级别
     */
    private Integer tournamentLevel;

    /**
     * 赛事所属时间期号
     */
    private String dateExpect;

    /**
     * 比赛开始时间. 比赛开始时间 UTC时间
     */
    private Long beginTime;

    /**
     * 结算时的最新比分-获取结算订单信息
     */
    private String settleScore;


    public Long getPlayOptionsId() {
        return playOptionsId;
    }

    public void setPlayOptionsId(Long playOptionsId) {
        this.playOptionsId = playOptionsId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBetNo() {
        return betNo;
    }

    public void setBetNo(String betNo) {
        this.betNo = betNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public Long getPlayId() {
        return playId;
    }

    public void setPlayId(Long playId) {
        this.playId = playId;
    }

    public String getPlayName() {
        return playName;
    }

    public void setPlayName(String playName) {
        this.playName = playName;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public Integer getMatchType() {
        return matchType;
    }

    public void setMatchType(Integer matchType) {
        this.matchType = matchType;
    }

    public Long getBetTime() {
        return betTime;
    }

    public void setBetTime(Long betTime) {
        this.betTime = betTime;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public String getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(String marketValue) {
        this.marketValue = marketValue;
    }

    public String getMarketValueNew() {
        return marketValueNew;
    }

    public void setMarketValueNew(String marketValueNew) {
        this.marketValueNew = marketValueNew;
    }

    public String getMatchInfo() {
        return matchInfo;
    }

    public void setMatchInfo(String matchInfo) {
        this.matchInfo = matchInfo;
    }

    public Long getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(Long betAmount) {
        this.betAmount = betAmount;
    }

    public Double getOddsValue() {
        return oddsValue;
    }

    public void setOddsValue(Double oddsValue) {
        this.oddsValue = oddsValue;
    }

    public String getMarketTypeFinally() {
        return marketTypeFinally;
    }

    public void setMarketTypeFinally(String marketTypeFinally) {
        this.marketTypeFinally = marketTypeFinally;
    }

    public String getOddFinally() {
        return oddFinally;
    }

    public void setOddFinally(String oddFinally) {
        this.oddFinally = oddFinally;
    }

    public Integer getAcceptBetOdds() {
        return acceptBetOdds;
    }

    public void setAcceptBetOdds(Integer acceptBetOdds) {
        this.acceptBetOdds = acceptBetOdds;
    }

    public Long getMaxWinAmount() {
        return maxWinAmount;
    }

    public void setMaxWinAmount(Long maxWinAmount) {
        this.maxWinAmount = maxWinAmount;
    }

    public Integer getIsValid() {
        return isValid;
    }

    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    public String getScoreBenchmark() {
        return scoreBenchmark;
    }

    public void setScoreBenchmark(String scoreBenchmark) {
        this.scoreBenchmark = scoreBenchmark;
    }

    public String getPlayOptions() {
        return playOptions;
    }

    public void setPlayOptions(String playOptions) {
        this.playOptions = playOptions;
    }

    public String getPlayOptionsRange() {
        return playOptionsRange;
    }

    public void setPlayOptionsRange(String playOptionsRange) {
        this.playOptionsRange = playOptionsRange;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Long getMatchProcessId() {
        return matchProcessId;
    }

    public void setMatchProcessId(Long matchProcessId) {
        this.matchProcessId = matchProcessId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
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

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Integer getBetStatus() {
        return betStatus;
    }

    public void setBetStatus(Integer betStatus) {
        this.betStatus = betStatus;
    }

    public Integer getBetResult() {
        return betResult;
    }

    public void setBetResult(Integer betResult) {
        this.betResult = betResult;
    }

    public String getSeriesValue() {
        return seriesValue;
    }

    public void setSeriesValue(String seriesValue) {
        this.seriesValue = seriesValue;
    }

    public Double getAddition() {
        return addition;
    }

    public void setAddition(Double addition) {
        this.addition = addition;
    }

    public Integer getRiskChannel() {
        return riskChannel;
    }

    public void setRiskChannel(Integer riskChannel) {
        this.riskChannel = riskChannel;
    }

    public String getPlayType() {
        return playType;
    }

    public void setPlayType(String playType) {
        this.playType = playType;
    }

    public Integer getTournamentLevel() {
        return tournamentLevel;
    }

    public void setTournamentLevel(Integer tournamentLevel) {
        this.tournamentLevel = tournamentLevel;
    }

    public String getDateExpect() {
        return dateExpect;
    }

    public void setDateExpect(String dateExpect) {
        this.dateExpect = dateExpect;
    }

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public String getSettleScore() {
        return settleScore;
    }

    public void setSettleScore(String settleScore) {
        this.settleScore = settleScore;
    }
}
