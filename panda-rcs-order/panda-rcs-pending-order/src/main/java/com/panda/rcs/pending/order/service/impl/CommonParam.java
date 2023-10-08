package com.panda.rcs.pending.order.service.impl;

import com.panda.rcs.pending.order.constants.NumberConstant;
import com.panda.rcs.pending.order.param.TournamentTemplateParam;
import com.panda.rcs.pending.order.pojo.RcsPendingOrder;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import org.apache.commons.lang3.math.NumberUtils;


public class CommonParam {

    public static TournamentTemplateParam getTemplateConfig(RcsPendingOrder pendingOrder) {
        TournamentTemplateParam tournamentTemplateParam = new TournamentTemplateParam();
        Integer matchType = pendingOrder.getMatchType()== NumberConstant.NUM_TWO?NumberConstant.NUM_ZERO:NumberConstant.NUM_ONE;
        tournamentTemplateParam.setMatchType(matchType);
        tournamentTemplateParam.setPlayId(pendingOrder.getPlayId().intValue());
        tournamentTemplateParam.setSportId(pendingOrder.getSportId().intValue());
        tournamentTemplateParam.setTypeVal(pendingOrder.getMatchId());
        return tournamentTemplateParam;
    }
}
