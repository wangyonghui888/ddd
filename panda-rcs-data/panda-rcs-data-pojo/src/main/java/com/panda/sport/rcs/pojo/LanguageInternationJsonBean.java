package com.panda.sport.rcs.pojo;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

import java.util.Date;

/**
    * 自定义多语言json序列化bean
    */
@Data
public class LanguageInternationJsonBean extends RcsBaseEntity<LanguageInternationJsonBean> {


    /**
     * 语言类型. zh jp en 等
     */
    private String languageType;

    /**
    * json  language_type,text 
    */
    private String text;


}