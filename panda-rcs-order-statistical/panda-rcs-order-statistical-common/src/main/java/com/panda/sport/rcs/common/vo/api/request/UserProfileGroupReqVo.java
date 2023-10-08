package com.panda.sport.rcs.common.vo.api.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 玩家组 入参VO
 * </p>
 *
 * @author Kir
 * @since 2021-01-29
 */
@ApiModel(value="UserProfileGroup VO", description="玩家组表")
public class UserProfileGroupReqVo implements Serializable  {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户ID集合")
    private Long[] userId;

    @ApiModelProperty(value = "玩家组名称")
    private String groupName;

    @ApiModelProperty(value = "备注/理由")
    private String remake;

    @ApiModelProperty(value = "最后修改人")
    private String modifyUser;

    @ApiModelProperty(value = "最后修改日期")
    private Long modifyTime;

    @ApiModelProperty(value = "玩家组风控措施 入参VO")
    private UserGroupBetRateReqVo userGroupBetRateReqVo;

    @ApiModelProperty(value = "1.保存货量百分比信息并发送MQ   2.只修改玩家组信息及玩家组内的用户数量/新增玩家组")
    private Integer isSaveBetRate;

    public Integer getIsSaveBetRate() {
        return isSaveBetRate;
    }

    public void setIsSaveBetRate(Integer isSaveBetRate) {
        this.isSaveBetRate = isSaveBetRate;
    }

    public UserGroupBetRateReqVo getUserGroupBetRateReqVo() {
        return userGroupBetRateReqVo;
    }

    public void setUserGroupBetRateReqVo(UserGroupBetRateReqVo userGroupBetRateReqVo) {
        this.userGroupBetRateReqVo = userGroupBetRateReqVo;
    }

    public Long[] getUserId() {
        return userId;
    }

    public void setUserId(Long[] userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @NotNull
    public String getRemake() {
        return remake;
    }

    public void setRemake(String remake) {
        this.remake = remake;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

}
