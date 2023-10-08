package com.panda.sport.rcs.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author :  riben
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.pojo.dto
 * @Description :  TODO
 * @Date: 2020-10-24 17:49
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchTradeMemoRemindDTO implements Serializable{
    /**
     * 操盘手id
     */
    private String traderId;

    /**
     * 提醒发送的的赛事id
     */
    private List<Long> matchIds;

}
