package com.panda.sport.data.rcs.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * @author :  myname
 * @Project Name :  panda-rcs-common
 * @Package Name :  com.panda.sport.data.rcs.dto.order
 * @Description :  TODO
 * @Date: 2023-01-03 14:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchEventInfoRes implements Serializable {
    private Long matchId;
    private String dataSource;
}
