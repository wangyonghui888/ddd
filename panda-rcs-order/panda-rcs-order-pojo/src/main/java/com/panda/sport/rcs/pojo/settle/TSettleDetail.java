package com.panda.sport.rcs.pojo.settle;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class TSettleDetail implements Serializable {
  private static final long serialVersionUID = 1L;


  /**
   * 注单号
   */
  @TableId
  private String betNo;

  /**
   * 订单号
   */
  private String orderNo;

  /**
   * 赛事id
   */
  private Long matchId;

  /**
   * 开售时间
   */
  private Long beginTime;

  /**
   * 结算任务id
   */
  private Integer settleMatchProcessId;

  /**
   * 联赛级别
   */
  private Integer tournamentLevel;

  /**
   * 类型：1 ：早盘 ，2： 滚球盘， 3： 冠军盘
   */
  private Integer matchType;

  /**
   * 运动标识
   */
  private Long sportId;

  /**
   * 用户id
   */
  private Long uid;

  /**
   * 投注项id
   */
  private Long playOptionsId;

  /**
   * 盘口类型(OU:欧盘 HK:香港盘 US:美式盘 ID:印尼盘 MY:马来盘 GB:英式盘）
   */
  private String marketType;

  /**
   * 注单结算结果
   */
  private Integer betResult;

  /**
   * 注单赔率,固定2位小数 (结算时用到)
   */
  private String oddFinally;

  /**
   * 盘口值
   */
  private String marketValue;

  /**
   * 投注金额
   */
  private Long betAmount;

  /**
   * 变化前的赔率
   */
  private Double oddsValue;

  /**
   * 最大可赢金额
   */
  private Long maxWinAmount;

  /**
   * 盘口id
   */
  private Long marketId;

  /**
   * 投注项
   */
  private String playOptions;

  /**
   * 玩法id
   */
  private Long playId;

  /**
   * 基准比分
   */
  private String settleScore;

  /**
   * 初始早盘盘口值
   */
  private BigDecimal marketValueNew;

  /**
   * @Author toney
   * @Date 2020/12/4 下午 2:43
   * @Description 创建人
   * @param null
   * @Return
   * @Exception
   */
  private String createUser;

  /**
   * 添加时间
   */
  private Long createTime;


  
  /**
   * @Author toney
   * @Date 2020/12/4 下午 2:43
   * @Description 修改用户
   * @param null
   * @Return 
   * @Exception 
   */
  private Long modifyTime;

  /**
   * @Author toney
   * @Date 2020/12/4 下午 2:43
   * @Description 修改人
   * @param null
   * @Return
   * @Exception
   */
  private String modifyUser;
}

