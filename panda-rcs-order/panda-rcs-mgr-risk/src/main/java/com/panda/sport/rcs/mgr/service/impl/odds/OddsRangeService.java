package com.panda.sport.rcs.mgr.service.impl.odds;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.panda.merge.api.ITradeMarketOddsApi;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardMarketDTO;
import com.panda.merge.dto.StandardMarketOddsDTO;
import com.panda.merge.dto.StandardMatchMarketDTO;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.mapper.RcsOddsConvertMappingMapper;
import com.panda.sport.rcs.mapper.RcsOddsConvertMappingMyMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.mgr.service.impl.odds.api.OddsPublicMethodApi;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.RcsOddsConvertMapping;
import com.panda.sport.rcs.pojo.RcsOddsConvertMappingMy;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.utils.DataRealtimeApiUtils;
import com.panda.sport.rcs.vo.odds.RcsStandardMarketDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description //操盘的一些校验
 * @Param
 * @Author sean
 * @Date 2021/1/9
 * @return
 **/
@Service
@Slf4j
public class OddsRangeService {
    @Autowired
    private RcsOddsConvertMappingMyMapper rcsOddsConvertMappingMyMapper;
    @Autowired
    private RcsOddsConvertMappingMapper rcsOddsConvertMappingMapper;
    @Autowired
    private StandardSportMarketMapper standardSportMarketMapper;
    @Autowired
    private OddsPublicMethodApi oddsPublicMethodApi;
    @Reference(check = false, lazy = true, retries = 1, timeout = 5000)
    private ITradeMarketOddsApi tradeMarketOddsApi;

    /**
     * @Description   //根据赔率获取spread
     * @Param [odds, json]
     * @Author  sean
     * @Date   2021/8/13
     * @return java.math.BigDecimal
     **/
    public static BigDecimal getSpreadByOdds(String odds, JSONObject json){
        BigDecimal spread = new BigDecimal("0.1");
        for (String key : json.keySet()){
            String[] ks = key.split("-");
            if (Double.valueOf(odds).doubleValue() >= Double.valueOf(ks[0]) &&
                    Double.valueOf(odds).doubleValue() <= Double.valueOf(ks[1])){
                spread = new BigDecimal(json.getString(key));
                break;
            }
        }

        return spread;
    }
    /**
     * @Description   //跟赔率和特殊抽水重新计算赔率
     * @Param [oddsList, config]
     * @Author  sean
     * @Date   2021/8/13
     * @return void
     **/
    public void caluOddsBySpread(List<StandardSportMarketOdds> oddsList, RcsMatchMarketConfig config, RcsTournamentTemplatePlayMargain templatePlayMargain,BigDecimal changeOdds) {
        if (templatePlayMargain.getIsSpecialPumping() == 1){
//            StandardSportMarketOdds odds0 = oddsList.get(NumberUtils.INTEGER_ZERO);
//            StandardSportMarketOdds odds1 = oddsList.get(NumberUtils.INTEGER_ONE);
//            String oddsType = oddsPublicMethodApi.getOddsType(oddsList,config);
//            if (odds0.getOddsValue().intValue() != odds1.getOddsValue()){
//                oddsType = oddsList.stream().filter(e -> e.getOddsValue().intValue() == Math.min(odds0.getOddsValue(),odds1.getOddsValue())).findFirst().get().getOddsType();
//            }
//            StandardSportMarketOdds odds = oddsList.stream().filter(e -> e.getOddsValue().intValue() == Math.min(odds0.getOddsValue(),odds1.getOddsValue())).findFirst().get();
            StandardSportMarketOdds odds = getLowOdds(oddsList,config);
            BigDecimal spread = getSpreadByOdds(getEuOdds(odds.getOddsValue()),JSONObject.parseObject(templatePlayMargain.getSpecialOddsInterval()));
            for (StandardSportMarketOdds oddsDTO : oddsList){
                if (!oddsDTO.getOddsType().equalsIgnoreCase(odds.getOddsType())){
                    BigDecimal od = caluOddsBySpread(new BigDecimal(getMyOdds(odds.getOddsValue())),spread);
                    oddsDTO.setOddsValue(getEUOddsInteger(od.toPlainString()));
                }
            }
        }
    }

    private String getMyOdds(Integer oddsValue) {
        String odds = new BigDecimal(oddsValue).divide(new BigDecimal(BaseConstants.MULTIPLE_VALUE),NumberUtils.INTEGER_TWO,BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
        QueryWrapper<RcsOddsConvertMapping> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RcsOddsConvertMapping::getEurope, odds);
        wrapper.lambda().select(RcsOddsConvertMapping::getMalaysia);
        RcsOddsConvertMapping mapping = rcsOddsConvertMappingMapper.selectOne(wrapper);
        if ((!ObjectUtils.isEmpty(mapping)) && StringUtils.isNotBlank(mapping.getMalaysia())){
            odds = mapping.getMalaysia();
        }
        return odds;
    }
    private Integer getEUOddsInteger(String oddsValue) {
        Integer odds = new BigDecimal(oddsValue).multiply(new BigDecimal(BaseConstants.MULTIPLE_VALUE)).intValue();
        QueryWrapper<RcsOddsConvertMappingMy> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RcsOddsConvertMappingMy::getMalaysia, oddsValue);
        wrapper.lambda().select(RcsOddsConvertMappingMy::getEurope);
        RcsOddsConvertMappingMy mapping = rcsOddsConvertMappingMyMapper.selectOne(wrapper);
        if ((!ObjectUtils.isEmpty(mapping)) && StringUtils.isNotBlank(mapping.getEurope())){
            odds = new BigDecimal(mapping.getEurope()).multiply(new BigDecimal(BaseConstants.MULTIPLE_VALUE)).intValue();
        }
        return odds;
    }

    public static BigDecimal caluOddsBySpread(BigDecimal odds, BigDecimal spread) {
        BigDecimal odd = odds.add(spread);
        if (odd.intValue() < 1 ){
            odd = odd.multiply(new BigDecimal(NumberUtils.INTEGER_MINUS_ONE));
        }else if (odd.intValue() >= 1){
            odd = new BigDecimal(NumberUtils.INTEGER_TWO).subtract(odd);
        }
        return odd;
    }
    public String getEuOdds(Integer oddsValue){
        return new BigDecimal(oddsValue).divide(new BigDecimal(BaseConstants.MULTIPLE_VALUE),NumberUtils.INTEGER_TWO,BigDecimal.ROUND_DOWN).toPlainString();
    }
    /**
     * @Description   //计算低赔
     * @Param [oddsList, config]
     * @Author  sean
     * @Date   2021/8/31
     * @return com.panda.sport.rcs.pojo.StandardSportMarketOdds
     **/
    public StandardSportMarketOdds getLowOdds(List<StandardSportMarketOdds> oddsList,RcsMatchMarketConfig config){
        StandardSportMarketOdds odds0 = oddsList.get(NumberUtils.INTEGER_ZERO);
        StandardSportMarketOdds odds1 = oddsList.get(NumberUtils.INTEGER_ONE);
        StandardSportMarketOdds odds = odds0;
        if (odds0.getOddsValue().intValue() == odds1.getOddsValue()){
            String oddsType = oddsPublicMethodApi.getOddsType(oddsList,config);
            if (odds0.getOddsType().equalsIgnoreCase(oddsType)){
                odds = odds0;
            }else {
                odds = odds1;
            }
        }else {
            odds = oddsList.stream().filter(e -> e.getOddsValue().intValue() == Math.min(odds0.getOddsValue(),odds1.getOddsValue())).findFirst().get();
        }
        return odds;
    }
    /**
     * @Description   //根据低赔计算特殊spread
     * @Param [oddsList, config, templatePlayMargain]
     * @Author  sean
     * @Date   2021/8/31
     * @return java.math.BigDecimal
     **/
    public BigDecimal getSpicalSpread(List<StandardSportMarketOdds> oddsList,RcsMatchMarketConfig config,RcsTournamentTemplatePlayMargain templatePlayMargain){
        StandardSportMarketOdds odds = getLowOdds(oddsList,config);
        BigDecimal spread = getSpreadByOdds(getEuOdds(odds.getOddsValue()),JSONObject.parseObject(templatePlayMargain.getSpecialOddsInterval()));
        return spread;
    }
}
