package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.RcsTournamentMarketConfig;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标准玩法表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Repository
public interface StandardSportMarketCategoryMapper extends BaseMapper<StandardSportMarketCategory> {

	Map<String, Object> getMatchMarketConfig(Map<String, Object> params);

	Map<String, Object> getTournamentConfig(Map<String, Object> params);

	List<Map<String, Object>> queryOddsListByMarketId(Map<String, Object> params);

	RcsMatchMarketConfig queryRcsMatchMarketConfig(RcsMatchMarketConfig config);

	RcsMatchMarketConfig queryRcsMatchMarketConfigByPlayId(RcsMatchMarketConfig config);

	int updateMatchMarketConfig(RcsMatchMarketConfig config);

	int saveupdateTournamentConfig(RcsTournamentMarketConfig config);

	Map<String, Object> getTournamentConfigByMatchId(Map<String, Object> params);

	StandardSportMarketCategory queryCategoryInfoByMap(Map<String, Object> map);

	Map<String, Object> queryMatchMarketInfo(Map<String, Object> map);
}
