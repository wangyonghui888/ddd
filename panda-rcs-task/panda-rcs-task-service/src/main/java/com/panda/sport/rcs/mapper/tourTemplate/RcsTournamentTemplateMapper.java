package com.panda.sport.rcs.mapper.tourTemplate;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.dto.TournamentTemplateDto;
import com.panda.sport.rcs.pojo.dto.TournamentTemplateMarginDto;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Description   联赛模板配置
 * @Param 
 * @Author  toney
 * @Date  20:02 2020/5/10
 * @return 
 **/
@Component
public interface RcsTournamentTemplateMapper extends BaseMapper<RcsTournamentTemplate> {
    int deleteByPrimaryKey(Integer id);

    RcsTournamentTemplate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RcsTournamentTemplate record);

    int updateByPrimaryKey(RcsTournamentTemplate record);

    /**
     * 查询列表
     * @param map
     * @return
     */
    List<RcsTournamentTemplate> menuList(Map<String,Object> map);

    /**
     * 查询
     * @param type
     * @param typeVal
     * @return
     */
    RcsTournamentTemplate queryByTypeAndTypeVal(Integer type,Long typeVal);



    /**
     * 按联赛 id进行搜索，取联赛配置
     * @param tournamentId
     * @return
     */
    List<TournamentTemplateDto> queryByTournamentId(@Param("tournamentId") Long tournamentId, @Param("sportId") Integer sportId,@Param("matchType")Integer matchType);

    /**
     * 按联赛级别进行搜索,取模板
     * @param tournamentLevel
     * @return
     */
    List<TournamentTemplateDto> queryByTournamentLevel(@Param("tournamentLevel") Integer tournamentLevel, @Param("sportId") Integer sportId,@Param("matchType")Integer matchType);

    /**
     * @Description  滚球查询出涨额预警值
     * @Param []
     * @Author  kimi
     * @Date   2020/9/15
     * @return com.panda.sport.rcs.pojo.dto.TournamentTemplateMarginDto
     **/
    TournamentTemplateMarginDto selectRcsTournamentTemplateMarketWarnByLive(@Param("matchType")Integer matchType,@Param("type")Integer type,@Param("typeVal")Integer typeVal,
                                                                            @Param("playId")Integer  playId,@Param("timeVal")Integer timeVal);
    /**
     * @Description  早盘
     * @Param [matchType, type, typeVal, playId, timeVal]
     * @Author  kimi
     * @Date   2020/9/1y
     * @return com.panda.sport.rcs.pojo.dto.TournamentTemplateMarginDto
     **/
    TournamentTemplateMarginDto selectRcsTournamentTemplateMarketWarn(@Param("matchType")Integer matchType,@Param("type")Integer type,@Param("typeVal")Integer typeVal,
                                                                      @Param("playId")Integer  playId,@Param("timeVal")Integer timeVal);
    /**
     * @Description   //TODO
     * @Param [matchType, type, typeVal, playId, marketId]
     * @Author  kimi
     * @Date   2020/10/11
     * @return com.panda.sport.rcs.pojo.dto.TournamentTemplateMarginDto
     **/
    TournamentTemplateMarginDto selectRcsTournamentTemplateMarket(@Param("matchType")Integer matchType,@Param("type")Integer type,@Param("typeVal")Integer typeVal,
                                                                  @Param("playId")Integer  playId,@Param("marketIndex")Integer marketIndex);
    /**
     * @Description   //TODO
     * @Param [matchType, type, typeVal, playId, marketId]
     * @Author  kimi
     * @Date   2020/10/11
     * @return com.panda.sport.rcs.pojo.dto.TournamentTemplateMarginDto
     **/
    TournamentTemplateMarginDto selectRcsTournamentTemplateMarketSub(@Param("matchType")Integer matchType,@Param("type")Integer type,@Param("typeVal")Integer typeVal,
                                                                  @Param("playId")Integer  playId,@Param("marketIndex")Integer marketIndex);

    TournamentTemplateMarginDto selectSubPlayChuZhangConfig(@Param("matchId") Long matchId,
                                                            @Param("playId") Integer playId,
                                                            @Param("subPlayId") String subPlayId,
                                                            @Param("placeNum") Integer placeNum,
                                                            @Param("matchType") Integer matchType);
}