package com.panda.sport.rcs.mgr.enums;

public enum LanguageTypeDataEnum {
    ZS("zs","中文"),
    EN("en","英文");
    private String type;
    private String value;

    private LanguageTypeDataEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
