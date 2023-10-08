package com.panda.sport.rcs.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 标准球类表. 【数据来自融合表：standard_sport_type】
 * </p>
 *
 * @author dorich
 * @since 2020-07-17
 */
@ApiModel(value="SSport对象", description="标准球类表. 【数据来自融合表：standard_sport_type】")
public class SSport implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "国际化字段")
    private Long nameCode;

    @ApiModelProperty(value = "球类名称")
    private String name;

    @ApiModelProperty(value = "当前运动的介绍. 默认为空，请保持在120个文字以内！")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "名称大写字母拼写")
    private String spell;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }
    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    @Override
    public String toString() {
        return "SSport{" +
            "id=" + id +
            ", nameCode=" + nameCode +
            ", name=" + name +
            ", remark=" + remark +
            ", createTime=" + createTime +
            ", modifyTime=" + modifyTime +
            ", spell=" + spell +
        "}";
    }
}
