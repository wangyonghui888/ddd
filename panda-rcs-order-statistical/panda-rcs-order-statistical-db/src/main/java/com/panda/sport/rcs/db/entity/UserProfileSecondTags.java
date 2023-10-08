package com.panda.sport.rcs.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 用户画像二级标签管理表
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-25
 */
@Data
@ApiModel(value="UserProfileSecondTags对象", description="用户画像二级标签管理表")
public class UserProfileSecondTags implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "标签名称")
    private String tagName;

    @ApiModelProperty(value = "标签说明")
    private String tagDetail;

    @ApiModelProperty(value = "标签英文名称")
    private String englishTagName;

    @ApiModelProperty(value = "标签英文说明")
    private String englishTagDetail;

    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;

    @ApiModelProperty(value = "修改人名称")
    private String updateUserName;

    @ApiModelProperty(value = "修改時間")
    private Long updateTime;

}
