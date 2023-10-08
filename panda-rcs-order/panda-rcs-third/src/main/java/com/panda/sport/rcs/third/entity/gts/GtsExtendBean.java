package com.panda.sport.rcs.third.entity.gts;

import com.panda.sport.data.rcs.dto.ExtendBean;
import lombok.Data;

/**
 * @author lithan
 * @date 2023-01-08 10:49:09
 * gts专用 扩展bean 用于取第三方原始数据 和第三方交互
 */
@Data
public class GtsExtendBean extends ExtendBean {
    /**
     * betgeniusContent参数对象
     */
    private GtsBetGeniusContentVo betgeniusContent;
    /**
     * bookmakerContent 参数对象
     */
    private BookmakerContentContentVo bookmakerContent;
}
