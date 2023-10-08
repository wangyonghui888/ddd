package com.panda.sport.rcs.pojo.resp;

import com.panda.sport.rcs.pojo.RcsSpecEventConfig;
import lombok.Data;

import java.util.List;

/**
 * 业务逻辑
 *
 * @author gray
 * @version 1.0.0
 * @date 2023/4/11 13:52
 */
@Data
public class RcsSpecEventConfigResp {
    
    /**
     * 事件级开关
     */
    private Integer matchSpecEventSwitch;
    /**
     * 事件列表
     */
    private List<RcsSpecEventConfig> specEventList;
    
}
