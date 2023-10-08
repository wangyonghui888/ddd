package com.panda.sport.rcs.pojo.dto;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

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
@AllArgsConstructor
@NoArgsConstructor
public class MatchTradeMemoRemindDTO extends RcsBaseEntity<MatchTradeMemoRemindDTO> {
    /**
     * 操盘手id
     */
    private String traderId;
    /**
     * 提醒发送的的赛事id
     */
    private Long matchId;
    /**
     * 提醒发送的的备忘录id
     */
    private String memoId;
    /**
     * 是否已读
     */
    private Boolean readFlag;
}
