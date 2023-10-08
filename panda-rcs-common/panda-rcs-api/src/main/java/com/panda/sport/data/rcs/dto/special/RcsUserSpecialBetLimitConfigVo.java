package com.panda.sport.data.rcs.dto.special;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: xindaima
 * @description:
 * @author: kimi
 * @create: 2020-12-11 16:26
 **/
@Data
public class RcsUserSpecialBetLimitConfigVo implements Serializable {
    private RcsUserConfigVo rcsUserConfigVo;
    private List<RcsUserSpecialBetLimitConfigDataVo> rcsUserSpecialBetLimitConfigDataVoList;
    private RcsOperationLogVo rcsOperationLogVo;
}


