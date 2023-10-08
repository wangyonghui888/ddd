package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsMatchPlayConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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

}
