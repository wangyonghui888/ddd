package com.panda.sport.rcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.predict.RcsPredictForecastPlayMapper;
import com.panda.sport.rcs.mapper.predict.pending.RcsPredictPendingForecastPlayMapper;
import com.panda.sport.rcs.pojo.vo.api.request.QueryForecastPlayReqVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecastPlay;
import com.panda.sport.rcs.pojo.vo.predict.pending.RcsPredictPendingForecastPlay;
import com.panda.sport.rcs.service.IRcsPredictForecastPlayService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 足球玩法forecast 服务实现类
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
@Service
public class RcsPredictForecastPlayServiceImpl extends ServiceImpl<RcsPredictForecastPlayMapper, RcsPredictForecastPlay> implements IRcsPredictForecastPlayService {
    @Autowired
    private RcsPredictForecastPlayMapper mapper;
    @Autowired
    private RcsPredictPendingForecastPlayMapper rcsPredictPendingForecastPlayMapper;

    private static RcsPredictForecastPlay buildRcsPredictForecastPlay(RcsPredictPendingForecastPlay rcsPredictPendingForecastPlay) {
        RcsPredictForecastPlay rcsPredictForecastPlay = new RcsPredictForecastPlay();
        BeanUtils.copyProperties(rcsPredictPendingForecastPlay, rcsPredictForecastPlay);
        return rcsPredictForecastPlay;
    }

    @Override
    public List<RcsPredictForecastPlay> selectList(QueryForecastPlayReqVo vo) {
        if (vo.getPendingType() == 0)
            return mapper.selectList(vo);
        else {
            LambdaQueryWrapper<RcsPredictPendingForecastPlay> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(!ObjectUtils.isEmpty(vo.getDataType()), RcsPredictPendingForecastPlay::getDataType, vo.getDataType());
            wrapper.eq(!ObjectUtils.isEmpty(vo.getMatchId()), RcsPredictPendingForecastPlay::getMatchId, vo.getMatchId());
            wrapper.eq(!ObjectUtils.isEmpty(vo.getPlayId()), RcsPredictPendingForecastPlay::getPlayId, vo.getPlayId());
            wrapper.eq(!ObjectUtils.isEmpty(vo.getMatchType()), RcsPredictPendingForecastPlay::getMatchType, vo.getMatchType());
            if (!ObjectUtils.isEmpty(vo.getScore())) {
                wrapper.orderByAsc(RcsPredictPendingForecastPlay::getScore);
            }
            List<RcsPredictPendingForecastPlay> rcsPredictPendingForecastPlays = rcsPredictPendingForecastPlayMapper.selectList(wrapper);
            if (!CollectionUtils.isEmpty(rcsPredictPendingForecastPlays)) {
                return rcsPredictPendingForecastPlays.stream().map(RcsPredictForecastPlayServiceImpl::buildRcsPredictForecastPlay).collect(Collectors.toList());
            }
        }
        return null;
    }
}
