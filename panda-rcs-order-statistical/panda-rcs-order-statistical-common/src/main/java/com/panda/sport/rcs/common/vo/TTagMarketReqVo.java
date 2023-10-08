package com.panda.sport.rcs.common.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 行情等级表
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-04-08
 */
public class TTagMarketReqVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private int id;

    /**
     * 标签ID
     */
    private Integer tagId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 等级ID
     */
    private Integer levelId;

    /**
     * 等级ID
     */
    private String levelName;

    /**
     * 赔率增减
     */
    private BigDecimal oddsValue;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 最后修改时间
     */
    private Long updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public BigDecimal getOddsValue() {
        return oddsValue;
    }

    public void setOddsValue(BigDecimal oddsValue) {
        this.oddsValue = oddsValue;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}
