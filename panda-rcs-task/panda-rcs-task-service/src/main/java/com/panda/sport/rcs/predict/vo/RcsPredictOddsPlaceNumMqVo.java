package com.panda.sport.rcs.predict.vo;

import com.panda.sport.rcs.utils.CommonUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

@Data
public class RcsPredictOddsPlaceNumMqVo {

    /**
     * 唯一请求id
     */
    private String linkId;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 玩法ID
     */
    private Integer playId;

    /**
     * 1.早盘  2.滚球
     */
    private Integer matchType;

    /**
     * 类型  1投注项 2坑位
     */
    private Integer dataType;

    /**
     * 盘口ID /坑位ID
     */
    private Long dataTypeValue;

    /**
     * forecast数据
     */
    List<RcsPredictBetOddsVo> list;

    /**
     * 子玩法ID
     */
    private String subPlayId;

    /**
     * 0-滚球，1-早盘
     *
     * @return
     */
    public Integer convertMatchType() {
        if (NumberUtils.INTEGER_TWO.equals(matchType)) {
            return 0;
        } else {
            return 1;
        }
    }

    public String generateLinkId() {
        if (StringUtils.isBlank(linkId)) {
            return CommonUtils.getUUID() + "_task";
        }
        return linkId;
    }
}
