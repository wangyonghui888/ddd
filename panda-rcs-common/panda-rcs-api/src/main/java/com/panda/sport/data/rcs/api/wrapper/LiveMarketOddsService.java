package com.panda.sport.data.rcs.api.wrapper;

import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.rcs.pojo.MatchBetChange;
import com.panda.sport.rcs.pojo.RcsMarketOddsConfig;
import org.springframework.stereotype.Service;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper
 * @Description :  TODO
 * @Date: 2019-11-13 17:11
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public interface LiveMarketOddsService {

    /**
     * 实时更新实货量、期望值
     *
     * @param marketOddsConfig
     * @return
     */
    Response oddsChanged(RcsMarketOddsConfig marketOddsConfig);

    /**
     * 同联赛赛事 实时更新实货量
     *
     * @param matchBetChange
     * @return
     */
    Response macthBetChanged(MatchBetChange matchBetChange);

}
