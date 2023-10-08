package com.panda.sport.rcs.mgr.wrapper.impl;

import java.util.HashMap;
import java.util.Map;

import com.panda.sport.rcs.log.LogContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.mapper.RcsRectanglePlayMapper;
import com.panda.sport.rcs.pojo.RcsRectanglePlay;
import com.panda.sport.rcs.mgr.wrapper.RcsRectanglePlayService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author admin
 * @since 2019-10-04
 */
@Service
@Slf4j
public class RcsRectanglePlayServiceImpl extends ServiceImpl<RcsRectanglePlayMapper, RcsRectanglePlay> implements RcsRectanglePlayService {

	@Autowired
	private RcsRectanglePlayMapper playMapper;
	
	public Long queryPlayCurrentPaid(ExtendBean order) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("busId", order.getBusId());
		params.put("sportId", order.getSportId());
		params.put("dateExpect", order.getDateExpect());
		params.put("type", "2");
		params.put("typeValue", order.getUserId());
		params.put("matchId", order.getMatchId());
		params.put("playId", order.getCurrentPlayType());
		params.put("handicap", order.getHandicap());
		params.put("selectId", order.getSelectId());
		params.put("paidMoney", order.getCurrentMaxPaid());
		params.put("matchType", order.getIsScroll());
		params.put("playType", order.getPlayType());
		String maxPaid = playMapper.queryPlayListByMatch(params);
		return Double.valueOf(maxPaid).longValue();
	}

	/**
	 * 查询当前赛事不是比分推算的玩法所有赔付总和
	 */
	public Long queryMatchNoScorePlay(ExtendBean extendBean) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("busId", extendBean.getBusId());
		params.put("sportId", extendBean.getSportId());
		params.put("dateExpect", extendBean.getDateExpect());
		params.put("type", "2");
		params.put("typeValue", extendBean.getUserId());
		params.put("matchId", extendBean.getMatchId());
		params.put("playId", extendBean.getPlayId());
		params.put("handicap", extendBean.getHandicap());
		params.put("selectId", extendBean.getSelectId());
		params.put("paidMoney", extendBean.getCurrentMaxPaid());
		params.put("recType", extendBean.getRecType());
		params.put("matchType", extendBean.getIsScroll());
		params.put("playType", extendBean.getPlayType());
		log.info("requestId:{},UserMatchMaxPaid, queryMatchNoScorePlay:{}",LogContext.getContext().getRequestId(),params);

		String matchMaxAmount = playMapper.queryMatchNoScorePlay(params);
		
		return Double.valueOf(matchMaxAmount).longValue();
	}


	/**
	 * 查询当前赛事不是比分推算的玩法所有赔付总和
	 */
	public Long queryAllUserMatchNoScorePlay(ExtendBean extendBean) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("busId", extendBean.getBusId());
		params.put("sportId", extendBean.getSportId());
		params.put("dateExpect", extendBean.getDateExpect());
		params.put("type", "2");
		params.put("matchId", extendBean.getMatchId());
		params.put("playId", extendBean.getPlayId());
		params.put("handicap", extendBean.getHandicap());
		params.put("selectId", extendBean.getSelectId());
		params.put("paidMoney", extendBean.getCurrentMaxPaid());
		params.put("recType", extendBean.getRecType());
		params.put("matchType", extendBean.getIsScroll());
		params.put("playType", extendBean.getPlayType());
		log.info("requestId:{},UserMatchMaxPaid, queryMatchNoScorePlay:{}",LogContext.getContext().getRequestId(),params);

		String matchMaxAmount = playMapper.queryAllUserMatchNoScorePlay(params);

		return Double.valueOf(matchMaxAmount).longValue();
	}

	public Long queryAllMatchNoScorePlay(ExtendBean extendBean) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("busId", extendBean.getBusId());
		params.put("sportId", extendBean.getSportId());
		params.put("dateExpect", extendBean.getDateExpect());
		params.put("type", "2");
		params.put("typeValue", extendBean.getUserId());
		params.put("matchId", extendBean.getMatchId());
		params.put("playId", extendBean.getPlayId());
		params.put("handicap", extendBean.getHandicap());
		params.put("selectId", extendBean.getSelectId());
		params.put("paidMoney", extendBean.getCurrentMaxPaid());
		params.put("recType", extendBean.getRecType());
		params.put("matchType", extendBean.getIsScroll());
		params.put("playType", extendBean.getPlayType());
		String matchMaxAmount = playMapper.queryAllMatchNoScorePlay(params);
		
		return Double.valueOf(matchMaxAmount).longValue();
	}

	public Map<String, Object> queryMatchInfo(Map<String, Object> params) {
		return playMapper.queryMatchInfo(params);
	}

	@Override
	public int insertAndUpdate(ExtendBean extendBean) {
		return playMapper.insertAndUpdate(extendBean);
	}

}
