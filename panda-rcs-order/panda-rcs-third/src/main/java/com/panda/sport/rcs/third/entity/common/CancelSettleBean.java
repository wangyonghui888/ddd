package com.panda.sport.rcs.third.entity.common;

import lombok.Data;

/**
 * @author Beulah
 * @date 2023/8/15 13:31
 * @description 取消结算实体
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
