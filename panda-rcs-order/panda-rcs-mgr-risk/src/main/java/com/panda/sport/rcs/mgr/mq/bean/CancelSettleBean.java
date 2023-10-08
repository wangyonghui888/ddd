package com.panda.sport.rcs.mgr.mq.bean;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.bean
 * @Description :  结算
 * @Date: 2020-12-09 下午 9:42
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class CancelSettleBean {
  /**
   * 订单号，多个单号使用，分割
   */
  private String orderNo;
  /**
   * 发送时间
   */
  private Long sendTime;

  /**
   * 操作类型：
   * 1：结算；2：结算回滚，3：注单取消，4：取消回滚
   */
  private Integer operateType;

  //球种Id
  private Integer sportId;

  //赛事Id
  private Long matchId;

  //金额
  private String betAmount;
}
