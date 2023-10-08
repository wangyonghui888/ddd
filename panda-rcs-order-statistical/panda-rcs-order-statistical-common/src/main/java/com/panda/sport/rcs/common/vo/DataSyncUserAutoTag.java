package com.panda.sport.rcs.common.vo;

import java.util.List;

/**
 * 大数据同步用户自动化标签数据
 * 需求1912 自动化标签
 * http://lan-confluence.sportxxxr1pub.com/pages/viewpage.action?pageId=64616690
 * http://lan-confluence.sportxxxr1pub.com/pages/viewpage.action?pageId=65318636
 *
 * @description:
 * @author: magic
 * @create: 2022-06-21 10:15
 **/
public class DataSyncUserAutoTag {
    private Long merchantId;
    /**
     * 商户代码
     */
    private String merchantCode;

    /**
     * 查询时间
     */
    private Long timeStamp;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 建议标签ID
     */
    private Long tagId;

    /**
     * 标签实际值
     */
    private List<RuleValue> ruleValues;

    /**
     * 需求：40494
     * 大数据会增加一个备注1字段存放话术模板，
     */
    private String remark1;

    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    public static class RuleValue {
        /**
         * 规则Code
         */
        private String ruleCode;

        /**
         * 规则值
         */
        private String value;

        public String getRuleCode() {
            return ruleCode;
        }

        public void setRuleCode(String ruleCode) {
            this.ruleCode = ruleCode;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }


    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public List<RuleValue> getRuleValues() {
        return ruleValues;
    }

    public void setRuleValues(List<RuleValue> ruleValues) {
        this.ruleValues = ruleValues;
    }


}