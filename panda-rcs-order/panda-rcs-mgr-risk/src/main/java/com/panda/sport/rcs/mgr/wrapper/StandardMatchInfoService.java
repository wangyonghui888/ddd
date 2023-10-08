package com.panda.sport.rcs.mgr.wrapper;


import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.vo.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
public interface StandardMatchInfoService extends IService<StandardMatchInfo> {

    /**
     * 组合条件查询数据库赛事数据
     *
     * @param marketLiveOddsQueryVo
     * @return
     */
    List<StandardMatchInfo> queryMatches(MarketLiveOddsQueryVo marketLiveOddsQueryVo);

    List<Long> selectByMap(Map<String, Object> map);

    /**
     * 根据赛事ID查询赛事信息
     * @param matchId
     * @return
     */
    StandardMatchInfo getMacthInfoById(Long matchId);
}
