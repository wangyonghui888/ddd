package com.panda.sport.data.rcs.dto.dj;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName DJBetReqVo
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/21 22:12
 * @Version 1.0
 **/
@Data
public class DJBetReqVo implements Serializable {
    private static final long serialVersionUID = -2051984122055138592L;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 注单数量
     * 3串4就是4个注单
     * 4串11就是11个注单
     */
    private Integer betNum ;

    /**
     * 投注设备 1-PC 2-H5 3-Android 4-IOS
     */
    private Integer device;

    /**
     * 0:非VIP  1:VIP
     */
    private Integer vipLevel;

    /**
     * 商户
     */
    private String merchant;

    /**
     * 会员id
     */
    private Long userId;

    /**
     * 会员账号
     */
    private String accountName;

    /**
     * 投注ip
     */
    private String ip;

    /**
     *会员赔率接收方式
     * [1 自动接受最新赔
     * 率 2自动接受更高
     * 赔率 3永不接受最
     * 新赔率]
     */
    private Integer oddUpdateType;

    /**
     * 下注时间
     */
    private Long betTime;

    /**
     * 串关类型
     */
    private Integer seriesType;

    /**
     * 注单集合
     * 例如:3串4要拆成4个注单
     * 3个2串1
     * 1个3串1
     */
    private List<DjBetOrder> orderList;

    /**
     * 指纹字符串
     */
    private String fpId;
}
