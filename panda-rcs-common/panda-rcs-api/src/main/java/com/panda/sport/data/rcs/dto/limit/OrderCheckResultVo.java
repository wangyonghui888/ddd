package com.panda.sport.data.rcs.dto.limit;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Project Name: panda-rcs-order-group
 * @Package Name: com.panda.sport.rcs.dto.limit
 * @Description : 订单检查结果
 * @Author : Paca
 * @Date : 2020-10-10 20:39
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@AllArgsConstructor
public class OrderCheckResultVo implements Serializable {

    private static final long serialVersionUID = 207666748186239822L;

    private boolean pass;

    private String msg;

    private List<RedisUpdateVo> redisUpdateList;

}
