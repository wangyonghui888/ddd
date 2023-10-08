package com.panda.sport.rcs.pojo.tourTemplate;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panda.sport.rcs.utils.DateFormatSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RcsTournamentTemplateAcceptConfigSettle implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 模板id
     */
    private Long templateId;

    /**
     * 玩法集id
     */
    private Integer categorySetId;
    /**
     * SR  BC  BG
     */
    private String dataSource;

    /**
     * T常规
     */
    private Integer normal;

    /**
     * T延时
     */
    private Integer minWait;

    /**
     * 最大延时
     */
    private Integer maxWait;

    @JsonSerialize(using = DateFormatSerializer.class)
    private Date createTime;

    @JsonSerialize(using = DateFormatSerializer.class)
    private Date updateTime;
}
