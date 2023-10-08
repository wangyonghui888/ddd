package com.panda.sport.rcs.mapper.tourTemplate;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.dto.OddsChangeDTO;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lithan auto
 * @since 2020-09-15
 */
@Repository
public interface RcsTournamentTemplatePlayMargainMapper extends BaseMapper<RcsTournamentTemplatePlayMargain> {

    /**
     * @Description   //查询联赛盘口差和赔率变化配置
     * @Param [rcsMatchMarketConfig]
     * @Author  Sean
     * @Date  9:48 2020/10/11
     * @return com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargainRef
     **/
    RcsTournamentTemplatePlayMargain queryTournamentAdjustRangeByPlayId(@Param("config") RcsMatchMarketConfig rcsMatchMarketConfig);

    RcsTournamentTemplatePlayMargain queryTemplatePlay(@Param("matchId")Long matchId,@Param("categoryId")Long categoryId);
    /**
     * @Description   //其他球种跳分设置
     * @Param [config]
     * @Author  sean
     * @Date   2021/10/1
     * @return com.panda.sport.rcs.pojo.RcsMatchMarketConfig
     **/
    RcsMatchMarketConfig rcsTournamentConfig(@Param("config")RcsMatchMarketConfig config);

    List<RcsTournamentTemplatePlayMargain> getTemplatePlayMargainList(Integer templateId);

    /**
     * 根据赛事信息查询单个玩法信息
     * @param rcsMatchMarketConfig
     * @return
     */
    RcsTournamentTemplatePlayMargain selectPlayMarginByMatchInfoReject(@Param("config") RcsMatchMarketConfig rcsMatchMarketConfig);



}
