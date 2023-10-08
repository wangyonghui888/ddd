package com.panda.sport.rcs.pojo;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
    * 自定义多语言
    */
@Data
public class RcsLanguageInternation extends RcsBaseEntity<RcsLanguageInternation> {

    private Long id;

    /**
    * 文字对应的编码
    */
    private String nameCode;

    /**
    * json  language_type,text 
    */
    private String text;

    private Date createTime;

    private Date updateTime;
}