package com.panda.sport.rcs.mgr.mq.bean;

import com.panda.sport.data.rcs.dto.UserItem;
import lombok.Data;

import java.util.List;

/**
 * @program: xindaima
 * @description:
 * @author: kimi
 * @create: 2021-01-06 15:57
 **/
@Data
public class UserItemBean extends UserItem {
    private List<String> sportList;
    private List<String> tournamentList;
    private List<String> playList;
    private List<String> orderTypeList;
    private List<String> orderStageList;

    private Integer settleInAdvance;
}
