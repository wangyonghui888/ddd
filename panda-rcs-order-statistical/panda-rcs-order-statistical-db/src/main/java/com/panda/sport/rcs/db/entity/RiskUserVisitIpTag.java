package com.panda.sport.rcs.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * ip标签表
 * </p>
 *
 * @author Kir
 *
 * @since 2021-01-31
 */
@TableName("risk_user_visit_ip_tag")
@ApiModel(value="RiskUserVisitIpTag对象", description="ip标签表")
public class RiskUserVisitIpTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "标签")
    private String tag;

    @ApiModelProperty(value = "创建时间 存当天的0点0分0秒")
    private Long createTime;

    @ApiModelProperty(value = "备注")
    private String remake;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getRemake() {
        return remake;
    }

    public void setRemake(String remake) {
        this.remake = remake;
    }

}
