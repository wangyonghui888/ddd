package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 玩家组风控措施 入参VO
 * </p>
 *
 * @author Kir
 * @since 2021-01-29
 */
@ApiModel(value="UserGroupBetRateReqVo VO", description="玩家组风控措施")
public class UserGroupBetRateReqVo implements Serializable  {

    private static final long serialVersionUID = 1L;

    private List<TUserGroupBetRateReqVo> userGroupBetRateList;

    public List<TUserGroupBetRateReqVo> getUserGroupBetRateList() {
        return userGroupBetRateList;
    }

    public void setUserGroupBetRateList(List<TUserGroupBetRateReqVo> userGroupBetRateList) {
        this.userGroupBetRateList = userGroupBetRateList;
    }
}
