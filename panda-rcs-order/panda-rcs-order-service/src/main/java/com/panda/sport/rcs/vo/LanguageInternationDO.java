package com.panda.sport.rcs.vo;

import lombok.Data;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.vo
 * @Description :  TODO
 * @Date: 2020-07-23 19:26
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class LanguageInternationDO {
    /**
     * 文字内容.  name_code 代表文字在 language_type代表语言下的 具体内容.比如:中国 在 英文的表示  是China.
     */
    private String text;
    /**
     * 语言类型. zh jp en 等
     */
    private String languageType;
    /**
     * 玩法Id
     */
    private Long playId;
}
