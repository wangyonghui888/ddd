package com.panda.sport.rcs.console.mq.impl;

import com.panda.sport.rcs.console.pojo.RcsStandardSportMarketSellFlowing;
import com.panda.sport.rcs.console.pojo.Request;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MarketSellFlowingPushImpl extends ConsumerAdapter<Request<RcsStandardSportMarketSellFlowing>> {

    @Override
    public Boolean handleMs(Request<RcsStandardSportMarketSellFlowing> msg, Map<String, String> paramsMap) {
        return true;
    }
}