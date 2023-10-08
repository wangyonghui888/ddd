package com.panda.sport.rcs.pojo.dto;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.pojo.dto
 * @Description :  TODO
 * @Date: 2020-10-18 14:45
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutrightMarketTimeDTO extends RcsBaseEntity<OutrightMarketTimeDTO> {

    private Long standardMatchId;  //标准赛事id

    private Long relationMarketId; //统一盘口id

    private Long marketStartTime;   //盘口开始时间

    private Long marketEndTime;    //盘口结束时间

    private Long marketNextCloseTime;   //盘口关闭时间
}
