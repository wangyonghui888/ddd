package com.panda.sport.rcs.third.entity.common;

import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.pojo.TOrderDetail;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Beulah
 * @date 2023/3/24 19:54
 * @description 接拒封装对象，只封装使用的字段，用于减少内存开销
 */
@Data
public class ThirdOrderDelayVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    String orderNo;

    /**
     * 三方订单号
     */
    String thirdOrderNo;

    /**
     * 接单时间
     */
    Long acceptTime;

    /**
     * 延迟时间
     */
    Integer delayTime;

    /**
     * 注单信息
     */
    List<ExtendBean> list;

    /**
     * 是否是第三方 延时
     */
    String third;


    /**
     * 总投注金额
     */
    BigDecimal totalMoney;


    /**
     * 第三方返回结果
     */
    String thirdRes;

    /**
     * 接拒得次数
     */
    Integer rejectNum = 0;

    /**
     * 商户id
     */
    String busId;

    /**
     * 用户分组
     */
    String orderGroup;

    /**
     * 串关类型
     */
    Integer seriesType;

}
