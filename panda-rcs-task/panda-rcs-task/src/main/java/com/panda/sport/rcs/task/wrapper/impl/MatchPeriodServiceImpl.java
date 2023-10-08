package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.MatchPeriodMapper;
import com.panda.sport.rcs.pojo.MatchPeriod;
import com.panda.sport.rcs.task.wrapper.MatchPeriodService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName MatchPeriodServiceImpl
 * @Description: TODO
 * @Author Vector
 * @Date 2019/11/19
 **/
@Service
public class MatchPeriodServiceImpl extends ServiceImpl<MatchPeriodMapper, MatchPeriod> implements MatchPeriodService {

    @Resource
    private MatchPeriodMapper matchPeriodMapper;

    @Override
    public MatchPeriod getOne(Long macthId, Integer period) {
        QueryWrapper<MatchPeriod> wrapper = new QueryWrapper();
        wrapper.lambda().eq(MatchPeriod::getStandardMatchId, macthId);
        wrapper.lambda().eq(MatchPeriod::getPeriod, period);
        return matchPeriodMapper.selectOne(wrapper);
    }

    @Override
    public String selectRecentScoreTeam(Long macthId, String score, Integer coreType) {
        String result = "";
        try {
            if (StringUtils.isNotBlank(score)) {
                QueryWrapper<MatchPeriod> wrapper = new QueryWrapper();
                wrapper.lambda().eq(MatchPeriod::getStandardMatchId, macthId);
                wrapper.lambda().orderByDesc(true, MatchPeriod::getModifyTime, MatchPeriod::getPeriod);
                wrapper.last("limit 1");
                MatchPeriod matchPeriod = matchPeriodMapper.selectOne(wrapper);
                if (matchPeriod != null) {
                    String periodScore = matchPeriod.getScore();
                    if (coreType == 1) {
                        periodScore = matchPeriod.getScore();
                    } else if (coreType == 2) {
                        periodScore = matchPeriod.getCornerScore();
                    }
                    if (score.equals(periodScore)) {
                        QueryWrapper<MatchPeriod> wrapper1 = new QueryWrapper();
                        wrapper1.lambda().eq(MatchPeriod::getStandardMatchId, macthId);
                        wrapper1.lambda().lt(MatchPeriod::getModifyTime, matchPeriod.getModifyTime());
                        wrapper1.lambda().orderByDesc(true, MatchPeriod::getModifyTime, MatchPeriod::getPeriod);
                        wrapper.last("limit 1");
                        MatchPeriod period = matchPeriodMapper.selectOne(wrapper);
                        if (period != null) {
                            result = getName(periodScore, period.getScore());
                            if (coreType == 1) {
                                result = getName(periodScore, period.getScore());
                            } else if (coreType == 2) {
                                result = getName(periodScore, period.getCornerScore());
                            }
                        }
                    } else {
                        result = getName(score, periodScore);
                    }
                } else {
                    result = getName(score, score);
                }
            }
        } catch (Exception e) {
            log.error("获取最近比分队伍失败", e);
        }

        return result;
    }

    String getName(String newScore, String oldScore) {
        String result = "";
        if (newScore.equals(oldScore)) {
            String[] scoreSplit = newScore.split(":");
            Integer score1 = Integer.parseInt(scoreSplit[0]);
            Integer score2 = Integer.parseInt(scoreSplit[1]);
            if (score1 > 0) result = "home";
            if (score1 > score2) result = "home";
            if (score1 < score2) result = "away";
        } else {
            String[] scoreSplit = newScore.split(":");
            String[] periodScoreSplit = oldScore.split(":");
            Integer score1 = Integer.parseInt(scoreSplit[0]);
            Integer score2 = Integer.parseInt(scoreSplit[1]);
            Integer periodScore1 = Integer.parseInt(periodScoreSplit[0]);
            Integer periodScore2 = Integer.parseInt(periodScoreSplit[1]);
            if (score2 > periodScore2) result = "away";
            if (score1 > periodScore1) result = "home";
        }

        return result;
    }
}
