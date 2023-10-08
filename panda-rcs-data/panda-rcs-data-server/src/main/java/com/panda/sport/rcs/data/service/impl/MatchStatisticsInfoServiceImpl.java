package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.MatchStatisticsInfoMapper;
import com.panda.sport.rcs.data.service.MatchStatisticsInfoService;
import com.panda.sport.rcs.pojo.MatchStatisticsInfo;
import com.panda.sport.rcs.pojo.dto.MatchStatisticsInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName MatchStatisticsInfoServiceImpl
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/11
 **/
@Service
public class MatchStatisticsInfoServiceImpl extends ServiceImpl<MatchStatisticsInfoMapper, MatchStatisticsInfo> implements MatchStatisticsInfoService {

    @Autowired
    MatchStatisticsInfoMapper matchStatisticsInfoMapper;

    @Override
    public int insertMatchStatisticsInfo(MatchStatisticsInfo matchStatisticsInfo) {
        QueryWrapper<MatchStatisticsInfo> matchStatisticsInfoQueryWrapper = new QueryWrapper<>();
        matchStatisticsInfoQueryWrapper.eq(matchStatisticsInfo.getStandardMatchId()!=null,"standard_match_id",matchStatisticsInfo.getStandardMatchId());
        matchStatisticsInfoQueryWrapper.last("limit 1");
        MatchStatisticsInfo oldData = matchStatisticsInfoMapper.selectOne(matchStatisticsInfoQueryWrapper);
        if (oldData == null) {
            matchStatisticsInfoMapper.insert(matchStatisticsInfo);
        } else {
            UpdateWrapper<MatchStatisticsInfo> matchStatisticsInfoUpdateWrapper = new UpdateWrapper<>();
            matchStatisticsInfoUpdateWrapper.eq(matchStatisticsInfo.getStandardMatchId()!=null,"standard_match_id",matchStatisticsInfo.getStandardMatchId());
            matchStatisticsInfoMapper.update(matchStatisticsInfo,matchStatisticsInfoUpdateWrapper);
        }
        return 0;
    }


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
    public int insertOrUpdate(MatchStatisticsInfo matchStatisticsInfo) {
        return matchStatisticsInfoMapper.insertOrUpdate(matchStatisticsInfo);
    }

    @Override
    public int insertOrUpdate(MatchStatisticsInfoDTO data) {
        return matchStatisticsInfoMapper.insertOrUpdateDto(data);

    }
}
