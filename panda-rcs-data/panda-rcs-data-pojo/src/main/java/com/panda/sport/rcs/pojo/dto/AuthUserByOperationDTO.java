package com.panda.sport.rcs.pojo.dto;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import com.panda.sport.rcs.pojo.RcsBroadCast;
import com.panda.sport.rcs.pojo.RcsMarketNumStatis;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.pojo.dto
 * @Description :  TODO
 * @Date: 2020-10-22 17:28
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class AuthUserByOperationDTO extends RcsBaseEntity<AuthUserByOperationDTO> {
    /**
     * 消息
     **/
    MessageDTO messageDTO;
    /**
     * 有权限的玩家id
     **/
    Set<Integer> userId;
}
