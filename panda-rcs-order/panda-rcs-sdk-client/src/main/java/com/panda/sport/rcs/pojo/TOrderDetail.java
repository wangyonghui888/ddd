package com.panda.sport.rcs.pojo;


import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 投注单详细信息表
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-07
 */

public class TOrderDetail  implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
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
    private Integer sportId;

    /**
     * 运动种类名称
     */
    private String sportName;

    /**
     * 玩法ID
     */
    private Integer playId;

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
     * 下注时间
     */
    private Long betTime;

    /**
     * 类型：1 ：早盘 ，2： 滚球盘， 3： 冠军盘
     */
    private Integer matchType;

    /**
     * 盘口类型(OU:欧盘 HK:香港盘 US:美式盘 ID:印尼盘 MY:马来盘 GB:英式盘）
     */
    private String marketType;

    /**
     * 盘口Id
     */
    private Long marketId;
    /**
     * 盘口值
     */
    private String marketValue;

    /**
     * 对阵信息
     */
    private String matchInfo;

    /**
     * 注单金额，指的是下注本金2位小数，投注时x100
     */
    private Long betAmount;

    /**
     * 除了100
     * @return
     */
    public BigDecimal getBetAmount1(){
        if(betAmount == null){
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(betAmount).divide(BigDecimal.valueOf(100));
    }

    /**
     * 注单赔率,固定2位小数 (结算时用到)
     */
    private Double oddsValue;

    /**
     * 变化前的赔率
     */
    private String oddFinally;

    /**
     * 是否自动接收最高赔率
     */
    private Integer acceptBetOdds;

    /**
     * 最高可赢金额
     */
    private Double maxWinAmount;

    /**
     * 返还用户金额
     * @return
     */
    public BigDecimal getPaidAmount(){
        return BigDecimal.valueOf(betAmount).multiply(new BigDecimal(Double.toString(oddsValue)));
    }

    /**
     * 获取期望值
     * 下注金额 - 最大赔付金额
     * @return
     */
    public BigDecimal getProfitValueAmount(){
        return getBetAmount1().subtract(getPaidAmount());
    }

    /**
     * 注单状态(0:无效 1:有效  2:待确认)
     */
    private Integer isValid;

    /**
     * 基准比分(下注时已产生的比分)
     */
    private String scoreBenchmark;

    /**
     * 投注类型ID(对应上游的投注项ID),传给风控的
     */
    private Long playOptionsId;

    /**
     * 投注类型(投注时下注的玩法选项)，规则引擎用
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
    private String isResult;

    /**
     * 当前订单矩阵类型
     */
    private Integer recType;

    /**
     * 订单对应推算的矩阵数据
     */
    private String recVal;

    /**
     * @Description 计算矩阵是否需要当前比分
     * 0 不需要
     * 1 需要
     * @Param
     * @Author max
     * @Date 13:38 2019/10/12
     * @return
     **/
    private int isRelationScore;

    /**
     * 校验结果  1：成功  2：失败
     */
    private Integer validateResult;

    /**
     * 注单状态：1-已结算；2-未结算
     */
    private Integer isSettlement;

    /**
     * 订单风控验证渠道  1 : 内部风控  2 : mts
     */
    private Integer riskChannel;

    /**
     * mts注单状态(0:待处理,1:已接单,2:已拒单,3:已取消)
     */
    private Integer mtsOrderStatus;

    /**
     * 当前用户的标签 货量百分比
     */
    private BigDecimal volumePercentage;

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

    public Integer getSportId() {
        return sportId;
    }

    public void setSportId(Integer sportId) {
        this.sportId = sportId;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public Integer getPlayId() {
        return playId;
    }

    public void setPlayId(Integer playId) {
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

    public Long getBetTime() {
        return betTime;
    }

    public void setBetTime(Long betTime) {
        this.betTime = betTime;
    }

    public Integer getMatchType() {
        return matchType;
    }

    public void setMatchType(Integer matchType) {
        this.matchType = matchType;
    }

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public String getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(String marketValue) {
        this.marketValue = marketValue;
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

    public Double getMaxWinAmount() {
        return maxWinAmount;
    }

    public void setMaxWinAmount(Double maxWinAmount) {
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

    public Long getPlayOptionsId() {
        return playOptionsId;
    }

    public void setPlayOptionsId(Long playOptionsId) {
        this.playOptionsId = playOptionsId;
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

    public String getIsResult() {
        return isResult;
    }

    public void setIsResult(String isResult) {
        this.isResult = isResult;
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

    public int getIsRelationScore() {
        return isRelationScore;
    }

    public void setIsRelationScore(int isRelationScore) {
        this.isRelationScore = isRelationScore;
    }

    public Integer getValidateResult() {
        return validateResult;
    }

    public void setValidateResult(Integer validateResult) {
        this.validateResult = validateResult;
    }

    public Integer getIsSettlement() {
        return isSettlement;
    }

    public void setIsSettlement(Integer isSettlement) {
        this.isSettlement = isSettlement;
    }

    public Integer getRiskChannel() {
        return riskChannel;
    }

    public void setRiskChannel(Integer riskChannel) {
        this.riskChannel = riskChannel;
    }

    public Integer getMtsOrderStatus() {
        return mtsOrderStatus;
    }

    public void setMtsOrderStatus(Integer mtsOrderStatus) {
        this.mtsOrderStatus = mtsOrderStatus;
    }

    public BigDecimal getVolumePercentage() {
        return volumePercentage;
    }

    public void setVolumePercentage(BigDecimal volumePercentage) {
        this.volumePercentage = volumePercentage;
    }


}
