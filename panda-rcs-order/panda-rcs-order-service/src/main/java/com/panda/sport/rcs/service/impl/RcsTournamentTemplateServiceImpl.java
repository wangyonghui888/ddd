package com.panda.sport.rcs.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplateMapper;
import com.panda.sport.rcs.pojo.StandardSportTournament;
import com.panda.sport.rcs.pojo.dto.TournamentTemplateDto;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.service.IRcsTournamentTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.trade.wrapper.tourTemplate
 * @Description :  联赛模板
 * @Date: 2020-05-10 20:39
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class RcsTournamentTemplateServiceImpl extends ServiceImpl<RcsTournamentTemplateMapper, RcsTournamentTemplate> implements IRcsTournamentTemplateService {
//    @Autowired
//    private RcsTournamentTemplateMapper templateMapper;
//    @Autowired
//    private RedisClient redisClient;
//
//    @Autowired
//    private StandardSportTournamentService standardSportTournamentService;
//    /**
//     * 按联赛 id进行搜索，取联赛配置
//     * @param tournamentId
//     * @return
//     */
//    @Override
//    public List<TournamentTemplateDto> queryByTournamentId(Long tournamentId, Integer sportId,Integer matchType){
//        /*String json = redisClient.get(String.format(RedisKeys.TOURTEMPLATE_TOUR_ID,sportId,matchType,tournamentId));
//        if(StringUtils.isNotEmpty(json)){
//            return JSONObject.parseObject(json,new TypeReference<List<TournamentTemplateDto>>() {});
//        }
//        else {
//            List<TournamentTemplateDto> list = templateMapper.queryByTournamentId(tournamentId, sportId,matchType);
//            if(list!=null && list.size() > 0){
//                redisClient.setExpiry(String.format(RedisKeys.TOURTEMPLATE_TOUR_ID,sportId,matchType,tournamentId), JSONObject.toJSONString(list),10L);
//            }
//            return list;
//        }*/
//        return null;
//    }
//
//
//
//    /**
//     * 按联赛级别进行搜索,取模板
//     * @param tournamentLevel
//     * @return
//     */
//    @Override
//    public List<TournamentTemplateDto> queryByTournamentLevel(Integer tournamentLevel,Integer sportId,Integer matchType){
//        /*String json = redisClient.get(String.format(RedisKeys.TOURTEMPLATE_TOUR_LEVEL,sportId,matchType,tournamentLevel));
//        if(StringUtils.isNotEmpty(json)){
//            return JSONObject.parseObject(json,new TypeReference<List<TournamentTemplateDto>>() {});
//        }
//        else {
//            List<TournamentTemplateDto> list = templateMapper.queryByTournamentLevel(tournamentLevel, sportId,matchType);
//            if(list!=null && list.size() > 0){
//                redisClient.setExpiry(String.format(RedisKeys.TOURTEMPLATE_TOUR_LEVEL,sportId,matchType,tournamentLevel), JSONObject.toJSONString(list),10L);
//            }
//            return list;
//        }*/
//        return null;
//    }
//
//    /**
//     * 查询配置
//     * @param matchId
//     * @param tournamentId
//     * @return
//     */
//    @Override
//    public List<TournamentTemplateDto> query(Integer sportId,Long matchId,Long tournamentId,Integer matchType){
//        List<TournamentTemplateDto> list = queryByTournamentId(tournamentId,sportId,matchType);
//        if(list == null || list.size() <= 0){
//            StandardSportTournament tournament = standardSportTournamentService.getById(tournamentId);
//            if(tournament!=null) {
//                list = queryByTournamentLevel(tournament.getTournamentLevel(), sportId,matchType);
//            }
//        }
//        return list;
//    }

}
