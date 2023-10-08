package com.panda.sport.rcs.third.entity.betguard.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Beulah
 * @date 2023/4/1 22:11
 * @description todo
 */
@Data
public class SelectionStateDto implements Serializable {
    private static final long serialVersionUID = -5536806236370187817L;

    //将指示当前投注中的投注项 ID。
    private String SelectionId;
    //State 字段的可能值为
    //  NotResulted = 0,
    //  Returned = 2
    //  Lost = 3
    //  Won = 4
    //  WinReturn = 5
    //  LossReturn = 6
    private Integer State;
}
