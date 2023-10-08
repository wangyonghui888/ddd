package com.panda.sport.rcs.task.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.vo.MarketLiveOddsQueryVo;
import com.panda.sport.rcs.vo.StandardMatchInfoVo;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
public interface StandardMatchInfoService extends IService<StandardMatchInfo> {

    List<StandardMatchInfoVo> queryMatchesNoLimitByV2(MarketLiveOddsQueryVo marketLiveOddsQueryVo);

    List<StandardMatchInfo> selectMatchs(MarketLiveOddsQueryVo marketLiveOddsQueryVo);

    StandardMatchInfo selectOne(Long matchId);

    /**
     * 获取当前账务日赛事信息：赛事ID和赛种ID
     *
     * @param sportIds
     * @return
     */
    List<StandardMatchInfo> getCurrentBillDayMatchInfo(Collection<Long> sportIds);
}
