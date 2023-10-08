package com.panda.sport.sdk.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class RcsMarketCategorySetRelation implements Serializable {
    /**
     * 表ID，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 玩法集id
     */
    private Long marketCategorySetId;
    /**
     * 玩法id
     */
    private Long marketCategoryId;
    /**
     * 排序值。
     */
    private Integer orderNo;
    /**
     * 创建时间. UTC时间，精确到毫秒
     */
    private Long createTime;

}