package com.panda.sport.data.rcs.api;

import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.MtsgGetMaxStakeDTO;


public interface MtsApiService {

    /**
     * 查询串关最大下注额
     * @param requestParam
     * @return
     */
    Response<Long> getMaxStake(Request<MtsgGetMaxStakeDTO> requestParam);

    /**
     * 查询单注最大下注额
     * @param requestParam
     * @return
     */
    Response<Long> getSingleMaxStake(Request<ExtendBean> requestParam);

}
