package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsChampionTradeConfig;
import com.panda.sport.rcs.pojo.vo.api.response.RcsChampionOddsFieldsResVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Kir
 * @since 2021-06-08
 * @Description :  冠军玩法操盘及限额管理
 */
public interface RcsChampionTradeConfigService extends IService<RcsChampionTradeConfig> {
    Integer selectMatchStatus(String marketId);

    List<RcsChampionOddsFieldsResVo> selectOddsFieldsList(String marketId);

    List<Map<String, Object>> selectBetAmount(String matchId, Integer playId, String marketId);
}
