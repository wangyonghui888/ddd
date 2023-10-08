package com.panda.sport.rcs.third.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Beulah
 * @date 2023/3/29 19:49
 * @description todo
 */

@Getter
@AllArgsConstructor
public enum BetGuardLanguageEnum {

    ZH("zh", "Chinese"),

    EN("en", "English"),

    ET("et", "Estonian"),

    FR("fr", "French"),

    KA("ka", "Georgian"),

    LV("lv", "Latvian"),

    IT("it", "Lithuanian"),

    ME("me", "Montenegrian"),

    RU("ru", "Russian"),

    SR("sr", "Serbian"),

    ES("es", "Spanish"),

    TR("tr", "Turkish");

    private String code;
    private String language;
}
