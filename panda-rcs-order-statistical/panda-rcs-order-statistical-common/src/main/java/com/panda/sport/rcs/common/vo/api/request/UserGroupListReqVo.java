package com.panda.sport.rcs.common.vo.api.request;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.panda.sport.rcs.common.bean.PageBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "玩家组列表及用户数 入参对象", description = "玩家组列表及用户数 入参对象")
public class UserGroupListReqVo extends PageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    /**
     * 排序字段名称
     */
    @ApiModelProperty(value = "排序字段名称")
    private String sortName;

    /**
     * 排序类型 1正序 2倒序
     */
    @ApiModelProperty(value = "排序类型 1正序 2倒序")
    private Integer sortType;

    public Integer getSortType() {
        return sortType;
    }

    public void setSortType(Integer sortType) {
        this.sortType = sortType;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getId() {
        if(StringUtils.isEmpty(id)){
            return null;
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
