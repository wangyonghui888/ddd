package com.panda.rcs.push.entity.enums;

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

    public static com.panda.rcs.push.entity.enums.LanguageEnums getLanguageEnums(String languageType) {
        com.panda.rcs.push.entity.enums.LanguageEnums[] allLanguageArr = com.panda.rcs.push.entity.enums.LanguageEnums.values();
        for (com.panda.rcs.push.entity.enums.LanguageEnums em : allLanguageArr) {
            if (em.getKey().equals(languageType)) {
                return em;
            }
        }
        return null;
    }

}
