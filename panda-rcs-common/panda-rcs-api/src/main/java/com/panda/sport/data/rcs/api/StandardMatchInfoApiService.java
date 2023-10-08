package com.panda.sport.data.rcs.api;

import com.panda.sport.data.rcs.dto.StandardMatchInfoDTO;


public interface StandardMatchInfoApiService {

    /**
     * 赛事投递接口-实时状况
     * @param requestParam
     * @return
     */
    Response putSportMatchInfoRealTime(Request<StandardMatchInfoDTO> requestParam);

}
