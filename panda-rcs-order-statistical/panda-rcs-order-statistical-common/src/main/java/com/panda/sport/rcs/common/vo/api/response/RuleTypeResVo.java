package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = " 规则类型 返回vo", description = "")
public class RuleTypeResVo {

    @ApiModelProperty(value = "规则类型 id ")
    private int typeId;
    @ApiModelProperty(value = "规则类型 名字")
    private String typeName;
    @ApiModelProperty(value = "标签类型 英文名字")
    private String englishTypeName;

    public String getEnglishTypeName() {
        return englishTypeName;
    }

    public void setEnglishTypeName(String englishTypeName) {
        this.englishTypeName = englishTypeName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
