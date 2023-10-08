package com.panda.sport.rcs.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 投注单详细信息表
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-06-28
 */
@TableName("t_order_detail")
@ApiModel(value="TOrderDetail对象", description="投注单详细信息表")
public class TOrderDetail extends Model<TOrderDetail> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "自动编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "注单编号")
    private String betNo;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "用户id")
    private Long uid;

    @ApiModelProperty(value = "投注类型ID(对应上游的投注项ID),传给风控的")
    private Long playOptionsId;

    @ApiModelProperty(value = "运动种类编号")
    private Integer sportId;

    @ApiModelProperty(value = "运动种类名称")
    private String sportName;

    @ApiModelProperty(value = "玩法ID")
    private Integer playId;

    @ApiModelProperty(value = "玩法名称")
    private String playName;

    @ApiModelProperty(value = "赛事编号")
    private Long matchId;

    @ApiModelProperty(value = "赛事名称")
    private String matchName;

    @ApiModelProperty(value = "赛事类型：1 ：早盘赛事 ，2： 滚球盘赛事，3： 冠军盘赛事")
    private Integer matchType;

    @ApiModelProperty(value = "下注时间")
    private Long betTime;

    @ApiModelProperty(value = "盘口id")
    private Long marketId;

    @ApiModelProperty(value = "盘口类型(EU:欧盘 HK:香港盘 US:美式盘 ID:印尼盘 MY:马来盘 GB:英式盘）")
    private String marketType;

    @ApiModelProperty(value = "盘口值")
    private String marketValue;

    @ApiModelProperty(value = "对阵信息")
    private String matchInfo;

    @ApiModelProperty(value = "注单金额，指的是下注本金2位小数，投注时x10000")
    private Long betAmount;

    @ApiModelProperty(value = "注单赔率,固定2位小数 【欧洲赔率】")
    private Double oddsValue;

    @ApiModelProperty(value = "最终盘口类型(EU:欧盘 HK:香港盘 US:美式盘 ID:印尼盘 MY:马来盘 GB:英式盘）")
    private String marketTypeFinally;

    @ApiModelProperty(value = "最终赔率,可能是1/20")
    private String oddFinally;

    @ApiModelProperty(value = "是否自动接收最高赔率（1：是，0：否）")
    private Integer acceptBetOdds;

    @ApiModelProperty(value = "最高可赢金额(注单金额*注单赔率)")
    private Long maxWinAmount;

    @ApiModelProperty(value = "注单状态(0未结算 1已结算 2结算异常)")
    private Integer betStatus;

    @ApiModelProperty(value = "基准比分(下注时已产生的比分)")
    private String scoreBenchmark;

    @ApiModelProperty(value = "投注类型(投注时下注的投注类型 比如1 X 2)，规则引擎用")
    private String playOptions;

    @ApiModelProperty(value = "0:未删除，1 已删除")
    private Integer delFlag;

    @ApiModelProperty(value = "投注赛事阶段id")
    private Long matchProcessId;

    @ApiModelProperty(value = "备注 ")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "创建用户")
    private String createUser;

    @ApiModelProperty(value = "修改人")
    private String modifyUser;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "联赛id")
    private Long tournamentId;

    @ApiModelProperty(value = "是否需要和风控赛果进行对比 1:是，0：否")
    private Integer isResult;

    @ApiModelProperty(value = "注项结算结果0-无结果  2-走水  3-输 4-赢 5-赢一半 6-输一半 7-赛事取消 8-赛事延期")
    private Integer betResult;

    @ApiModelProperty(value = "投注项名称")
    private String playOptionName;

    @ApiModelProperty(value = "附加金额(实际付款金额-投注金额)")
    private Long addition;

    @ApiModelProperty(value = "注单所有可能结算结果json格式 ")
    private String betAllResult;

    @ApiModelProperty(value = "投注项结算比分")
    private String settleScore;

    @ApiModelProperty(value = "结算赛事阶段id")
    private Long settleMatchProcessId;

    @ApiModelProperty(value = "联赛级别")
    private Long tournamentLevel;

    @ApiModelProperty(value = "主副盘标识 0、主盘 1、副盘")
    private Integer marketMain;

    @ApiModelProperty(value = "赛前操盘平台")
    private String preDataSourse;

    @ApiModelProperty(value = "滚球操盘平台")
    private String liveDataSourse;

    @ApiModelProperty(value = "赔率来源")
    private String oddsDataSourse;

    @ApiModelProperty(value = "操盘类型")
    private Integer tradeType;


    public TOrderDetail() {}

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

    public Long getPlayOptionsId() {
        return playOptionsId;
    }

    public void setPlayOptionsId(Long playOptionsId) {
        this.playOptionsId = playOptionsId;
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

    public Integer getBetStatus() {
        return betStatus;
    }

    public void setBetStatus(Integer betStatus) {
        this.betStatus = betStatus;
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

    public Integer getIsResult() {
        return isResult;
    }

    public void setIsResult(Integer isResult) {
        this.isResult = isResult;
    }

    public Integer getBetResult() {
        return betResult;
    }

    public void setBetResult(Integer betResult) {
        this.betResult = betResult;
    }

    public String getPlayOptionName() {
        return playOptionName;
    }

    public void setPlayOptionName(String playOptionName) {
        this.playOptionName = playOptionName;
    }

    public Long getAddition() {
        return addition;
    }

    public void setAddition(Long addition) {
        this.addition = addition;
    }

    public String getBetAllResult() {
        return betAllResult;
    }

    public void setBetAllResult(String betAllResult) {
        this.betAllResult = betAllResult;
    }

    public String getSettleScore() {
        return settleScore;
    }

    public void setSettleScore(String settleScore) {
        this.settleScore = settleScore;
    }

    public Long getSettleMatchProcessId() {
        return settleMatchProcessId;
    }

    public void setSettleMatchProcessId(Long settleMatchProcessId) {
        this.settleMatchProcessId = settleMatchProcessId;
    }

    public Long getTournamentLevel() {
        return tournamentLevel;
    }

    public void setTournamentLevel(Long tournamentLevel) {
        this.tournamentLevel = tournamentLevel;
    }

    public Integer getMarketMain() {
        return marketMain;
    }

    public void setMarketMain(Integer marketMain) {
        this.marketMain = marketMain;
    }

    public String getPreDataSourse() {
        return preDataSourse;
    }

    public void setPreDataSourse(String preDataSourse) {
        this.preDataSourse = preDataSourse;
    }

    public String getLiveDataSourse() {
        return liveDataSourse;
    }

    public void setLiveDataSourse(String liveDataSourse) {
        this.liveDataSourse = liveDataSourse;
    }

    public String getOddsDataSourse() {
        return oddsDataSourse;
    }

    public void setOddsDataSourse(String oddsDataSourse) {
        this.oddsDataSourse = oddsDataSourse;
    }

    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }
}
