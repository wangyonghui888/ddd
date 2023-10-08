package com.panda.rcs.push.service;

import com.panda.sport.rcs.pojo.dto.MatchEventInfoDTO;

public interface MatchEventService {

    void handlerMatchEvent(MatchEventInfoDTO eventDto, String linkId);

}
