package com.panda.sport.rcs.console.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author :  Sean
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.console.dto
 * @Description :  TODO
 * @Date: 2020-03-11 18:01
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchHomeAwayDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 球队名称
     */
    private String teamName;
    /**
     * 主客队
     */
    private String teamPosition;
    /**
     * 语言
     */
    private String languageType;

}
