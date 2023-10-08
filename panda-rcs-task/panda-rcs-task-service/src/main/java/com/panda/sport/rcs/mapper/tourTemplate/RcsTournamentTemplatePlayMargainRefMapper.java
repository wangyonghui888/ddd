
package com.panda.sport.rcs.mapper.tourTemplate;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsCategoryOddTemplet;
import com.panda.sport.rcs.pojo.dto.MatchTemplatePlayConfigDTO;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateComposeModel;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargainRef;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description 联赛模板margiain引用表
 * @Param
 * @Author toney
 * @Date 20:02 2020/5/10
 * @return
 **/
@Repository
public interface RcsTournamentTemplatePlayMargainRefMapper extends BaseMapper<RcsTournamentTemplatePlayMargainRef> {

    /**
     * 插入分时margin参数配置数据
     *
     * @param margainRefList
     * @return
     */
    int insertOrUpdateBatch(@Param("list") List<RcsTournamentTemplatePlayMargainRef> margainRefList);

    /**
     * 根据id修改分时margin参数数据
     *
     * @param param
     * @return
     */
    int updatePlayMargainRefById(RcsTournamentTemplatePlayMargainRef param);

    /**
     * 获取早盘上一个节点的margin值
     * @param param
     * @return
     */
    RcsTournamentTemplatePlayMargainRef selectPreLastPlayMargainRef(RcsTournamentTemplatePlayMargainRef param);

    /**
     * 获取滚球上一个节点的margin值
     * @param param
     * @return
     */
    RcsTournamentTemplatePlayMargainRef selectLiveLastPlayMargainRef(RcsTournamentTemplatePlayMargainRef param);

    /**
     * 获取所有模板配置
     * @return
     */
    List<RcsTournamentTemplateComposeModel> selectAllTemplates();

    RcsTournamentTemplatePlayMargainRef selectByPrimaryKey(Integer id);

	List<RcsCategoryOddTemplet> queryMatchOddTypeListByMatchIdAndCategoryId(Map<String, Object> map);

	RcsTournamentTemplatePlayMargainRef queryMatchMargainRefInfo(Map<String, Object> oldMargainRefParams);

	List<RcsTournamentTemplateComposeModel> selectAllTemplatesByNoMid(@Param("sportIds") Set<Integer> sportIds);

    List<RcsTournamentTemplateComposeModel> selectTemplatesByMatchId(@Param("matchId") Long matchId);

	RcsTournamentTemplatePlayMargain queryMatchMargainInfo(Map<String, Object> oldMargainRefParams);

	List<RcsTournamentTemplateComposeModel> selectAllTemplatesByFoot();

	List<RcsTournamentTemplateComposeModel> selectAllTemplatesByBasket();

    List<RcsTournamentTemplateComposeModel> selectAllTemplatesByTennis();

    List<RcsTournamentTemplateComposeModel> selectAllTemplatesByPingPong();

    List<RcsTournamentTemplateComposeModel> selectAllTemplatesByVolleyBall();

    List<RcsTournamentTemplateComposeModel> selectAllTemplatesBySnooker();

    List<RcsTournamentTemplateComposeModel> selectAllTemplatesByBaseBall();

    List<RcsTournamentTemplateComposeModel> selectAllTemplatesByBadminton();

    List<RcsTournamentTemplateComposeModel> selectAllTemplatesByIceHockeySync();

    List<RcsTournamentTemplateComposeModel> selectBoulesTemplatesByBasket(Map<String,Object> parmMap);

    List<RcsTournamentTemplateComposeModel> selectHandicapBoulesTemplatesByBasket(Map<String,Object> parmMap);
    List<RcsTournamentTemplateComposeModel> selectNoHandicapBoulesTemplatesByBasket(Map<String,Object> parmMap);

    List<RcsTournamentTemplateComposeModel> selectHandicapByMatchId(Map<String,Object> parmMap);

    List<RcsTournamentTemplateComposeModel> selectMatchPlayBoulesTemplatesByBasket(Map<String,Object> parmMap);

    /**
     * 查询模板玩法配置
     * @param matchId 赛事id
     * @param playIds 玩法id集合
     * @return
     */
    List<MatchTemplatePlayConfigDTO> selectTemplatePlayMatchId(@Param("matchId") Long matchId,@Param("playIds") List<Long> playIds);

    List<RcsTournamentTemplateComposeModel> selectAllLiveTemplatesByFoot();

    List<RcsTournamentTemplateComposeModel> selectAllZPTemplatesByFoot();
    List<RcsTournamentTemplateComposeModel> selectAllBasketballPreMatchId(@Param("eventMatchIds") List<Long> eventMatchIds);

    List<RcsTournamentTemplateComposeModel> selectAllBasketballPreMatchNodeData(@Param("eventMatchIds") List<Long> eventMatchIds);

    List<RcsTournamentTemplateComposeModel> selectAllLiveTemplatesByBasket();

    List<RcsTournamentTemplateComposeModel> selectAllZpTemplatesByBasket();

}