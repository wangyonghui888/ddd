package com.panda.sport.rcs.console.pojo;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
    * 自定义多语言
    */
@Data
@Table(name = "rcs_language_internation")
public class RcsLanguageInternation {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
    * 文字对应的编码
    */
    @Column(name = "name_code")
    private String nameCode;

    /**
    * json  language_type,text 
    */
    @Column(name = "text")
    private String text;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;
}