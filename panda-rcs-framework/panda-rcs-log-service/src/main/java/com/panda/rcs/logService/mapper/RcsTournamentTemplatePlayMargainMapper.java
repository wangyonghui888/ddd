package com.panda.rcs.logService.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.vo.RcsTournamentTemplatePlayMargain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;



/**
 * @Description 联赛玩法margin配置表
 * @Param
 * @Author toney
 * @Date 20:02 2020/5/10
 * @return
 **/
@Repository
@Mapper
public interface RcsTournamentTemplatePlayMargainMapper extends BaseMapper<RcsTournamentTemplatePlayMargain> {
    /**
     * @Description   //查询联赛盘口差和赔率变化配置
     * @Param [rcsMatchMarketConfig]
     * @Author  Sean
     * @Date  9:48 2020/10/11
     * @return com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargainRef
     **/
    RcsTournamentTemplatePlayMargain queryTournamentAdjustRangeByPlayId(@Param("config") LogAllBean rcsMatchMarketConfig);

   }