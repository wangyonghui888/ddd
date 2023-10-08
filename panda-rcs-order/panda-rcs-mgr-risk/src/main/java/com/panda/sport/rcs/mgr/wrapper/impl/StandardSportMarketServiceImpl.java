package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.panda.sport.rcs.common.MapUtils;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketOddsMapper;
import com.panda.sport.rcs.mgr.wrapper.StandardMatchInfoService;
import com.panda.sport.rcs.mgr.wrapper.StandardSportMarketService;
import com.panda.sport.rcs.mgr.wrapper.StandardSportTeamService;
import com.panda.sport.rcs.mgr.wrapper.StandardSportTournamentService;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.StandardSportTournament;
import com.panda.sport.rcs.vo.SportMarketOddsQueryVo;
import com.panda.sport.rcs.vo.SportMarketOddsVo;
import com.panda.sport.rcs.vo.SportMarketVo;
import com.panda.sport.rcs.vo.SportMatchInfoVo;
import com.panda.sport.rcs.vo.SportTeam;
import com.panda.sport.rcs.vo.SportTournamentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 盘口服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Slf4j
@Service
public class StandardSportMarketServiceImpl extends ServiceImpl<StandardSportMarketMapper, StandardSportMarket> implements StandardSportMarketService {

    @Autowired
    private StandardMatchInfoService matchService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private StandardSportTournamentService tournamentService;
    @Autowired
    private StandardSportMarketOddsMapper sportMarketOddsMapper;
    @Autowired
    private StandardSportTeamService sportTeamService;

    @Autowired
    private StandardSportMarketMapper standardSportMarketMapper;


    /**
     * 从DB加载赛事相关依赖数据
     *
     * @param matchEntity
     * @return
     */
    @Override
    public SportMatchInfoVo loadMatchInfoTreeVo(StandardMatchInfo matchEntity,List<Long> marketCategoryIds) {
        SportMatchInfoVo matchInfoVo = new SportMatchInfoVo();
        BeanUtils.copyProperties(matchEntity, matchInfoVo);
        // 构建联赛数据
        StandardSportTournament tournamentEntity = tournamentService.getById(matchInfoVo.getStandardTournamentId());
        if (tournamentEntity != null) {
            SportTournamentVo tournamentVo = new SportTournamentVo();
            BeanUtils.copyProperties(tournamentEntity, tournamentVo);
            matchInfoVo.setTournament(tournamentVo);
        }
        // 构建球队数据
        List<SportTeam> sportTeams = sportTeamService.queryTeamsByMatchId(matchEntity.getId());
        matchInfoVo.setTeamList(sportTeams);

        // 盘口
        SportMarketOddsQueryVo condition = new SportMarketOddsQueryVo();
        condition.setStandardMatchInfoId(matchEntity.getId());
        List<SportMarketOddsQueryVo> marketOddsQueryVoList = sportMarketOddsMapper.selectSportMarketOddsList(condition,marketCategoryIds);
        if (!CollectionUtils.isEmpty(marketOddsQueryVoList)) {
            // 构建盘口数据集
            matchInfoVo.setMarketMap(this.groupMarkets(marketOddsQueryVoList));
        }

        return matchInfoVo;
    }

    private Map<Long, SportMarketVo> groupMarkets(List<SportMarketOddsQueryVo> marketOddsQueryVoList) {
        // 1.按盘口ID分组
        Multimap<Long, SportMarketOddsQueryVo> multiMap = ArrayListMultimap.create();
        // 返回结果
        Map<Long, SportMarketVo> marketResultMap = new HashMap<>();
        for (SportMarketOddsQueryVo marketOdds : marketOddsQueryVoList) {
            multiMap.put(marketOdds.getMarketId(), marketOdds);
        }
        Map<Long, Collection<SportMarketOddsQueryVo>> marketOddsMap = multiMap.asMap();
        // 2.每组内数据转换成关联关系
        for (Map.Entry<Long, Collection<SportMarketOddsQueryVo>> entry : marketOddsMap.entrySet()) {
            SportMarketVo marketVo = new SportMarketVo();
            boolean firstLoop = true;
            for (SportMarketOddsQueryVo moq : entry.getValue()) {
                if (firstLoop) {
                    BeanUtils.copyProperties(moq, marketVo);
                    marketResultMap.put(entry.getKey(), marketVo);
                    marketVo.setMarketOddsMap(new HashMap<>());
                    marketVo.setOrderNo(moq.getMarketTempletOrderNo());
                    marketVo.setNameCode(moq.getMarketTempletNameCode());// 取模板名
                    marketVo.setStatus(moq.getStatus());
                    marketVo.setId(moq.getMarketId());
                    firstLoop = false;
                }
                SportMarketOddsVo odds = new SportMarketOddsVo();
                odds.setCreateTime(moq.getMarketOddsCreateTime());
                odds.setDataSourceCode(moq.getMarketOddsDataSourceCode());
                odds.setOddsValue(moq.getOddsValue());
                odds.setId(moq.getMarketOddsId());
                odds.setManagerConfirmPrize(moq.getMarketOddsManagerConfirmPrize());
                odds.setMarketId(moq.getMarketId());
                odds.setName(moq.getMarketOddsName());
                odds.setModifyTime(moq.getMarketOddsModifyTime());
                odds.setNameCode(moq.getOddsTempletNameCode());// 取模板名
                odds.setOddsFieldsTempletId(moq.getOddsFieldsTempletId());// 取操作项id
                odds.setOrderOdds(moq.getOrderOdds());
                odds.setNameExpressionValue(moq.getNameExpressionValue());
                odds.setTargetSide(moq.getTargetSide());
                odds.setThirdOddsFieldSourceId(moq.getThirdOddsFieldSourceId());
                odds.setOrderNo(moq.getOddsTempletOrderNo());
                odds.setAddition1(moq.getMarketOddsAddition1());
                odds.setAddition2(moq.getMarketOddsAddition2());
                odds.setAddition3(moq.getMarketOddsAddition3());
                odds.setAddition4(moq.getMarketOddsAddition4());
                odds.setAddition5(moq.getMarketOddsAddition5());
                odds.setActive(moq.getActive());
                odds.setI18nNames(moq.getI18nNames());
                odds.setOddsType(moq.getOddsType());
                odds.setBetAmount(moq.getBetAmount());
                odds.setBetNum(moq.getBetOrderNum());
                odds.setProfitValue(moq.getProfitValue());
                marketVo.getMarketOddsMap().put(moq.getMarketOddsId(), odds);
            }
            // 盘口投注项排序：按模板配置顺序
            marketVo.setMarketOddsMap(MapUtils.sortByValues(marketVo.getMarketOddsMap()));
        }
        // 盘口排序：按玩法配置排序
        return MapUtils.sortByValues(marketResultMap);
    }

    /**
     * 从缓存中获取赛事盘口赔率树
     *
     * @param matchId
     * @return
     */
    @Override
    public SportMatchInfoVo getMatchInfoTreeVoFromCache(Long matchId) {
        SportMatchInfoVo  matchInfoVo = null;
            try {
                    // 缓存中无赛事则从数据库重新加载并设置
                    StandardMatchInfo matchEntity = matchService.getById(matchId);
                    log.info("load odds from DB");
                    if (matchEntity == null) {
                        log.warn("Fail to sync market odds data: can not found match from DB, matchId:{}", matchId);
                        return null;
                    }
                    matchInfoVo = loadMatchInfoTreeVo(matchEntity,null);
            } catch (Exception e) {
                log.error("::{}::从缓存中获取赛事盘口赔率树异常：{}",matchId,e.getMessage(), e);
            }
        return matchInfoVo;
    }

    /**
     * @Description   按matchId和playName进行查询数据库
     * @Param [matchId, playName]
     * @Author  toney
     * @Date  15:09 2019/12/10
     * @return java.util.List<com.panda.sport.rcs.pojo.StandardSportMarket>
     **/
    @Override
    public List<StandardSportMarket> queryMakertInfoByMatchIdAndPlayName(Long matchId,String playName){
        return standardSportMarketMapper.queryMakertInfoByMatchIdAndPlayName(matchId,playName);
    }

    @Override
    public StandardSportMarket selectMainMarketInfo(Long matchId,Long playId,String subPlayId) {
        return this.baseMapper.selectMainMarketInfo(matchId,playId,subPlayId);
    }
}
