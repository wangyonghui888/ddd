package com.panda.sport.rcs.mapper.champion;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsChampionTradeConfig;
import com.panda.sport.rcs.pojo.vo.api.response.RcsChampionOddsFieldsResVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 多语言 Mapper 接口
 * @Description :  冠军玩法操盘及限额管理
 * </p>
 *
 * @author Kir
 * @since 2021-06-08
 */
public interface RcsChampionTradeConfigMapper extends BaseMapper<RcsChampionTradeConfig> {

    Integer selectMatchStatus(@Param("marketId") String marketId);

    List<RcsChampionOddsFieldsResVo> selectOddsFieldsList(@Param("marketId") String marketId);

    List<Map<String, Object>> selectBetAmount(@Param("matchId") String matchId, @Param("playId") Integer playId, @Param("marketId") String marketId);
}
