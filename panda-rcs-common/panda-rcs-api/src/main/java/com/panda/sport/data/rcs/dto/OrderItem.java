package com.panda.sport.data.rcs.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <p>
 * 投注单详细信息表
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-03
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class OrderItem implements Serializable {

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
     * 盘口值
     */
    private String marketValue;

    /**
     * 让球基于基准分的盘口值
     */
    private String marketValueNew;

    /**
     * 对阵信息
     */
    private String matchInfo;

    /**
     * 注单金额，指的是下注本金2位小数，投注时x100  单位：分
     */
    private Long betAmount;

    public static int PlUSTIMES = 100;
    /**
     * @Description 订单扫描状态 0 未处理 1 已处理
     * @Param
     * @Author toney
     * @Date 11:40 2020/1/31
     * @return
     **/
    @TableField(exist = false)
    private Integer handleStatus = 0;

    /**
     * @Description 盘口位置，区分主副盘以及副盘位置
     */
    @TableField(exist = false)
    private Integer placeNum;

    /**
     * 暂停倒计时（秒）
     */
    @TableField(exist = false)
    private Integer pauseTime;

    /**
     * 提前結算狀態
     */
    @TableField(exist = false)
    private Integer preSettleStatus;
    /**
     * 提前結算賠率
     */
    @TableField(exist = false)
    private Double preSettleOdss;
    /**
     * 提前結算耗時
     */
    @TableField(exist = false)
    private Integer preSettleTime;

    /**
     * @return java.math.BigDecimal
     * @Description 除以100
     * @Param []
     * @Author toney
     * @Date 16:55 2019/12/16
     **/
    public BigDecimal getHandledBetAmout() {
        if (betAmount == null) {
            betAmount = 0L;
        }
        return BigDecimal.valueOf(this.betAmount).divide(BigDecimal.valueOf(PlUSTIMES));
    }

    /**
     * 盘口ID
     */
    private Long marketId;

    public Double getHandleAfterOddsValue() {
        if (oddsValue == null) {
            return 0D;
        }
        return new BigDecimal(oddsValue + "").divide(new BigDecimal("100000")).setScale(2, RoundingMode.DOWN).doubleValue();
    }

    /**
     * @return java.math.BigDecimal
     * @Description 赔付金额
     * @Param []
     * @Author toney
     * @Date 18:38 2020/2/6
     **/
    public BigDecimal getPaidAmount() {
        return BigDecimal.valueOf(getBetAmount()).multiply(new BigDecimal(String.valueOf(getHandleAfterOddsValue())));
    }

    public static void main(String[] args) {
        OrderItem item = new OrderItem();
        item.setBetAmount(10000l);
        item.setOddsValue(195000d);
        System.out.println(item.getPaidAmount().toPlainString());

        System.out.println(item.getPaidAmount1().toPlainString());

        System.out.println(new BigDecimal("10000").multiply(new BigDecimal("1.95")).toPlainString());
    }

    /**
     * @return java.math.BigDecimal
     * @Description 赔付金额/100
     * @Param []
     * @Author toney
     * @Date 18:38 2020/2/6
     **/
    public BigDecimal getPaidAmount1() {
        return getPaidAmount().divide(BigDecimal.valueOf(100));
    }

    public Double getHandleAfterOddsValue1() {
        if (oddsValue == null) {
            return 0D;
        }
        return new BigDecimal(oddsValue + "").divide(new BigDecimal("100000")).setScale(6, RoundingMode.DOWN).doubleValue();
    }

//    public Double getHandleAfterBetAmount() {
//        return new BigDecimal(betAmount + "").divide(new BigDecimal("100")).setScale(4, RoundingMode.DOWN).doubleValue();
//    }

    /**
     * 注单赔率   扩大了10万倍
     * 获取原始值使用getHandleAfterOddsValue方法
     */
    private Double oddsValue;

    /**
     * 客户端原始赔率,仅用于mts操盘的注单
     */
    private Double originOdds;

    /**
     * 两项盘口,另一个投注项的赔率
     */
    private Double otherOddsValue;

    /**
     * 投注项类型
     */
    private String oddsType;

    /**
     * 变化前的赔率
     */
    private String oddFinally;

    /**
     * 最高可赢金额
     */
    private Double maxWinAmount;


    /**
     * 基准比分(下注时已产生的比分)
     */
    private String scoreBenchmark;

    /**
     * 投注类型ID(对应上游的投注项ID),传给风控的
     */
    private Long playOptionsId;

    /**
     * 投注项名称(新加)
     */
    private String playOptionsName;

    /**
     * 投注类型(投注时下注的玩法选项)，规则引擎用
     */
    private String playOptions;

    /**
     * 投注类型范围（所有投注的可能性-范围玩法时有值）
     */
    private String playOptionsRange;


    /**
     * 赛事阶段id
     */
    private Long matchProcessId;


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
     * 计算矩阵是否需要当前比分  1：是  2：否
     */
    private Integer isRelationScore;

    /**
     * 注单状态  0:待处理 1：成功  2：失败 3:已取消
     */
    private Integer validateResult = 0;


    private Integer currentPlayType;

    private String dateExpect;


    /**
     * 订单风控验证渠道  1 : 内部风控  2 : mts
     */
    private Integer riskChannel;


    /**
     * 订单状态( 0 待处理  1：成功  2：拒绝\r\n)
     */
    private Integer orderStatus = 0;

    /**
     * 串关类型(1：单关(默认)  )
     */
    private Integer seriesType;

    /**
     * 操盘方式 ： WS推送使用以下定义
     * ------ MANUAD(0, "手动");
     * ------ AUTO(1, "自动"),
     * <p>
     * 业务调用的传的订单
     * 0：表示自动  1：表示手动
     **/
    @TableField(exist = false)
    private Integer tradeType;

    //新增字段  联赛分级. 1: 一级联赛; 2:二级联赛; 3: 三级联赛; 以此类推; 0: 未分级
    Integer turnamentLevel;

    /**
     * 操盘平台 如： SR，MTS,Panda等
     */
    private String platform;

    /**
     * 颜色 新加-用于注单
     */
    private Integer colorLevel;

    /**
     * 其他比分(篮球小节比分/ 角球比分/ 加时赛比分等)
     */
    private String otherScore;



    /**
     * 数据源
     */
    private String dataSourceCode;

    /**
     * 子玩法标识
     */
    private String subPlayId;
    /**
     * 用户标签货量百分比
     */
    private BigDecimal volumePercentage;

    /**
     * 预期最迟接单时间
     */
    private Long maxAcceptTime;

    /**
     * @Description //最小接单时间
     * @Param
     * @Author sean
     * @Date 2020/11/7
     * @return
     **/
    private Integer minWait;

    /**
     * 事件编码ID
     */
    private Long eventId;
    /**
     * 当前事件类型
     */
    private Integer currentEventType;
    /**
     * 当前事件编码
     */
    private String  currentEvent;

    public String getSubPlayId() {
        return subPlayId;
    }

    public void setSubPlayId(String subPlayId) {
        this.subPlayId = subPlayId;
    }


    public Integer getPauseTime() {
        return pauseTime;
    }

    public void setPauseTime(Integer pauseTime) {
        this.pauseTime = pauseTime;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
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

    public Integer getHandleStatus() {
        return handleStatus;
    }

    public void setHandleStatus(Integer handleStatus) {
        this.handleStatus = handleStatus;
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
    	if (betAmount == null) {
            return 0L;
        }
        return betAmount ;
    }

    /**
     * @return java.math.BigDecimal
     * @Description 注单金额/100
     * @Param []
     * @Author toney
     * @Date 18:45 2020/2/6
     **/
    public BigDecimal getBetAmount1() {
        if (betAmount == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(betAmount).divide(BigDecimal.valueOf(100));
    }

    public String getOddsType() {
        return oddsType;
    }

    public void setOddsType(String oddsType) {
        this.oddsType = oddsType;
    }

    public void setBetAmount(Long betAmount) {
        this.betAmount = betAmount;
    }

    public static int getPlUSTIMES() {
        return PlUSTIMES;
    }

    public static void setPlUSTIMES(int plUSTIMES) {
        PlUSTIMES = plUSTIMES;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
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


    public Double getMaxWinAmount() {
        return maxWinAmount;
    }

    public void setMaxWinAmount(Double maxWinAmount) {
        this.maxWinAmount = maxWinAmount;
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

    public String getPlayOptionsName() {
        return playOptionsName;
    }

    public void setPlayOptionsName(String playOptionsName) {
        this.playOptionsName = playOptionsName;
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


    public Long getMatchProcessId() {
        return matchProcessId;
    }

    public void setMatchProcessId(Long matchProcessId) {
        this.matchProcessId = matchProcessId;
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

    public Integer getIsRelationScore() {
        return isRelationScore;
    }

    public void setIsRelationScore(Integer isRelationScore) {
        this.isRelationScore = isRelationScore;
    }

    public Integer getValidateResult() {
        return validateResult;
    }

    public void setValidateResult(Integer validateResult) {
        this.validateResult = validateResult;
    }

    public Integer getCurrentPlayType() {
        return currentPlayType;
    }

    public void setCurrentPlayType(Integer currentPlayType) {
        this.currentPlayType = currentPlayType;
    }

    public String getDateExpect() {
        return dateExpect;
    }

    public void setDateExpect(String dateExpect) {
        this.dateExpect = dateExpect;
    }

    public Integer getRiskChannel() {
        return riskChannel;
    }

    public void setRiskChannel(Integer riskChannel) {
        this.riskChannel = riskChannel;
    }


    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }

    public Integer getTurnamentLevel() {
        return turnamentLevel;
    }

    public void setTurnamentLevel(Integer turnamentLevel) {
        this.turnamentLevel = turnamentLevel;
    }

    public String getMarketValueNew() {
        return marketValueNew;
    }

    public void setMarketValueNew(String marketValueNew) {
        this.marketValueNew = marketValueNew;
    }

    public Integer getColorLevel() {
        return colorLevel;
    }

    public void setColorLevel(Integer colorLevel) {
        this.colorLevel = colorLevel;
    }

	public Integer getPlaceNum() {
		return placeNum;
	}

	public void setPlaceNum(Integer placeNum) {
		this.placeNum = placeNum;
	}

	public String getOtherScore() {
		return otherScore;
	}

	public void setOtherScore(String otherScore) {
		this.otherScore = otherScore;
	}

    public Double getOtherOddsValue() {
        return otherOddsValue;
    }

    public void setOtherOddsValue(Double otherOddsValue) {
        this.otherOddsValue = otherOddsValue;
    }

    public Integer getPreSettleStatus() {
        return preSettleStatus;
    }

    public void setPreSettleStatus(Integer preSettleStatus) {
        this.preSettleStatus = preSettleStatus;
    }

    public Double getPreSettleOdss() {
        return preSettleOdss;
    }

    public void setPreSettleOdss(Double preSettleOdss) {
        this.preSettleOdss = preSettleOdss;
    }

    public Integer getPreSettleTime() {
        return preSettleTime;
    }

    public void setPreSettleTime(Integer preSettleTime) {
        this.preSettleTime = preSettleTime;
    }

    public Double getOriginOdds() {
        return originOdds;
    }

    public void setOriginOdds(Double originOdds) {
        this.originOdds = originOdds;
    }
}
