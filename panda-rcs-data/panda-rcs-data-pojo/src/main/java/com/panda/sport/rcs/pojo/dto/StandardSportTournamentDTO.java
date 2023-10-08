package com.panda.sport.rcs.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mirro
 * @Project Name :  panda_data_nonrealtime
 * @Package Name :  com.panda.sport.data.nonrealtime.api.query.bo
 * @Description: 联赛查询结果单元对象
 * @date 2019/9/3 15:36
 * @ModificationHistory Who    When    What
 */
@Data
public class StandardSportTournamentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 三方联赛ID*/
    private Long id;

    /** 运动种类ID*/
    private Long sportId;

    /** 数据来源编码。指的是当前赛事使用哪个数据供应商的数据。使用该数据，则使用该风控*/
    private String dataSourceCode;

    /** 所属标准区域 id。 对应  standard_sport_region.id*/
    private Long standardSportRegionId;

    /** 联赛分级。1: 一级联赛；2:二级联赛；3：三级联赛；以此类推；0：未分级*/
    private Integer tournamentLevel;

    /** 后台管理使用的联赛id。*/
    private String tournamentManagerId;

    /** 联赛名称编码。联赛名称编码. 用于多语言*/
    private List<I18nItemDTO> il8nNameList;

    /** 当为子联赛时取父联赛的id*/
    private String fatherTournamentId;

    /** 是否子联赛(0:否，1是)*/
    private Integer simpleFlage;

    /** 当前赛季id*/
    private String currenSeasonId;

     /** 当前轮类型：Group，Cup，Qualification*/
    private String currentRoundType;

     /** 当期轮次(当类型Group时存在值)*/
    private Integer currentRoundNumber;

     /** 当期轮次名称*/
    private String currentRoundName;

     /** 联赛官网*/
    private String leagueUrl;

    /** 对用户可见。1：可见； 0：不可见*/
    private Integer visible;

    /** 联赛是否热门  0:非热门  1:热门*/
    private Integer hotStatus;

    /** 是否有对应的三方联赛（默认1）0：没有、1：有 **/
    private Integer hasRelation;

    /** 第三方联赛原始id.第三方提供的联赛的id*/
    private String thirdTournamentSourceId;

    /** 联赛归属（1:无 2:东京奥运会）*/
    private String tournamentType;

    /** 联赛缩略图。联赛缩略图url地址*/
    private String picUrlThumb;
    /** 联赛图标。联赛图片url地址*/
    private String picUrl;

    /** 简介。*/
    private String introduction;
    /** 备注。*/
    private String remark;

    /** 修改时间。*/
    private Long modifyTime;

}
