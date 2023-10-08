package com.panda.sport.rcs.common.vo.api.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@ApiModel(value = "二級标签  接收vo", description = "")
@Data
public class UserProfileSecondTagListReqVo {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "中英文标签名称")
    private Map<String, String> tagNames;

    @ApiModelProperty(value = "中英文标签说明")
    private Map<String, String> tagDetails;

    @ApiModelProperty(value = "更新人ID")
    private Long updateUserId;

    @ApiModelProperty(value = "更新人")
    private String updateUserName;

    @ApiModelProperty(value = "更新人")
    private Long updateTime;

}


