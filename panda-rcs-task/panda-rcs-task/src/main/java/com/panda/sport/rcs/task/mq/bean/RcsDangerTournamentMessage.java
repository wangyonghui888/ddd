package com.panda.sport.rcs.task.mq.bean;

import com.panda.sport.rcs.pojo.danger.RcsDangerTournament;
import lombok.Data;

@Data
public class RcsDangerTournamentMessage extends RcsDangerTournament {

    private String type;
}
