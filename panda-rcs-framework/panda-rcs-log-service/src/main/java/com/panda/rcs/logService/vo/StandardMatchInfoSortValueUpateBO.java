package com.panda.rcs.logService.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class StandardMatchInfoSortValueUpateBO  implements Serializable {

    private Long standardMatchId;
    private Integer sortValue;

    private Integer oldValue;


}
