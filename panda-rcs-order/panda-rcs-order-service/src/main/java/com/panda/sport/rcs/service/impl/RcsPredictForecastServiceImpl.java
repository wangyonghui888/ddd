package com.panda.sport.rcs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.predict.RcsPredictForecastMapper;
import com.panda.sport.rcs.mapper.predict.pending.RcsPredictPendingForecastMapper;
import com.panda.sport.rcs.pojo.vo.api.request.QueryBetForMarketReqVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecast;
import com.panda.sport.rcs.pojo.vo.predict.pending.RcsPredictPendingForecast;
import com.panda.sport.rcs.service.IRcsPredictForecastService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 预测forecast表 服务实现类
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
@Service
public class RcsPredictForecastServiceImpl extends ServiceImpl<RcsPredictForecastMapper, RcsPredictForecast> implements IRcsPredictForecastService {
    @Autowired
    private RcsPredictForecastMapper mapper;

    @Autowired
    private RcsPredictPendingForecastMapper rcsPredictPendingForecastMapper;


    @Override
    public List<RcsPredictForecast> selectList(QueryBetForMarketReqVo vo) {
        return mapper.selectList(vo);
    }

    public List<RcsPredictForecast> pendingSelectList(QueryBetForMarketReqVo vo) {
        List<RcsPredictPendingForecast> rcsPredictPendingForecasts = rcsPredictPendingForecastMapper.selectRcsPredictPendingForecastList(vo);
        if (!CollectionUtils.isEmpty(rcsPredictPendingForecasts)) {
            return rcsPredictPendingForecasts.stream().map(this::copyRcsPredictPendingForecast).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private RcsPredictForecast copyRcsPredictPendingForecast(RcsPredictPendingForecast rcsPredictPendingForecast) {
        RcsPredictForecast rcsPredictForecast = new RcsPredictForecast();
        BeanUtils.copyProperties(rcsPredictPendingForecast, rcsPredictForecast);
        return rcsPredictForecast;
    }
}
