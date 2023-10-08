package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.panda.sport.rcs.common.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description :  订单明细扩展表
 * @Date: 2020-01-31 10:42
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TOrderDetailExt implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * @Description id
     * @Param
     * @Author toney
     * @Date 10:43 2020/1/31
     * @return
     **/
    private Long id;
    /**
     * @Description betNo
     * @Param
     * @Author toney
     * @Date 10:43 2020/1/31
     * @return
     **/
    private String betNo;
    /**
     * @Description 订单编号
     * @Param
     * @Author toney
     * @Date 10:44 2020/1/31
     * @return
     **/
    private String orderNo;
    /**
     * @Description 赛事Id
     * @Param
     * @Author toney
     * @Date 10:50 2020/1/31
     * @return
     **/
    private Long matchId;
    private Integer playSetId;
    /**
     * @Description 处理状态 0 待处理 1 接单  2拒单
     * @Param
     * @Author toney
     * @Date 10:52 2020/1/31
     * @return

     **/
    private Integer orderStatus;
    /**
     * @Description 下单时间
     * @Param
     * @Author toney
     * @Date 11:36 2020/1/31
     * @return
     **/
    private Long betTime;
    /**
     * 预期最迟接单时间
     */
    private Long maxAcceptTime;
    private Long finalAcceptTime;
    /**
     * @Description 当前事件最长接单等待时长
     * @Param
     * @Author toney
     * @Date 11:37 2020/1/31
     * @return
     **/
    private Integer maxWait;
    /**
     * @Description   //最小接单时间
     * @Param
     * @Author  sean
     * @Date   2020/11/7
     * @return
     **/
    private Integer minWait;
    /**
     * @Description 当前事件
     * @Param
     * @Author toney
     * @Date 11:39 2020/1/31
     * @return
     **/
    private String currentEvent;
    /**
     * @Description 创建时间
     * @Param
     * @Author toney
     * @Date 11:39 2020/1/31
     * @return
     **/
    private Date crtTime;
    /**
     * @Description 更新时间
     * @Param
     * @Author toney
     * @Date 11:40 2020/1/31
     * @return
     **/
    private Date updateTime;

    /**
     * @Description 0 自动 1 手动
     * @Param
     * @Author toney
     * @Date 11:40 2020/1/31
     * @return
     **/
    private Integer mode;
    /**
     * @Description 订单扫描状态 0 未处理 1 已处理 2处理中
     * @Param
     * @Author YiMing
     * @Date 11:40 2022/1/24
     * @return
     **/
    private Integer handleStatus;
    /**
     * @Description   事件类型0-安全;1-危险;2-封盘;3拒单
     * @Param
     * @Author YiMing
     * @Date 11:40 2022/1/24
     * @return
     **/
    private Integer currentEventType;
    /**
     * @Description   //字符串时间类型
     * @Param
     * @Author  sean
     * @Date   2020/11/13
     * @return
     **/
    /**
     * 暂停倒计时（秒）
     */
    private Integer pauseTime;

    //盘口位置
    private Integer placeNum;
    //盘口ID
    private String marketId;
    //投注项ID
    private String playOptionsId;
    //赔率
    private BigDecimal oddsValue;
    //玩法ID
    private Integer playId;
    //是否滚球早盘
    private Integer matchType;
    //赛种ID
    private Integer sportId;
    //子玩法ID
    private  String subPlayId;
    //串关类型，1为单关2为串关
    private Integer seriesType;

    public String getUpdateTimeStr(){
        return DateUtils.transferLongToDateStrings(System.currentTimeMillis());
    }
}
