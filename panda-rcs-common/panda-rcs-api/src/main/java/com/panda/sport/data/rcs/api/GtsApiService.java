package com.panda.sport.data.rcs.api;

import com.panda.sport.data.rcs.dto.GtsGetMaxStakeDTO;


public interface GtsApiService {

    /**
     * 查询最大下注额
     * @param requestParam
     * @return
     */
    Response<Long> getMaxStake(Request<GtsGetMaxStakeDTO> requestParam);



}
