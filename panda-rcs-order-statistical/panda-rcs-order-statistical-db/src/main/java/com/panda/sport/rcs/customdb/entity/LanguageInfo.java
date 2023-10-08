package com.panda.sport.rcs.customdb.entity;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.entity
 * @description :  语言信息
 * @date: 2020-07-22 16:28
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class LanguageInfo {

    private long nameCode;

    private String name;

    public LanguageInfo(long nameCode, String name) {
        this.nameCode = nameCode;
        this.name = name;
    }

    public LanguageInfo() {
        
    }
    
    public long getNameCode() {
        return nameCode;
    }

    public void setNameCode(long nameCode) {
        this.nameCode = nameCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
