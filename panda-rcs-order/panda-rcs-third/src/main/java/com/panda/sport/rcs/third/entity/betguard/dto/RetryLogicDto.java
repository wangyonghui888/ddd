package com.panda.sport.rcs.third.entity.betguard.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Beulah
 * @date 2023/3/29 19:08
 * @description BetPlaced 请求实体
 */

@Data
public class RetryLogicDto implements Serializable {

    private static final long serialVersionUID = -1606664692753764046L;

    // 详见AuthToken parameter说明。（必填）
    private String AuthToken;

    //详见上方TS parameter说明
    private Long TS;

    // 详见上方TS parameter说明
    private String Hash;

    //BC后端中的唯一交易 ID，用于识别与投注有关的对PM后端的每次调用。这不是投注 ID。
    //PM后端应保留此 ID 以消除重复调用。 它还用于回滚调用以取消以前的事务。 （必填）
    private Long TransactionId;

    //BC后端中用于识别投注的唯一 ID。 在与BC的 LiveChat 和其他支持服务交谈时，应参考此值。（必填）
    private Long BetId;

}
