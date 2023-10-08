package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.MatchEventInfoMapper;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.pojo.MatchEventInfo;
import com.panda.sport.rcs.pojo.MatchStatisticsInfo;
import com.panda.sport.rcs.task.wrapper.MatchEventInfoService;
import com.panda.sport.rcs.task.wrapper.MatchStatisticsInfoService;
import com.panda.sport.rcs.vo.RedCardVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName MatchEventInfoServiceImpl
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/10
 **/
@Service
public class MatchEventInfoServiceImpl extends ServiceImpl<MatchEventInfoMapper, MatchEventInfo> implements MatchEventInfoService {

    @Autowired
    private MatchEventInfoMapper matchEventInfoMapper;

    @Autowired
    MatchStatisticsInfoService matchStatisticsInfoService;

    @Override
    public void selectRecentMatchEventInfo(MatchMarketLiveBean matchMarketLiveBean) {
        if (null == matchMarketLiveBean.getPeriod()||0==matchMarketLiveBean.getPeriod() || null == matchMarketLiveBean.getSecondsMatchStart() || 0 == matchMarketLiveBean.getSecondsMatchStart()) {
            QueryWrapper<MatchEventInfo> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(MatchEventInfo::getStandardMatchId, matchMarketLiveBean.getMatchId());
            wrapper.lambda().orderByDesc(MatchEventInfo::getCreateTime);
            wrapper.last("limit 1");
            MatchEventInfo matchEventInfo = matchEventInfoMapper.selectOne(wrapper);
            if (matchEventInfo != null) {
                matchMarketLiveBean.setSecondsMatchStart(matchEventInfo.getSecondsFromStart().intValue());
                matchMarketLiveBean.setPeriod(matchEventInfo.getMatchPeriodId().intValue());
            }
        }
    }
}
