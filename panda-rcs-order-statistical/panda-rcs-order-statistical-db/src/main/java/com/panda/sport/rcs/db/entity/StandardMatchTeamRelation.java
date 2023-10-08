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
 * 保存标准赛事与球队之间的所属关系.  
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-06-23
 */
@TableName("standard_match_team_relation")
@ApiModel(value="StandardMatchTeamRelation对象", description="保存标准赛事与球队之间的所属关系.  ")
public class StandardMatchTeamRelation extends Model<StandardMatchTeamRelation> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "标准球队id")
    private Long standardTeamId;

    @ApiModelProperty(value = "球队名称（中文）")
    private String teamName;

    @ApiModelProperty(value = "标准比赛id")
    private Long standardMatchId;

    @ApiModelProperty(value = "比赛中的作用.足球:主客队或者其他.home:主场队;away:客场队")
    private String matchPosition;

    @ApiModelProperty(value = "显示顺序.  默认不使用")
    private Integer displayOrder;

    @ApiModelProperty(value = "球队名称快照")
    private String teamNameRecord;

    private String remark;

    private Long createTime;

    private Long modifyTime;

    private LocalDateTime updateTime;


    public StandardMatchTeamRelation() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStandardTeamId() {
        return standardTeamId;
    }

    public void setStandardTeamId(Long standardTeamId) {
        this.standardTeamId = standardTeamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public String getMatchPosition() {
        return matchPosition;
    }

    public void setMatchPosition(String matchPosition) {
        this.matchPosition = matchPosition;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getTeamNameRecord() {
        return teamNameRecord;
    }

    public void setTeamNameRecord(String teamNameRecord) {
        this.teamNameRecord = teamNameRecord;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
