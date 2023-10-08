package com.panda.sport.rcs.common.vo.api.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@ApiModel(value = "标签  返回vo", description = "")
@Data
public class TagListResVo {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "标签类型  1基本属性类 2投注特征类 3访问特征类 4财务特征类")
    private Integer tagType;

    @ApiModelProperty(value = "标签名称")
    private String tagName;

    @ApiModelProperty(value = "标签说明")
    private String tagDetail;

    @ApiModelProperty(value = "标签英文名称")
    private String englishTagName;

    @ApiModelProperty(value = "标签英文说明")
    private String englishTagDetail;

    @ApiModelProperty(value = "标签颜色")
    private String tagColor;

    @ApiModelProperty(value = "标签图标url")
    private String tagImgUrl;

    @ApiModelProperty(value = "标签复核天数")
    private Integer tagRecheckDays;

    @ApiModelProperty(value = "是否循环复核（0.否 1.是）默认为0否")
    private Integer isRecheck;

    @ApiModelProperty(value = "是否停止计算（0.否 1.是）默认为0否")
    private Integer isCalculate;

    @ApiModelProperty(value = "是否自动化（0.否 1.是）默认为0否")
    private Integer isAuto;

    @ApiModelProperty(value = "是否默认标签（0.否 1.是）默认为0否")
    private Integer isDefault;

    @ApiModelProperty(value = "是否不达标回退至上级标签（0.否 1.是）默认为0否")
    private Integer isRollback;

    @ApiModelProperty(value = "上级标签")
    private Long fatherId;

    @ApiModelProperty(value = "允许风控措施 0否 1是")
    private Integer riskStatus;

    @ApiModelProperty(value = "更新人ID")
    private Long updateUserId;

    @ApiModelProperty(value = "更新人")
    private String updateUserName;

    @ApiModelProperty(value = "更新人")
    private Long updateTime;

    @ApiModelProperty(value = "二級標籤ID")
    private Long levelId;

}


