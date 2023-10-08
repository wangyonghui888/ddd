package com.panda.sport.rcs.mapper.statistics;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.RcsMatchPlayConfig;
import com.panda.sport.rcs.vo.odds.MatchMarketPlaceConfig;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mapper.statistics
 * @Description :  TODO
 * @Date: 2020-01-15 21:19
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Repository
public interface RcsMatchPlayConfigMapper extends BaseMapper<RcsMatchPlayConfig> {
    /**
     * @return void
     * @Description //根据玩法id进行更新或者插入
     * @Param [matchId, playId, status, dataSource]
     * @Author kimi
     * @Date 2020/1/15
     **/
    void inserOrUpdate(@Param("matchId") Long matchId, @Param("playId") Integer playId, @Param("status") Integer status, @Param("dataSource") Integer dataSource);

    /**
     * @return void
     * @Description //TODO
     * @Param [matchId, playId, status, dataSource]
     * @Author kimi
     * @Date 2020/2/18
     **/
    void inserOrUpdateList(@Param("matchId") Long matchId, @Param("playIds") List<Integer> playIds, @Param("status") Integer status, @Param("dataSource") Integer dataSource);
    /**
     * @Description   //更新盘口差
     * @Param [config]
     * @Author  Sean
     * @Date  16:47 2020/10/6
     * @return void
     **/
    int insertOrUpdateMarketHeadGap(@Param("config") RcsMatchMarketConfig config);

	List<MatchMarketPlaceConfig> queryPlaceWaterConfigList(Map<String, Object> map);

	int updateMatchPlayPlaceList(Map<String, Object> map);
    /**
     * @Description   //子玩法更新水差
     * @Param [map]
     * @Author  sean
     * @Date   2021/7/29
     * @return int
     **/
    int updateMatchPlayPlaceSubList(Map<String, Object> map);
    /**
     * @Description   //子玩法查询水差
     * @Param [map]
     * @Author  sean
     * @Date   2021/7/29
     * @return java.util.List<com.panda.sport.rcs.vo.odds.MatchMarketPlaceConfig>
     **/
    List<MatchMarketPlaceConfig> queryPlaceWaterConfigSubList(Map<String, Object> map);
    /**
     * @Description   //新赛种保存水差
     * @Param [map]
     * @Author  sean
     * @Date   2021/10/1
     * @return int
     **/
    int insertOrUpdateNewSportWaterConfig(Map<String, Object> map);
    /**
     * @Description   //查询新赛种水差
     * @Param [map]
     * @Author  sean
     * @Date   2021/10/1
     * @return java.util.List<com.panda.sport.rcs.vo.odds.MatchMarketPlaceConfig>
     **/
    List<MatchMarketPlaceConfig> queryNewSportWaterConfig(Map<String, Object> map);
}
