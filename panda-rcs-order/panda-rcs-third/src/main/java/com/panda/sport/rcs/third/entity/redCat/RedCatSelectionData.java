package com.panda.sport.rcs.third.entity.redCat;

import com.panda.merge.dto.I18nItemDTO;
import lombok.Data;

import java.util.List;

/**
 * 红猫数据投注项mq缓存类
 */
@Data
public class RedCatSelectionData {
    /**
     * 投注项id
     */
    private Long selectionId;
    /**
     * 原始投注项id
     */
    private String thirdOddsFieldSourceId;
    /**
     * 盘口id
     */
    private Long marketId;
    /**
     * 修改时间
     */
    private Long modifyTime;
    /**
     * 赔率类型
     */
    private String oddsType;
    /**
     * 标准赛事id
     */
    private Long standardMatchInfoId;

    /**
     * mq 消费时间
     */
    private Long mqConsumerTime;

    /**
     * 该盘口具体显示的值. 例如: 大小球中, 大小界限是:  3.5
     */
    private String oddsValue;
}
