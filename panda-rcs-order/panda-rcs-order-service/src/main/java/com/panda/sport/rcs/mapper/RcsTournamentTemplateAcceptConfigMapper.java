package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.vo.RcsTournamentTemplateAcceptConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 多语言 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Repository
public interface RcsTournamentTemplateAcceptConfigMapper extends BaseMapper<RcsTournamentTemplateAcceptConfig> {
    /**
     * @return java.util.List<com.panda.sport.rcs.pojo.RcsTournamentTemplateAcceptConfig>
     * @Description //根据玩法集和赛事查询事件配置
     * @Param [config]
     * @Author Sean
     * @Date 20:44 2020/9/5
     **/
    List<RcsTournamentTemplateAcceptConfig> queryWaitTimeConfig(@Param("config") OrderItem config, @Param("playSet") Integer playSet);

    List<RcsTournamentTemplateAcceptConfig> selectOrderAcceptConfigNew(@Param("matchId") Long matchId, @Param("categorySetId") Integer categorySetId);

    String queryConfigCode(@Param("sportId") Long sportId, @Param("matchId") Long matchId, @Param("categorySetId") Long categorySetId);

    int updateMatchDataSourceByTemplateId(@Param("dataSourceCode") String dataSourceCode, @Param("templateId") Long templateId);

    List<RcsTournamentTemplateAcceptConfig> selectOrderAcceptConfig(@Param("matchId") Long matchId);

}
