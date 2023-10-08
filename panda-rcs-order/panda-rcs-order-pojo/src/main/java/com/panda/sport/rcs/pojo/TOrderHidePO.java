package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rcs_order_hide")
public class TOrderHidePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单单号
     */
    @TableId(value = "order_no")
    private String orderNo;
    /**
     * 藏单类型 0 、用户；1、标签；2、设备类型；3、商户；5动态藏单
     * */
    private Integer category;

    /**
     * 货量百分比
     */
    private BigDecimal volumePercentage;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 动态藏单比例
     **/
    private BigDecimal dynamicVolumePercentage;

    /**
     * 商户藏单比例
     **/
    private BigDecimal merchantVolumePercentage;

    /**
     * 设备藏单比例
     **/
    private BigDecimal equipmentVolumePercentage;


//    /**
//     * 用户ID
//     */
//    private Integer  timeType;
//
//    /**
//     * 订单状态(0:待处理,1:已处理,2:取消交易)
//     */
//    private Integer tenantId;
//
//    /**
//     * 注单项数量
//     */
//    private String tenantName;
//
//    /**
//     * 标签编号
//     */
//    private Integer firstTag;
//
//    /**
//     * 标签值
//     */
//    private String tagValue;
//
//    /**
//     * 赛种ID
//     */
//    private Integer sportId;
//
//
//    /**
//     * 赛事ID
//     */
//    private Long matchId;
//    /**
//     * 用户标签
//     * */
//    private String userTag;
//    /**
//     * 盘口Id
//     * */
//
//    private  Long marketId;
//    /**
//     * 盘口值
//     * */
//    private String marketValue;



//    /**
//     * 设备类型：0、PC；1、H5；2、APP；3、other
//     */
//    private Integer deviceType;
//
//    /**
//     * 人数
//     */
//    private Integer numberPeople;
//    /**
//     * 订单状态：0 、下单初始化；1、已结算
//     * */
//    private Integer orderStatus;


//    /**
//     * 最终赔率
//     * */
//    private BigDecimal oddFinally;
//    /**
//     * 真实下单数量
//     * */
//    private Integer realCount;
//    /**
//     * 真实下单金额
//     * */
//    private Long realAmount;
//    /***
//     * 真实平台输赢
//     * */
//    private  Long realLoseAmount;
//    /**
//     * 显示操盘数量
//     * */
//    private Integer traderCount;
//    /**
//     * 显示操盘数量
//     * */
//    private Long traderAmount;
//    /**
//     * 显示操盘输赢
//     * */
//
//    private  Long traderLoseAmount;
//    /**
//     * 币种编码
//     * */
//    private String currencyCode;
//    /**
//     * 货币名称
//     * */
//    private String currencyName;


//    /**
//     * 修改时间
//     */
//    private LocalDateTime updateTime;
}
