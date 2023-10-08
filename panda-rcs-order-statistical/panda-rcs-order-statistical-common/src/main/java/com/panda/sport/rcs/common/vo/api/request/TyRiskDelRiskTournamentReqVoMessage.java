package com.panda.sport.rcs.common.vo.api.request;

import lombok.Data;
@Data
public class TyRiskDelRiskTournamentReqVoMessage extends  TyRiskDelRiskTournamentReqVo{
    private String type;
    private Long tournamentId;
}

