package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsBusinessMatchPaidConfigMapper;
import com.panda.sport.rcs.pojo.RcsBusinessMatchPaidConfig;
import com.panda.sport.rcs.mgr.wrapper.RcsBusinessMatchPaidConfigService;
import com.panda.sport.rcs.vo.MatchPaidVo;
import com.panda.sport.rcs.vo.TournamentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 赛事最大赔付配置 服务实现类
 * </p>
 *
 * @author admin
 * @since 2019-10-04
 */
@Service
public class RcsBusinessMatchPaidConfigServiceImpl extends ServiceImpl<RcsBusinessMatchPaidConfigMapper, RcsBusinessMatchPaidConfig> implements RcsBusinessMatchPaidConfigService {
    @Autowired
    private RcsBusinessMatchPaidConfigMapper rcsBusinessMatchPaidConfigMapper;


    @Override
    public List<MatchPaidVo> getMatchPaidListView() {
        return rcsBusinessMatchPaidConfigMapper.getMatchPaidListView();
    }

    @Override
    public void updateRcsBusinessMatchPaidConfig(RcsBusinessMatchPaidConfig rcsBusinessMatchPaidConfig) {
        rcsBusinessMatchPaidConfigMapper.updateById(rcsBusinessMatchPaidConfig);
    }

    @Override
    public List<RcsBusinessMatchPaidConfig> getMatchPaids(Long businessId, Long sportId) {
        List<RcsBusinessMatchPaidConfig> rcsBusinessMatchPaidConfigs = rcsBusinessMatchPaidConfigMapper.selectList(businessId, sportId);
        return rcsBusinessMatchPaidConfigs;
    }

    @Override
    public RcsBusinessMatchPaidConfig getRusinessMatchPaidByTournamentLevelId(Long tournamentLevelId, Long businessId) {
        QueryWrapper<RcsBusinessMatchPaidConfig> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RcsBusinessMatchPaidConfig::getTournamentLevelId, tournamentLevelId);
        wrapper.lambda().eq(RcsBusinessMatchPaidConfig::getBusinessId, businessId);
        return rcsBusinessMatchPaidConfigMapper.selectOne(wrapper);
    }

    @Override
    public List<TournamentVo> selectTournaments(TournamentVo tournamentVo) {
        return rcsBusinessMatchPaidConfigMapper.selectTournaments(tournamentVo);
    }


}
