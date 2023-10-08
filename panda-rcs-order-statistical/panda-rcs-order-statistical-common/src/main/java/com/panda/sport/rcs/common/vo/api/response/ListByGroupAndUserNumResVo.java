package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 玩家组列表及用户数  返回vo
 * </p>
 *
 * @author Kir
 * @since 2021-01-29
 */
@ApiModel(value = "玩家组列表及用户数  返回vo", description = "玩家组列表及用户数  返回vo")
public class ListByGroupAndUserNumResVo {

    @ApiModelProperty(value = "玩家组ID")
    private int id;

    @ApiModelProperty(value = "玩家组名称")
    private String groupName;

    @ApiModelProperty(value = "玩家数")
    private int userNum;

    @ApiModelProperty(value = "最后修改人")
    private String modifyUser;

    @ApiModelProperty(value = "最后修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "理由/备注")
    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getUserNum() {
        return userNum;
    }

    public void setUserNum(int userNum) {
        this.userNum = userNum;
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
