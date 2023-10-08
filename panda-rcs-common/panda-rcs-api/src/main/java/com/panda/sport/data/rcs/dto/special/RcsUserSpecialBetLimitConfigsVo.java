package com.panda.sport.data.rcs.dto.special;

import lombok.Data;

import java.util.List;

/**
 * @program: xindaima
 * @description:
 * @author: kimi
 * @create: 2021-02-05 16:19
 **/
@Data
public class RcsUserSpecialBetLimitConfigsVo {
    private RcsUserConfigVo rcsUserConfigVo;
    private List<RcsUserSpecialBetLimitConfigDataVo> rcsUserSpecialBetLimitConfigDataVoList;
}
