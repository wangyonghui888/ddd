package com.panda.sport.rcs.common.vo.api.request;

import com.panda.sport.rcs.common.bean.PageBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@ApiModel(value = "危险投注 列表获取  vo对象", description = "危险投注 列表获取  vo对象")
public class DangerousListReqVo extends PageBean implements Serializable {

    @ApiModelProperty(value = "所属球类 0全部  非0表示对应的体育id")
    private Integer sportId;
    
    @ApiModelProperty(value = "名称")
    private String ruleName;

    public Integer getSportId() {
        return sportId;
    }

    public void setSportId(Integer sportId) {
        this.sportId = sportId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}

