package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.data.rcs.api.BalanceValueService;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mgr.wrapper.BalanceService;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.vo.statistics.MarketBalanceVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
/**
 * @author :  holly
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.rcs.rpc.calculator.service.impl
 * @Description :
 * @Date: 2019-11-30 18:57
 */
@Slf4j
public class BalanceValueServiceImpl implements BalanceValueService {
    /**
     * 加载lua脚本和计算
     * @param fileName
     * @return
     */
    @Autowired
    public RedisClient redisClient;
    @Autowired
    BalanceService balanceService;
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    private RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;

    @Override
    public void zeroBalanceValue(Long matchId,Long marketId) {
        log.info("{} manual zeroBalanceValue , marketId{},matchId:{}",this.getClass(),marketId  , matchId);
        // redis 没有从redis重新计算
        QueryWrapper<StandardMatchInfo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StandardMatchInfo::getId, matchId);
        wrapper.lambda().select(StandardMatchInfo::getBeginTime);
        StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectOne(wrapper);
        if (ObjectUtils.isEmpty(standardMatchInfo)){
            throw new RcsServiceException("赛事不存在"+matchId);
        }
        if (ObjectUtils.isEmpty(standardMatchInfo.getBeginTime())){
            standardMatchInfo.setBeginTime(System.currentTimeMillis());
        }
        String dateExpect = DateUtils.getDateExpect(standardMatchInfo.getBeginTime());

        String key = String.format(RedisKeys.CALC_AMOUNT_ODDS_CHANGE, dateExpect,marketId);
        String keyPlus = String.format(RedisKeys.CALC_AMOUNT_ODDS_CHANGE_PLUS, dateExpect,marketId);
        String suffixKey = "{" + marketId + "}" ;
        redisClient.delete(key + suffixKey);
        redisClient.delete(keyPlus + suffixKey);
        redisClient.delete(key + ":count" + suffixKey);
        redisClient.delete(key + ":lock" + suffixKey);
        //清0平衡值
        MarketBalanceVo vo = new MarketBalanceVo();

//        QueryWrapper<RcsMatchMarketConfig> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().eq(RcsMatchMarketConfig :: getMarketId,marketId);
//        RcsMatchMarketConfig config = rcsMatchMarketConfigMapper.selectOne(queryWrapper);
        balanceService.updateBalance(matchId, marketId, vo,null);
    }
}
