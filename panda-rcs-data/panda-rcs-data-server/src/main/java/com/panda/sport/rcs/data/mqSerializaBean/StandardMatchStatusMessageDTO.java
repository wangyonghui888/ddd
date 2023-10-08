package com.panda.sport.rcs.data.mqSerializaBean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author :  Jimmy
 * @Project Name :  data-realtime
 * @Package Name :  com.panda.sport.data.realtime.api.message
 * @Description :  TODO
 * @Date: 2019-10-07 16:48
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StandardMatchStatusMessageDTO implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
     * 标准赛事ID
     */
    private Long standardMatchId;

    /**
     * 运动种类id。 对应sport.id
     * 如果玩法不区分体育类型，传0，否则传对应体育类型标识
     */
    private Long sportId;

    /**
     * 赛事状态. 
     * 字典数据，对应 parent_type_id = 5
     */
    private Integer matchStatus;

    /**
     * 数据来源编码. 取值见: data_source.code
     */
    private String dataSourceCode;

    /**
     * 第三方赛事原始id. 该厂比赛在第三方数据供应商中的id. 比如:  SportRadar 发送数据时, 这场比赛的ID.
     */
    private String thirdMatchSourceId;

}
