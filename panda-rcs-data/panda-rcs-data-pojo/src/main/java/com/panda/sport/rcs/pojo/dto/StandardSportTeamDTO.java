package com.panda.sport.rcs.pojo.dto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mirro
 * @Project Name :  panda_data_nonrealtime
 * @Package Name :  com.panda.sport.data.nonrealtime.api.query.bo
 * @Description: 球队查询结果单元对象
 * @date 2019/9/3 20:21
 * @ModificationHistory Who    When    What
 */
@Data
public class StandardSportTeamDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 球队表id
     */
    private Long id;

    /**
     * 体育种类id。体育种类id
     */
    private Long sportId;


    /**
     * 球队区域ID。  standard_sport_region.id
     */
    private Long regionId;

    /**
     * 数据来源编码。取值： SR BC分别代表：SportRadar、FeedConstruc。详情见data_source
     */
    private String dataSourceCode;

    /**
     * 球队 logo。图标的url地址
     */
    private String logoUrl;

    /**
     * 球队 logo缩略图的url地址
     */
    private String logoUrlThumb;
    /**
     * 球队 logo。图标的url地址
     */
    private String logoUrl2;

    /**
     * 球队 logo缩略图的url地址
     */
    private String logoUrlThumb2;

    /**
     * 球队管理id。 该id 用于后台管理。
     */
    private String teamManageId;

    /**
     * 球队名称编码。国际化信息
     */
    private List<I18nItemDTO> il8nNameList;

    /**
     * 对用户可见。1：可见； 0：不可见
     */
    private Integer visible;

    /**
     * 主教练。主教练名称
     */
    private String coach;

    /**
     * 主场。比如：所在地 和 名称
     */
    private String statium;

    /**
     * 球队介绍。默认是空
     */
    private String introduction;

    /**
     * 备注。
     */
    private String remark;

    /**
     * 更新时间。
     */
    private Long modifyTime;

    /**
     * 球队类型.1:团体;2:男单;3:女单;4:男双;5:女双;6:混双;7:未知',
     */
    private Integer type;

    /**
     * 球员源ID
     */
    private String playerIds;

    /**
     * 赛事球队关系信息
     */
    private StandardMatchTeamRelationDTO matchTeamRelation;
}
