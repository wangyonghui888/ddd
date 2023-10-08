package com.panda.sport.rcs.task.mq.bean;

import com.panda.sport.rcs.pojo.danger.RcsDangerTeam;
import lombok.Data;

@Data
public class RcsDangerTeamMessage extends  RcsDangerTeam{

    private String type;

}
