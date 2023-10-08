package com.panda.rcs.pending.order.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.trade.vo.tourTemplate
 * @Description :  联赛模板
 * @Date: 2020-05-12 19:10
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class TournamentTemplateVo {
    /**
     * 主键id
     */
    private Long id;
    /**
     * sportId
     */
    private Integer sportId;
    /**
     * 1：级别  2：联赛id   3：赛事id
     */
    private Integer type;
    /**
     * 根据type设置（1：设值联赛等级  2：设值联赛id   3：设值赛事id）
     */
    private Long typeVal;
    /**
     * 1：早盘；0：滚球
     */
    private Integer matchType;
    /**
     * 赔率源
     */
    private String dataSourceCode;
    /**
     * 商户单场赔付限额
     */
    private Long businesMatchPayVal;

    /**
     * 用户单场赔付限额
     */
    private Long userMatchPayVal;
    /**
     * 商户单场预约赔付限额
     */
    private Long businesPendingOrderPayVal;
    /**
     * 用户单场预约赔付限额
     */
    private Long userPendingOrderPayVal;
    /**
     * 用户预约中笔数
     */
    private Integer userPendingOrderCount;
    /**
     * 预约投注速率
     */
    private Integer pendingOrderRate;
    /**
     * 分时节点数据
     */
    private RcsMargainRefVo rcsMargainRefVo;
}