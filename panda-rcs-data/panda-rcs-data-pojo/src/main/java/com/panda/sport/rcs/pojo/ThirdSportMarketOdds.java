package com.panda.sport.rcs.pojo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ThirdSportMarketOdds implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *表ID,自增
     **/
    private String id;

    private String oddsId;
    /**
     *盘口IDthird_sport_market.id
     **/
    private String marketId;
    /**
     *如果当前盘口与标准盘口中的B记录玩法相同且盘口显示内容相同,则该记录的当前字段值为B.ID
     **/
    //private Long referenceId;
    /**
     *当前投注项是否被激活.1激活;0未激活(锁盘)
     **/
    private Integer active;
    /**
     *投注项结算结果文本
     **/
    //private String settlementResultText;
    /**
     *投注项结算结果文本
     **/
    //private String settlementResult;
    /**
     *赛果已确认:Confirmed,盘中事件确认:LiveScouted,未知:Unknown
     **/
    //private String betSettlementCertainty;
    /**
     *投注项类型
     **/
    private String oddsType;
    /**
     *附加字段1
     **/
    private String addition1;
    /**
     *附加字段2
     **/
    private String addition2;
    /**
     *附加字段3
     **/
    private String addition3;
    /**
     *附加字段4
     **/
    private String addition4;
    /**
     *附加字段5
     **/
    private String addition5;
    /**
     *第三方投注项原始ID.
     **/
    private String thirdOddsFieldSourceId;
    /**
     *用于排序,大于1,越小越靠前
     **/
    private Integer orderOdds;
    /**
     *名称编码.用于多语言.投注项可能有也可能没有该字段.需要的时候填入
     **/
    //private Long nameCode;
    /**
     *投注项名称
     **/
    private String name;
    /**
     *投注项名称中包含的表达式的值
     **/
    private String nameExpressionValue;
    /**
     *投注项赔率.单位:0.0001
     **/
    private Integer oddsValue;
    /**
     * 赔率显示值
     */
    private String fieldOddsValue;

    /**
     *投注项PA赔率.单位:0.0001
     **/
    private Integer paOddsValue;
    /**
     *投注项原始赔率.单位:0.0001
     **/
    private Integer originalOddsValue;
    /**
     *标准投注项模板idstandard_sport_odds_fields_templet.id
     **/
    //private Long oddsFieldsTemplateId;
    /**
     *三方投注项模板源ID
     **/
    //private String thirdTemplateSourceId;
    /**
     *投注给哪一方:T1主队,T2客队
     **/
    //private String targetSide;
    /**
     *取值:SRBC分别代表:SportRadar、FeedConstruc.详情见data_source
     **/
    private String dataSourceCode;


    //警示值
    private Boolean isWarn;

    //private String remark;

    //private Long createTime;

    //private Long modifyTime;

    //private String extraInfo;
    /**
     *赛事ID,third_match_info.id
     **/
    //private Long thirdMatchId;

}