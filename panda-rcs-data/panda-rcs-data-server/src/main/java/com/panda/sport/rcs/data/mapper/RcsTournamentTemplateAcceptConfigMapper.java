package com.panda.sport.rcs.data.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTemplateEventInfoConfig;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfig;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfigVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-09-05
 */
public interface RcsTournamentTemplateAcceptConfigMapper extends BaseMapper<RcsTournamentTemplateAcceptConfig> {
    /**
     * @return java.util.List<com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfig>
     * @Description //查询赛事事件接拒单配置
     * @Param [matchId]
     * @Author sean
     * @Date 2020/11/7
     **/
    List<RcsTournamentTemplateAcceptConfig> selectOrderAcceptConfig(@Param("matchId") Long matchId);

    RcsTemplateEventInfoConfig selectOrderAcceptConfigNew(@Param("matchId") Long matchId, @Param("categorySetId") Long categorySetId, @Param("eventCode") String eventCode);


    /**
     * @return com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfig
     * @Description //更新赛事数据源和时间配置
     * @Param [config]
     * @Author Sean
     * @Date 15:40 2020/9/5
     **/
    void updateMatchDataSourceAndTimeConfig(@Param("config") RcsTournamentTemplateAcceptConfigVo config);

    /**
     * @return com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfig
     * @Description //根据赛事查询接拒单时间和数据源配置
     * @Param [config]
     * @Author Sean
     * @Date 11:26 2020/9/5
     **/
    String queryConfigCode(@Param("sportId") Long sportId, @Param("matchId") Long matchId, @Param("categorySetId") Long categorySetId);

    /**
     * @return com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfig
     * @Description //根据赛事提前结算接拒数据源配置
     * @Param [config]
     * @Author Sean
     * @Date 11:26 2020/9/5
     **/
    String querySettleCode(@Param("sportId") Long sportId, @Param("matchId") Long matchId, @Param("categorySetId") Long categorySetId);

    int updateMatchDataSourceByTemplateId(@Param("dataSourceCode") String dataSourceCode, @Param("templateId") Long templateId);

}
