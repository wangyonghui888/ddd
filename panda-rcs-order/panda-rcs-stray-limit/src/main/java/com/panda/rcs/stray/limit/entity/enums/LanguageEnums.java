package com.panda.rcs.stray.limit.entity.enums;

public enum LanguageEnums {

    LANGUAGE_CHINA("zs", "中文"),
    LANGUAGE_ENGLISH("en", "英文");

    private String key;

    private String value;

    LanguageEnums(String key, String value){
        this.key = key;
        this.value = value;
    }

    public String getKey(){
        return this.key;
    }

    public String getValue(){
        return this.value;
    }

    public static LanguageEnums getLanguageEnums(String languageType) {
        LanguageEnums [] allLanguageArr = LanguageEnums.values();
        for (LanguageEnums em : allLanguageArr) {
            if (em.getKey().equals(languageType)) {
                return em;
            }
        }
        return null;
    }

}
