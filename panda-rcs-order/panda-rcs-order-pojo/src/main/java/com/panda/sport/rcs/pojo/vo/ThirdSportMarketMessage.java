package com.panda.sport.rcs.pojo.vo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Data
public class ThirdSportMarketMessage implements Serializable {
    /**
     * id
     */
    @Field(value = "id")
    private String id;

    /**
     * 标准盘口id
     */
    private String relationMarketId;

    /**
     * 三方玩法id
     */
    private Long marketCategoryId;

    /**
     * 第三提供的id
     */
    private String thirdMarketSourceId;

    /**
     * 如果当前盘口与标准盘口中的B记录玩法相同且盘口显示内容相同,则该记录的当前字段值为B.ID
     */
    //private Long referenceId;

    /**
     * 1:赛前盘;0:滚球盘
     */
    private Integer marketType;

    /**
     * 详情见data_source
     */
    private String dataSourceCode;

    /**
     * 盘口状态0-5.0:active,1:suspended,2:deactivated,3:settled,4:cancelled,5:handedOver
     */
    private Integer status;

    /**
     * 三方盘口源状态
     */
    private Integer thirdMarketSourceStatus;

    /**
     * 盘口阶段id.对应对应system_item_dict.value
     */
    //private String scopeId;

    /**
     * 盘口名称编码.用于多语言
     */
    //private Long nameCode;

    /**
     * 玩法的中文名称.仅用用于数据库操作人员使用
     */
    private String oddsTypeName;

    /**
     * 接收到第三方数据后,可以通过该字段快速定位到当前的盘口.通过玩法和具体内容确认盘口的唯一性
     */
    //private String thirdOddsType;

    /**
     * 该盘口具体显示的值.例如:大小球中,大小界限是:3.5
     */
    private String oddsValue;

    /**
     * 排序类型
     */
    private String orderType;

    /**
     * 盘口名称
     */
    private String oddsName;

    /**
     * 盘口级别，数字越小优先级越高
     */
    private Long oddsMetric;

    private String addition1;

    private String addition2;

    private String addition3;

    private String addition4;

    private String addition5;

    //private String remark;

    private Long createTime;

    private Long modifyTime;

    private Long insertTime;

    private Long updateTime;


    //private String extraInfo;

    /**
     * TX坑位
     */
    private Integer offerLineId;

    private Integer dbData;

    /**
     * 并列-胜出数
     */
    //private Integer numberOfWinners;

    private static final long serialVersionUID = 1L;

    /**
     * 三方盘口投注项集合
     */
    private List<ThirdSportMarketOdds> thirdSportMarketOddsList;


}
