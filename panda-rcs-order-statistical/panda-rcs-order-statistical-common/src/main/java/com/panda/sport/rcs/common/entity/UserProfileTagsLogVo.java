package com.panda.sport.rcs.common.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "二級標籤日誌对象")
@Data
public class UserProfileTagsLogVo implements Serializable {

    private Long id;
    /**
     * 标签类型  2投注特征类
     */
    private Integer tagType;
    /**
     * 标签名称
     */
    private String tagName;
    /**
     * 标签说明
     */
    private String tagDetail;
    /**
     * 标签颜色
     */
    private String tagColor;
    /**
     * 标签图标
     */
    private String tagImgUrl;

}
