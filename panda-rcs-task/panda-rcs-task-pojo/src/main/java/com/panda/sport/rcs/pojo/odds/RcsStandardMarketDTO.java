package com.panda.sport.rcs.pojo.odds;

import com.panda.merge.dto.StandardMarketDTO;
import com.panda.merge.dto.StandardMarketOddsDTO;
import lombok.Data;

import java.util.List;

/**
 * @author :  sean
 * @Project Name :  panda-rcs-trade
 * @Package Name :  com.panda.sport.rcs.pojo.dto.odds
 * @Description :  TODO
 * @Date: 2020-12-02 14:44
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsStandardMarketDTO extends StandardMarketDTO {
    private static final long serialVersionUID = 1L;
    /**替换赔率顺序
        0-根据盘口id替换赔率；1-根据位置替换赔率; 2-根据盘口值替换赔率
     */
    private Integer oddsReplaceOrder;
    /*盘口id*/
    private String id;
}
