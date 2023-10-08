package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.MatchStatisticsInfoMapper;
import com.panda.sport.rcs.pojo.MatchStatisticsInfo;
import com.panda.sport.rcs.task.wrapper.MatchStatisticsInfoService;
import com.panda.sport.rcs.vo.RedCardVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName MatchStatisticsInfoServiceImpl
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/11
 **/
@Service
@Slf4j
public class MatchStatisticsInfoServiceImpl extends ServiceImpl<MatchStatisticsInfoMapper, MatchStatisticsInfo> implements MatchStatisticsInfoService {

    @Autowired
    MatchStatisticsInfoMapper matchStatisticsInfoMapper;

    @Override
    public MatchStatisticsInfo getMatchInfoByMatchId(Long matchId) {
        QueryWrapper<MatchStatisticsInfo> matchStatisticsInfoQueryWrapper = new QueryWrapper<>();
        matchStatisticsInfoQueryWrapper.eq(matchId != null, "standard_match_id", matchId);
        matchStatisticsInfoQueryWrapper.last("limit 1");
        MatchStatisticsInfo matchStatisticsInfo = matchStatisticsInfoMapper.selectOne(matchStatisticsInfoQueryWrapper);
        if (matchStatisticsInfo == null) {
            matchStatisticsInfo = new MatchStatisticsInfo();
        }
        return matchStatisticsInfo;
    }

    @Override
    public RedCardVo selectRedCardNum(Long standardMatchId) {
        RedCardVo cardVo = new RedCardVo();
        try {
            MatchStatisticsInfo matchStatisticsInfo = getMatchInfoByMatchId(standardMatchId);
            if (null != matchStatisticsInfo && StringUtils.isNotBlank(matchStatisticsInfo.getRedCardScore())) {
                String redCardScore = matchStatisticsInfo.getRedCardScore();
                String[] split = redCardScore.split(":");
                cardVo.setHomeNum(Integer.parseInt(split[0]));
                cardVo.setAwayNum(Integer.parseInt(split[1]));
                cardVo.setSportId(Long.parseLong(matchStatisticsInfo.getSportId()));
                cardVo.setStandardMatchId(standardMatchId);
            }
        } catch (Exception e) {
            log.error("获取队伍红牌数错误", e);
        }
        return cardVo;
    }
}
