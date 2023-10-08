package com.panda.sport.rcs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum MatchDisplayStatusEnum {

    MATCH_SUSPENDED(1, "封盘"),
    MATCH_ADD_SUSPENDED(21,  "累封"),
    MATCH_AVOID_SUSPENDED(25,  "防封");

    /**
     * id
     */
    private Integer id;

    /**
     * 描述
     */
    private String desc;


    /**
     * 获取封盘状态
     * @return
     */
    public static List<Integer> getSealIds() {
        List<Integer> ids = Arrays.stream(MatchDisplayStatusEnum.values()).map(MatchDisplayStatusEnum::getId).collect(Collectors.toList());
        return ids;
    }


}
