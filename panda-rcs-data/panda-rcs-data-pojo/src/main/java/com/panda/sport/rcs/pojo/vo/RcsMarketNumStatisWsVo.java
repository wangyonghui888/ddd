package com.panda.sport.rcs.pojo.vo;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import com.panda.sport.rcs.pojo.RcsMarketNumStatis;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 盘口位置统计表 ws推送用
 * </p>
 *
 * @author lithan auto
 * @since 2020-10-03
 */
@Data
public class RcsMarketNumStatisWsVo extends RcsBaseEntity<RcsMarketNumStatis>  {

    private static final long serialVersionUID = 1L;

    /**
     * 体育种类
     */
    private Long sportId;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 玩法ID
     */
    private Integer marketCategoryId;

    /**
     * 盘口位置
     */
    private Integer placeNum;

    /**
     * 1 早盘  2 滚球
     */
    private Integer matchType;

    /**
     * 货量清零 1是 0否
     */
    private Integer clearZero;

    /**
     * 投注项货量
     */
    List<RcsMarketNumStatisVo> rcsMarketNumStatisVoList;


    private List<Long> categoryIds;

}
