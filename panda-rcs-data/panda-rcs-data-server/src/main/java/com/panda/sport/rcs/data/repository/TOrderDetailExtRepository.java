package com.panda.sport.rcs.data.repository;

import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfig;

public interface TOrderDetailExtRepository {

    void updateOrderDetailExtToMongo(Long currentTime, RcsTournamentTemplateAcceptConfig config);


}
