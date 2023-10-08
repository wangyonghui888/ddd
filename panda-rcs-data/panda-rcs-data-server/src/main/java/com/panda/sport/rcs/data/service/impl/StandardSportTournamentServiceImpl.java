package com.panda.sport.rcs.data.service.impl;

import com.panda.sport.rcs.core.db.annotation.Master;
import com.panda.sport.rcs.data.mapper.StandardSportMarketCategoryMapper;
import com.panda.sport.rcs.data.mapper.StandardSportTournamentMapper;
import com.panda.sport.rcs.data.service.IStandardSportTournamentService;
import com.panda.sport.rcs.pojo.StandardSportTournament;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * SR中对应tournament
BC中对应Competition
 服务实现类
 * </p>
 *
 * @author Vector
 * @since 2019-09-26
 */
@Service
public class StandardSportTournamentServiceImpl extends ServiceImpl<StandardSportTournamentMapper, StandardSportTournament> implements IStandardSportTournamentService {

    @Autowired
    StandardSportTournamentMapper standardSportTournamentMapper;

    @Override
    public Long getLastCrtTime() {
        return standardSportTournamentMapper.getLastCrtTime();
    }

    @Override
    public int batchInsert(List<StandardSportTournament> standardSportTournaments) {
        if(CollectionUtils.isEmpty(standardSportTournaments)){return 0;}
        return standardSportTournamentMapper.batchInsert(standardSportTournaments);
    }

    @Override
    public List<StandardSportTournament> listByListIds(ArrayList<Long> sportTournamentDataLongs) {
        return standardSportTournamentMapper.selectBatchIds(sportTournamentDataLongs);
    }

    @Override
    public int batchInsertOrUpdate(List<StandardSportTournament> standardSportTournaments) {
        if(CollectionUtils.isEmpty(standardSportTournaments)){return 0;}
        return standardSportTournamentMapper.batchInsertOrUpdate(standardSportTournaments);
    }
}
