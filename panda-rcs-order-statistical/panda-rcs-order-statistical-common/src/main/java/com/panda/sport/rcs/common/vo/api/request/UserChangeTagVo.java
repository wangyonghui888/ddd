package com.panda.sport.rcs.common.vo.api.request;

import java.io.Serializable;

/**
 * 修改用户标签vo
 * @Date: 2022-3-27 16:32:08
 */
public class UserChangeTagVo implements Serializable {

    private Long userId;

    /**
     * 提交类型  1 提交商户决策 2 强制执行
     */
    private Integer submitType;

    /**
     * 用户标签(修改后)
     */
    private Integer tagId;

    /**
     * 标签名称
     */
    private String tagName;
    /**
     * 风控补充说明
     */
    private String supplementExplain;


    /**
     * 备注
     */
    private String remark;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getSubmitType() {
        return submitType;
    }

    public void setSubmitType(Integer submitType) {
        this.submitType = submitType;
    }

    public String getSupplementExplain() {
        return supplementExplain;
    }

    public void setSupplementExplain(String supplementExplain) {
        this.supplementExplain = supplementExplain;
    }
}
