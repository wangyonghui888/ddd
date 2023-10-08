package com.panda.sport.rcs.data.calc.service;

import com.panda.merge.dto.StandardMarketDTO;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMarketMessageDTO;
import com.panda.sport.rcs.pojo.config.BuildMarketConfigDto;
import com.panda.sport.rcs.pojo.config.BuildMarketPlayConfig;

import java.util.List;

/**
 * @author :  holly
 * @Project Name :panda-rcs-new
 * @Package Name :com.panda.sport.rcs.data.calc.service
 * @Description :
 * @Date: 2020-10-03 14:16
 */
public interface MarketValueCalculatorService {

    /**
     * A+模式构建盘口
     *
     * @param market
     * @param config
     */
    void buildMarketList(StandardMarketMessageDTO market, BuildMarketConfigDto config);

    /**
     * A+模式构建盘口(让分,大小)
     *
     * @param matchId
     * @param market
     * @param config
     * @param isFirst
     * @return
     */
    List<StandardMarketDTO> buildMarketList(Long matchId, StandardMarketMessage market, BuildMarketPlayConfig config, boolean isFirst);

    /**
     * 篮球独赢封盘
     *
     * @param matchId
     * @param winPlayId
     */
    void basketballWinSeal(Long matchId, Long winPlayId);

    /**
     * A+模式构建盘口(单双)
     *
     * @param matchId
     * @param market
     * @param config
     * @param isFirst
     * @return
     */
    List<StandardMarketDTO> buildMarketSingleOperList(Long matchId, StandardMarketMessage market, BuildMarketPlayConfig config, boolean isFirst);
}
