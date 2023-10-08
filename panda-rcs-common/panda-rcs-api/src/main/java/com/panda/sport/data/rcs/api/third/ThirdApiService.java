package com.panda.sport.data.rcs.api.third;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.bts.ThirdBetParamDto;

/**
 * @author Beulah
 * @date 2023/3/21 11:35
 * @description dubbo api 提供给sdk
 */

public interface ThirdApiService {

     Response<Long> getMaxBetAmount(Request<ThirdBetParamDto> requestParam);

}
