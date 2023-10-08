package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description :  赛事
 * @Date: 2020-12-02 下午 12:01
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@TableName(value = "rcs_match_profit")
public class RcsMatchProfit {
  /**
   * 主键
   */
  private Long id;
  /**
   * 运动种类
   */
  private Long sportId;
  /**
   * 赛事id
   */
  private Long matchId;
  /**
   * 赛事种类
   */
  private Integer matchType;
  /**
   * 赛事阶段
   */
  private Long matchProcessId;
  /**
   * 玩法id
   */
  private Long playId;
  /**
   * 盘口id
   */
  private Long marketId;

  /**
   * 盈利金额
   */
  private BigDecimal profitAmount;
  /**
   * 创建时间
   */
  private Long createTime;
  /**
   * 更新时间
   */
  private Long updateTime;
}
