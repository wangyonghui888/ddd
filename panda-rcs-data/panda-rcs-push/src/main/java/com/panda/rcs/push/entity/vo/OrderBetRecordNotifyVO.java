package com.panda.rcs.push.entity.vo;

import com.panda.rcs.push.entity.vo.WebSocketRequest;
import lombok.Data;

import java.util.List;

/**
 * @author :  Sean
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.websocket.request
 * @Description :  注单页面入参接收类
 * @Date: 2019-11-06 10:45
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class OrderBetRecordNotifyVO extends WebSocketRequest {
    /**
     * 联赛id
     */
    private Long tournamentId;
    /**
     * 联赛id集合
     */
    private List<Long> tournamentIds;

    /**
     * 1: 赛前盘; 0: 滚球盘.
     */
    private Integer matchType;
    /**
     * 赛事id集合
     */
    private List<Long> matchIds;
    /**
     * 最大赔率
     */
    private String maxOdds;
    /**
     * 最小赔率
     */
    private String minOdds;
    /**
     * 最大货量
     */
    private Long maxMoney;
    /**
     * 最小货量
     */
    private Long minMoney;
    /**
     * 下注金额
     */
    private Long betAmount;
    /**
     * 注单状态(0:待处理,1:已接单,2:已拒单,3:已取消)
     */
    private Integer betStatus;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 商户id集
     */
    private List<Long> tenantId;
    /**
     * 用户等级集
     */
    private List<String> userLevels;
    /**
     * 玩法id集
     */
    private List<Integer> playIds;
    /**
     * 风控型玩法id集
     */
    private List<Long> riskPlayIds;
    /**
     * 货币集
     */
    private List<String> currencyCode;
    /**
     * 盘口ID
     */
    private Long marketId;
    /**
     * 投注类型(投注时下注的玩法选项)，规则引擎用
     */
    private String playOptions;

}
