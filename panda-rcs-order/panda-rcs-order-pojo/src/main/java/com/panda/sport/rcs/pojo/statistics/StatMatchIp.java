package com.panda.sport.rcs.pojo.statistics;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description :  统计ip
 * @Date: 2020-06-07 16:27
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StatMatchIp {
   /**
    * @Description   赛事id
    * @Param
    * @Author  toney
    * @Date  16:28 2020/6/7
    * @return
    **/
   @TableId
    private  Long matchId;
    /**
     * ip前三段地址
     */
    private String ipAddr;
    /**
     * 数量
     */
    private Integer nums;
     /**
      * 订单数
      */
     private Integer betNums;
}
