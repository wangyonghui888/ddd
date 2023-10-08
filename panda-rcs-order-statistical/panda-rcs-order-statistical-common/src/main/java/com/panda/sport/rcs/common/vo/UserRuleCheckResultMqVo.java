package com.panda.sport.rcs.common.vo;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class UserRuleCheckResultMqVo implements Serializable {

    private static final long serialVersionUID = 1L;


    public String requestId;
    /**
     * 用户id
     */
    Long userId;


    /**
     * 标签id
     */
    public Long tagId;

    /**
     * 规则id
     */
    Long ruleId;
    /**
     * 规则code
     */
    String ruleCode;
    /**
     * 是否满足规则 0不滿足 1滿足
     */
    String result;
    /**
     * 实际值
     */
    String data;

    /**
     * 时间戳  当日0点0分
     */
    Long time;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}
