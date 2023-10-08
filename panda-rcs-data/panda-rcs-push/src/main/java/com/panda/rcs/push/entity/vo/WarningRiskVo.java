package com.panda.rcs.push.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class WarningRiskVo  implements Serializable {

    private  String standardMatchId;

    private  Integer marketCategoryId;

    private  boolean sign;

    private  String linkId;

}
