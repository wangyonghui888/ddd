package com.panda.sport.rcs.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class RcsMarketNumStatisWsVo implements Serializable {

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
     * 投注项货量
     */
    List<RcsMarketNumStatisVo> rcsMarketNumStatisVoList;




}
