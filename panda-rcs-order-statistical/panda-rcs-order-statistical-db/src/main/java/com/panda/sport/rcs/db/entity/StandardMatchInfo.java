package com.panda.sport.rcs.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-06-23
 */
@TableName("standard_match_info")
@ApiModel(value="StandardMatchInfo对象", description="")
public class StandardMatchInfo extends Model<StandardMatchInfo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id. id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "体育种类id. 运动种类id 对应sport.id")
    private Long sportId;

    @ApiModelProperty(value = "标准联赛 id. 对应联赛 id  对应  standard_sport_tournament.id")
    private Long standardTournamentId;

    @ApiModelProperty(value = "第三方比赛id. 第三方比赛在 表 third_match_info 中的id")
    private Long thirdMatchId;

    @ApiModelProperty(value = "开赛后的时间. 单位:秒.例如:3分钟11秒,则该值是 191")
    private Integer secondsMatchStart;

    @ApiModelProperty(value = "事件发生时间. UTC时间")
    private Long eventTime;

    @ApiModelProperty(value = "赛事是否开放赛前盘. 取值为 1  或  0.  1=开放; 0=不开放")
    private Integer preMatchBusiness;

    @ApiModelProperty(value = "赛事是否开放滚球. 取值为 1  或  0.  1=开放; 0=不开放")
    private Integer liveOddBusiness;

    @ApiModelProperty(value = "比赛开盘标识. 0: 未开盘; 1: 开盘; 2: 关盘; 3: 封盘; 开盘后用户可下注")
    private Integer operateMatchStatus;

    @ApiModelProperty(value = "比赛开始时间. 比赛开始时间 UTC时间")
    private Long beginTime;

    @ApiModelProperty(value = "比赛是否被激活. 1: 激活; 0: 未激活.  激活的比赛可以进行下注. ")
    private Integer active;

    @ApiModelProperty(value = "赛前盘下注状态. 赛前盘: 1 可下注; 0不可下注; 用于数据源控制下注状态")
    private Integer preMatchBetStatus;

    @ApiModelProperty(value = "滚球下注状态. 滚球中使用: 1 可下注; 0不可下注; 用于数据源控制下注状态")
    private Integer liveOddsBetStatus;

    @ApiModelProperty(value = "赛事状态.  比如:未开赛, 滚球, 取消, 延迟等. ")
    private Integer matchStatus;

    @ApiModelProperty(value = "是否为中立场. 取值为 0  和1  .   1:是中立场, 0:非中立场. 操盘人员可手动处理")
    private Integer neutralGround;

    @ApiModelProperty(value = "标准赛事编码. 用于管理的赛事id")
    private String matchManageId;

    @ApiModelProperty(value = "比赛场地名称,仅限中文. 用于查看mysql 时 使用. ")
    private String matchPositionName;

    @ApiModelProperty(value = "比赛场地的国际化编码")
    private Long matchPositionNameCode;

    @ApiModelProperty(value = "风控服务商编码. sr bc pa 等. 详见 数据源表 data_source中的code字段")
    private String riskManagerCode;

    @ApiModelProperty(value = "数据来源编码. 取值见: data_source.code")
    private String dataSourceCode;

    @ApiModelProperty(value = "关联数据源编码列表.  数据样例: SR,BC,188; SR,188; BC,188")
    private String relatedDataSourceCoderList;

    @ApiModelProperty(value = "关联数据源数量")
    private Integer relatedDataSourceCoderNum;

    @ApiModelProperty(value = "数据来源编码. 取值见: data_source.code")
    private String matchDataProviderCode;

    @ApiModelProperty(value = "第三方赛事原始id. 该厂比赛在第三方数据供应商中的id. 比如:  SportRadar 发送数据时, 这场比赛的ID. ")
    private String thirdMatchSourceId;

    @ApiModelProperty(value = "赛事双方的对阵信息.格式:主场队名称 VS 客场队名称")
    private String homeAwayInfo;

    @ApiModelProperty(value = "父赛事id")
    private Long parentId;

    @ApiModelProperty(value = "赛事可下注状态. 0: betstart; 1: betstop")
    private Integer betStatus;

    @ApiModelProperty(value = "赛事包含的所有球队多语言信息,json串,冗余字段,用于赛程页面查询")
    private String teamName;

    @ApiModelProperty(value = "赛事包含的所有球队id信息,json串,冗余字段,用于赛程页面查询")
    private String teamManageId;

    @ApiModelProperty(value = "比赛阶段id. 取自基础表 : match_status.id")
    private Long matchPeriodId;

    @ApiModelProperty(value = "备注. ")
    private String remark;

    @ApiModelProperty(value = "赛前操盘平台 MTS,PA")
    private String preRiskManagerCode;

    @ApiModelProperty(value = "滚球操盘平台 MTS,PA")
    private String liveRiskManagerCode;

    @ApiModelProperty(value = "创建时间. ")
    private Long createTime;

    @ApiModelProperty(value = "修改时间. ")
    private Long modifyTime;

    private LocalDateTime updateTime;


    public StandardMatchInfo() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Long getStandardTournamentId() {
        return standardTournamentId;
    }

    public void setStandardTournamentId(Long standardTournamentId) {
        this.standardTournamentId = standardTournamentId;
    }

    public Long getThirdMatchId() {
        return thirdMatchId;
    }

    public void setThirdMatchId(Long thirdMatchId) {
        this.thirdMatchId = thirdMatchId;
    }

    public Integer getSecondsMatchStart() {
        return secondsMatchStart;
    }

    public void setSecondsMatchStart(Integer secondsMatchStart) {
        this.secondsMatchStart = secondsMatchStart;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public Integer getPreMatchBusiness() {
        return preMatchBusiness;
    }

    public void setPreMatchBusiness(Integer preMatchBusiness) {
        this.preMatchBusiness = preMatchBusiness;
    }

    public Integer getLiveOddBusiness() {
        return liveOddBusiness;
    }

    public void setLiveOddBusiness(Integer liveOddBusiness) {
        this.liveOddBusiness = liveOddBusiness;
    }

    public Integer getOperateMatchStatus() {
        return operateMatchStatus;
    }

    public void setOperateMatchStatus(Integer operateMatchStatus) {
        this.operateMatchStatus = operateMatchStatus;
    }

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getPreMatchBetStatus() {
        return preMatchBetStatus;
    }

    public void setPreMatchBetStatus(Integer preMatchBetStatus) {
        this.preMatchBetStatus = preMatchBetStatus;
    }

    public Integer getLiveOddsBetStatus() {
        return liveOddsBetStatus;
    }

    public void setLiveOddsBetStatus(Integer liveOddsBetStatus) {
        this.liveOddsBetStatus = liveOddsBetStatus;
    }

    public Integer getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(Integer matchStatus) {
        this.matchStatus = matchStatus;
    }

    public Integer getNeutralGround() {
        return neutralGround;
    }

    public void setNeutralGround(Integer neutralGround) {
        this.neutralGround = neutralGround;
    }

    public String getMatchManageId() {
        return matchManageId;
    }

    public void setMatchManageId(String matchManageId) {
        this.matchManageId = matchManageId;
    }

    public String getMatchPositionName() {
        return matchPositionName;
    }

    public void setMatchPositionName(String matchPositionName) {
        this.matchPositionName = matchPositionName;
    }

    public Long getMatchPositionNameCode() {
        return matchPositionNameCode;
    }

    public void setMatchPositionNameCode(Long matchPositionNameCode) {
        this.matchPositionNameCode = matchPositionNameCode;
    }

    public String getRiskManagerCode() {
        return riskManagerCode;
    }

    public void setRiskManagerCode(String riskManagerCode) {
        this.riskManagerCode = riskManagerCode;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getRelatedDataSourceCoderList() {
        return relatedDataSourceCoderList;
    }

    public void setRelatedDataSourceCoderList(String relatedDataSourceCoderList) {
        this.relatedDataSourceCoderList = relatedDataSourceCoderList;
    }

    public Integer getRelatedDataSourceCoderNum() {
        return relatedDataSourceCoderNum;
    }

    public void setRelatedDataSourceCoderNum(Integer relatedDataSourceCoderNum) {
        this.relatedDataSourceCoderNum = relatedDataSourceCoderNum;
    }

    public String getMatchDataProviderCode() {
        return matchDataProviderCode;
    }

    public void setMatchDataProviderCode(String matchDataProviderCode) {
        this.matchDataProviderCode = matchDataProviderCode;
    }

    public String getThirdMatchSourceId() {
        return thirdMatchSourceId;
    }

    public void setThirdMatchSourceId(String thirdMatchSourceId) {
        this.thirdMatchSourceId = thirdMatchSourceId;
    }

    public String getHomeAwayInfo() {
        return homeAwayInfo;
    }

    public void setHomeAwayInfo(String homeAwayInfo) {
        this.homeAwayInfo = homeAwayInfo;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getBetStatus() {
        return betStatus;
    }

    public void setBetStatus(Integer betStatus) {
        this.betStatus = betStatus;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamManageId() {
        return teamManageId;
    }

    public void setTeamManageId(String teamManageId) {
        this.teamManageId = teamManageId;
    }

    public Long getMatchPeriodId() {
        return matchPeriodId;
    }

    public void setMatchPeriodId(Long matchPeriodId) {
        this.matchPeriodId = matchPeriodId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPreRiskManagerCode() {
        return preRiskManagerCode;
    }

    public void setPreRiskManagerCode(String preRiskManagerCode) {
        this.preRiskManagerCode = preRiskManagerCode;
    }

    public String getLiveRiskManagerCode() {
        return liveRiskManagerCode;
    }

    public void setLiveRiskManagerCode(String liveRiskManagerCode) {
        this.liveRiskManagerCode = liveRiskManagerCode;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
