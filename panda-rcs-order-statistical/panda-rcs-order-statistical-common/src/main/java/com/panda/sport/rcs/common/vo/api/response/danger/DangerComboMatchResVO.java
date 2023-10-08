package com.panda.sport.rcs.common.vo.api.response.danger;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="DangerComboMatchResVO")
public class DangerComboMatchResVO {

    //unknown
    private String id;

    //sport related
    @ApiModelProperty(value = "赛种名称")
    private String sportName;
    @ApiModelProperty(value = "英文赛种名称")
    private String sportNameEn;
    @ApiModelProperty(value = "赛种名称国际化信息")
    private List<Language> sportNameLanguageList;

    //tournament related
    @ApiModelProperty(value = "联赛名称")
    private String tournamentName;
    @ApiModelProperty(value = "英文联赛名称")
    private String tournamentNameEn;
    @ApiModelProperty(value = "联赛名称国际化信息")
    private List<Language> tournamentNameLanguageList;

    //match related
    @ApiModelProperty(value = "赛事长ID")
    private String matchManageId;
    @ApiModelProperty(value = "赛事名称")
    private String matchInfo;
    @ApiModelProperty(value = "英文赛事名称")
    private String matchInfoEn;
    @ApiModelProperty(value = "赛事开始时间")
    private String matchBeginTime;
    @ApiModelProperty(value = "投注阶段，赛事类型： 1 ：早盘赛事 ，2： 滚球盘赛事，3： 冠军盘赛事，5：活动赛事")
    private String matchType;
    @ApiModelProperty(value = "赛事名称国际化信息")
    private List<Language> matchInfoLanguageList;

    //play related
    @ApiModelProperty(value = "玩法名称")
    private String playName;
    @ApiModelProperty(value = "英文玩法名称")
    private String playNameEn;
    @ApiModelProperty(value = "投注项名称国际化信息")
    private List<Language> playNameLanguageList;
    @ApiModelProperty(value = "投注项名称")
    private String playOption;
    @ApiModelProperty(value = "英文投注项名称")
    private String playOptionEn;
    @ApiModelProperty(value = "英文投注项名称")
    private List<Language> playOptionLanguageList;

    //statistic related
    @ApiModelProperty(value = "危险串关注单数")
    private Integer orderCount=0;
    @ApiModelProperty(value = "危险串关投注金额")
    private Integer betAmount=0;

}
