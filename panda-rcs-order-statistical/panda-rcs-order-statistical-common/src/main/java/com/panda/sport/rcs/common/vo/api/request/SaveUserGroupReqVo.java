package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "新增更新玩家组 接收 对象", description = "")
public class SaveUserGroupReqVo {

    private Long id;

    @ApiModelProperty(value = "新增用户id集合")
    private List<Long> addUserIdList;

    @ApiModelProperty(value = "移除用户id集合")
    private List<Long> removeUserIdList;

    @ApiModelProperty(value = "危险等级")
    private Integer dangerLevel;

    @ApiModelProperty(value = "操作者")
    private String operator;

    @ApiModelProperty(value = "玩家组名称")
    private String userGroupName;

    @ApiModelProperty(value = "备注/理由")
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getAddUserIdList() {
        return addUserIdList;
    }

    public void setAddUserIdList(List<Long> addUserIdList) {
        this.addUserIdList = addUserIdList;
    }

    public List<Long> getRemoveUserIdList() {
        return removeUserIdList;
    }

    public void setRemoveUserIdList(List<Long> removeUserIdList) {
        this.removeUserIdList = removeUserIdList;
    }

    public Integer getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(Integer dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getUserGroupName() {
        return userGroupName;
    }

    public void setUserGroupName(String userGroupName) {
        this.userGroupName = userGroupName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
