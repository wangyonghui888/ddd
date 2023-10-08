package com.panda.sport.rcs.pojo.param;

import com.panda.sport.rcs.pojo.dto.SpecEventChangeDTO;
import lombok.Data;

/**
 * 业务逻辑
 *
 * @author gray
 * @version 1.0.0
 * @date 2023/4/11 15:45
 */
@Data
public class AutoOpenMarketStatusParam {
    
    
    /**
     * 开关类型 0-系统级别  1-赛事级别
     */
    private Integer switchType;
    
    
    /**
     * 1：级别  2：联赛id   3：赛事id
     */
    private Integer type;
    
    
    /**
     * 根据type设置（1：设值联赛等级  2：设值联赛id   3：设值赛事id）
     */
    private Long typeVal;
    
    
    /**
     * 赛事级开关 1-开，2-关
     */
    private Integer switchStatus;

    /**
     * 操作類別編碼
     */
    private Integer operatePageCode;

    private AutoOpenMarketStatusParam beforeParams;
    
}
