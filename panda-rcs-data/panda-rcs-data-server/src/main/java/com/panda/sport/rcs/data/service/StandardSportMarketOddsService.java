package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMarketOddsMessageDTO;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;

import java.util.List;

/**
 * <p>
 * 赛事盘口交易项表 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
public interface StandardSportMarketOddsService extends IService<StandardSportMarketOdds> {

	int batchSaveOrUpdate(List<StandardMarketOddsMessageDTO> listStandardSportMarketOdds);

	/**
	 * 根据盘口ID查询投注项列表
	 *
	 * @param marketId
	 * @return
	 * @author Paca
	 */
	List<StandardSportMarketOdds> list(Long marketId);

}
