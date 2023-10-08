package com.panda.sport.rcs.data.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.panda.sport.rcs.core.db.annotation.Master;
import com.panda.sport.rcs.data.mapper.StandardSportTeamMapper;
import com.panda.sport.rcs.data.service.IStandardSportTeamService;
import com.panda.sport.rcs.pojo.StandardSportTeam;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * <p>
 * 标准球队信息表.
球队id 与比赛id 作为唯一性约束 服务实现类
 * </p>
 *
 * @author Vector
 * @since 2019-09-26
 */
@Service
@Slf4j
public class StandardSportTeamServiceImpl extends ServiceImpl<StandardSportTeamMapper, StandardSportTeam> implements IStandardSportTeamService {

    @Autowired
    StandardSportTeamMapper standardSportTeamMapper;

    @Override
    public int batchInsert(List<StandardSportTeam> standardSportTeams) {
        if(CollectionUtils.isEmpty(standardSportTeams)){return 0;}
        List<StandardSportTeam> standardSportTeams1 = removeDupliById(standardSportTeams);
        return standardSportTeamMapper.batchInsert(standardSportTeams1);
    }

    @Override
    public List<StandardSportTeam> listByListIds(ArrayList<Long> sportTeamListLongs) {
        return standardSportTeamMapper.selectBatchIds(sportTeamListLongs);
    }

    @Override
    public int batchInsertOrUpdate(List<StandardSportTeam> standardSportTeams) {
        if(CollectionUtils.isEmpty(standardSportTeams)){return 0;}
        List<StandardSportTeam> standardSportTeams1 = removeDupliById(standardSportTeams);
        return standardSportTeamMapper.batchInsertOrUpdate(standardSportTeams1);
    }

    /**
     * 根据对象属性去重  属性：id
     * @param teams
     * @return
     */
    public static List<StandardSportTeam> removeDupliById(List<StandardSportTeam> teams) {
        Set<StandardSportTeam> set = new TreeSet<>((o1, o2) -> o1.getId().compareTo(o2.getId()));
        set.addAll(teams);
        return new ArrayList<>(set);
    }


}
