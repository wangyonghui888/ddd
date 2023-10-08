package com.panda.sport.rcs.common.vo.api.request;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.panda.sport.rcs.common.bean.PageBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "根据玩家组ID查询用户列表 入参对象", description = "根据玩家组ID查询用户列表 入参对象")
public class UserListByGroupIdReqVo extends PageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "groupId")
    private String groupId;

    public String getGroupId() {
        if(StringUtils.isEmpty(groupId)){
            return null;
        }
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
