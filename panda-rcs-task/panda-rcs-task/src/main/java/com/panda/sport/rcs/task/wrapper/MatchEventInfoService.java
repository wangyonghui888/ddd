package com.panda.sport.rcs.task.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.pojo.MatchEventInfo;
import com.panda.sport.rcs.vo.MatchMarketLiveOddsVo;
import com.panda.sport.rcs.vo.RedCardVo;

/**
 * @ClassName MatchEventInfoService
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/10
 **/
public interface MatchEventInfoService extends IService<MatchEventInfo> {

    void selectRecentMatchEventInfo(MatchMarketLiveBean matchMarketLiveBean);
}
