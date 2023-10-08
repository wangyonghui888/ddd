package com.panda.sport.rcs.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 周区间
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-26
 */
public class ResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String result;

    private String rule;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}
