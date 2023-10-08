package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 标签-用户关系表
 * </p>
 *
 * @author lithan auto
 * @since 2020-07-02
 */
@ApiModel(value = "更新用户", description = "")
public class TagUserModifyReqVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "标签id")
    @NotNull
    @Min(value = 1, message = "tagId最小值为1")
    private Long tagId;

    @ApiModelProperty(value = "用户id")
    @NotNull
    @Min(value = 1, message = "userId最小值为1")
    private Long userId;

    @ApiModelProperty(value = "操作人")
    private String manager;

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }
}
