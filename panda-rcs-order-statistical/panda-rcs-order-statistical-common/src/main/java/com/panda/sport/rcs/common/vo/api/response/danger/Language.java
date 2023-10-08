package com.panda.sport.rcs.common.vo.api.response.danger;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Language {

    @ApiModelProperty(value = "语言类型:[en,zs]")
    private String languageType;

    @ApiModelProperty(value = "文本")
    private String text;

}
