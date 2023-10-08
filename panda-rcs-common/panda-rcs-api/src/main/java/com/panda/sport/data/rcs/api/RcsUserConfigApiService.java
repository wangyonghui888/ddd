package com.panda.sport.data.rcs.api;

import java.util.HashMap;
import java.util.List;

import com.panda.sport.data.rcs.dto.special.RcsUserConfigVo;
import com.panda.sport.data.rcs.dto.special.RcsUserSpecialBetLimitConfigVo;

/**
 * @program: xindaima
 * @description:
 * @author: kimi
 * @create: 2021-02-05 14:56
 **/
public interface RcsUserConfigApiService {
    /**
     *
     * @param userId
     * @return
     */
     RcsUserSpecialBetLimitConfigVo getList(Long userId);

    /**
     *
     * @param userIdList
     * @return
     */
    HashMap<Long, RcsUserConfigVo> getRcsUserConfigVo(List<Long> userIdList);
}
