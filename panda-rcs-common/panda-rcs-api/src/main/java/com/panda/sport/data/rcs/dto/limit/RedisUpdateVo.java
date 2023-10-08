package com.panda.sport.data.rcs.dto.limit;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Project Name: panda-rcs-order-group
 * @Package Name: com.panda.sport.rcs.dto.limit
 * @Description : Redis 更新记录
 * @Author : Paca
 * @Date : 2020-10-09 15:43
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@AllArgsConstructor
public class RedisUpdateVo implements Serializable {

    private static final long serialVersionUID = 3986443975209888756L;

    private String cmd;

    private String key;

    private String field;

    private String value;

    private String result;

}
