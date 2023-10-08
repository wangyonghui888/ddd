package com.panda.sport.rcs.console.pojo;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName LanguageInternation
 * @Description: TODO 
 * @Author Vector
 * @Date 2019/10/8 
**/
@Data
@Table(name = "language_internation")
public class LanguageInternation {
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
    * 数据来源编码. SR BC等
    */
    @Column(name = "data_source_code")
    private String dataSourceCode;

    /**
    * 语言类型. zh jp en 等
    */
    @Column(name = "language_type")
    private String languageType;

    /**
    * 文字内容.  name_code 代表文字在 language_type代表语言下的 具体内容.比如:中国 在 英文的表示  是China. 
    */
    @Column(name = "text")
    private String text;
    @Column(name = "remark")
    private String remark;
    @Column(name = "create_time")
    private Long createTime;
    @Column(name = "modify_time")
    private Long modifyTime;


}