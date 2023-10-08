package com.panda.rcs.push.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.rcs.push.entity.vo.SettleOrder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author :  DC
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.websocket.response
 * @Description :  注单实时推送到页面的VO
 * @Date: 2019-11-09 14:57
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class OrderBetResponseVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 运动种类编号
     */
    private Integer sportId;

    /**
     * 注单编号
     */
    private String betNo;

    private Integer tournamentId;

    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 用户ID
     */
    private String uid;

    /**
     * 订单状态(0:待处理,1:已处理,2:取消交易)
     */
    private Integer orderStatus;
    /**
     * ip用户区域
     */
    private String ipArea;
    /**
     * 商户id
     */
    private Long tenantId;
    /**
     * 商户名称(新加)
     */
    private String tenantName;
    /**
     * 1:手机，2：PC
     */
    private Integer deviceType;

    /**
     * ip地址
     */
    private String ip;
    /**
     * 运动种类名称
     */
    private String sportName;

    /**
     * 玩法ID
     */
    private Integer playId;
    /**
     * 玩法集ID
     */
    private Long playSetId;
    /**
     * 玩法名称
     */
    private String playName;
    /**
     * 赛事编号
     */
    private String matchId;

    /**
     * 赛事名称
     */
    private String matchName;
    /**
     * 下注时间
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long betTime;
    /**
     * 注单金额，指的是下注本金2位小数，投注时x100  单位：分
     */
    private String betAmount;
    /**
     * 盘口ID
     */
    private String marketId;
    /**
     * 投注类型ID(对应上游的投注项ID),传给风控的
     */
    private String playOptionsId;
    
    /**
     * 是否需要更新赔率
     */
    private Boolean isUpdateOdds ;

    /**
     * 投注项名称(新加)
     */
    private String playOptionsName;
    /**
     * 用户标签
     **/
    private String userFlag;
    /**
     * 用户级别
     */
    private Integer levelId;
    /**
     * 币种编码
     */
    private String currencyCode;

    /**
     * 币种名称(新加)
     */
    private String currencyName;
    /**
     * 注单赔率   扩大了10万倍
     * 获取原始值使用getHandleAfterOddsValue方法
     */
    private Double oddsValue;
    /**
     * 联赛名称
     */
    private String tournamentName;
    /**
     * 等待时间，单位：s
     */
    private Integer waitTime;
    /**
     * 等待时间，单位：s
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long matchStartTime;
    /**
     * @Description   比赛主客队
     * @Param
     * @Author  Sean
     * @Date  18:32 2020/1/21
     * @return
     **/
    private String matchInfo;
    /**
     * 盘口值
     */
    private String marketValue;
    /**
     * 串关类型(1：单关(默认)  )
     */
    private Integer seriesType;
    /**
     * 最高可赢金额
     */
    private Double maxWinAmount;
    /**
     * 操盘类型 1:自动操盘 0:手动操盘
     */
    private Integer tradeType;
    /**
     * 颜色 新加-用于注单
     */
    private Integer colorLevel;
    /**
     * 用户名
     */
    private String username;
    
    private String optionMarket;
    
    private String awayTeam;
    
    private String homeTeam;
    /*拒单原因*/
    private String reason;
    /**
     * 修改时间
     */
    private Long modifyTime;

    private Integer vipLevel;

    /**
     * 基准比分(下注时已产生的比分)
     */
    private String scoreBenchmark;
    /**
     * 早盘1 滚球2
     */
    private Integer matchType;
    /**
     * matchManageId
     */
    private String matchManageId;
    /**
     * 详情
     */
    private Integer infoStatus = 0;

    /**
     * 二级标签
     */
    private String secondaryTag;

    /**
     * 其他比分(篮球小节比分/ 角球比分/ 加时赛比分等)
     */
    private String otherScore;

    /**
     * 一级标签 用户等级
     */
    private int userTagLevel;

    /**
     * 暂停倒计时（秒）
     */
    private Integer pauseTime;

    /**
     * 投注数
     */
    private Integer betCount;

    /**
     * 总赔率
     */
    private Double oddsCount;

    private SettleOrder settleOrder;

    /**
     * 危险ip标识  默认0即非危险ip ; 1 危险ip
     */
    private Integer dangerIpMark = 0;

    /**
     * 指纹池级别，默认0，即无级别 ; 返回数字即为几级指纹池
     */
    private Integer fpLevel = 0;

    /**
     * 玩家组级别，默认0，即无级别 ; 返回数字即为几级玩家组
     */
    private Integer playerGroupLevel = 0;

    /**
     * 原始金额
     */
    private String productAmountTotal;

    /**
     * 是否为预约投注订单 0为false ,1 为true;
     */
    private Integer isPendingOrder;
}
