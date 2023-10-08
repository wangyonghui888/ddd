package com.panda.sport.rcs.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 清理Bean子类
 */
@Data
public class ClearSubDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 赛事id
     */
    private Long matchId;
    /**
     * 玩法id
     */
    private Long playId;

    /**
     * 盘口id
     */
    private Long marketId;

    /**
     * 投注项类型
     */
    private String oddsType;

    /**
     * 投注项id
     */
    private Long marketOddsId;

    /**
     * 盘口位置
     */
    private Integer placeNum;

    private String subPlayId;


    private Integer type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClearSubDTO that = (ClearSubDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(matchId, that.matchId) &&
                Objects.equals(playId, that.playId) &&
                Objects.equals(marketId, that.marketId) &&
                Objects.equals(oddsType, that.oddsType) &&
                Objects.equals(marketOddsId, that.marketOddsId) &&
                Objects.equals(placeNum, that.placeNum) &&
                Objects.equals(subPlayId, that.subPlayId) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, matchId, playId, marketId, oddsType, marketOddsId, placeNum, subPlayId, type);
    }
}
