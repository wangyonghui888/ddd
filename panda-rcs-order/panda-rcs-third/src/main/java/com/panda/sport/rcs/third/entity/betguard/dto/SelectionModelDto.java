package com.panda.sport.rcs.third.entity.betguard.dto;

import lombok.Data;
import org.apache.poi.hpsf.Decimal;

import java.io.Serializable;

/**
 * @author Beulah
 * @date 2023/4/1 22:11
 * @description todo
 */
@Data
public class SelectionModelDto implements Serializable {
    private static final long serialVersionUID = 1L;

    //SelectionId
    private Long SelectionId;

    //Current odd of the selection at the time of cashout
    private Decimal Odd;
}
