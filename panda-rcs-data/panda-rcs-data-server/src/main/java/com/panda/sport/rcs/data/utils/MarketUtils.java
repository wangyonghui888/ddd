package com.panda.sport.rcs.data.utils;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.merge.dto.I18nItemDTO;
import com.panda.merge.dto.StandardMarketDTO;
import com.panda.merge.dto.StandardMarketOddsDTO;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMarketMessageDTO;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMarketOddsMessageDTO;
import com.panda.sport.rcs.enums.Basketball;
import com.panda.sport.rcs.enums.OddsTypeEnum;
import com.panda.sport.rcs.enums.TradeStatusEnum;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @Project Name: panda-rcs-trade-group
 * @Package Name: com.panda.sport.rcs.utils
 * @Description : Market 工具类
 * @Author : Paca
 * @Date : 2020-07-16 13:51
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
public class MarketUtils {

    public static StandardMarketDTO toStandardMarketDTO(StandardMarketMessageDTO market) {
        StandardMarketDTO marketDTO = new StandardMarketDTO();
        marketDTO.setMarketCategoryId(market.getMarketCategoryId());
        marketDTO.setChildStandardCategoryId(market.getChildMarketCategoryId());
        marketDTO.setMarketSource(market.getMarketSource());
        marketDTO.setMarketType(market.getMarketType());
//        marketDTO.setTradeType();
        marketDTO.setOddsValue(market.getOddsValue());
        marketDTO.setOddsName(market.getOddsName());
        marketDTO.setOrderType(market.getOrderType());
        marketDTO.setAddition1(market.getAddition1());
        marketDTO.setAddition2(market.getAddition2());
        marketDTO.setAddition3(market.getAddition3());
        marketDTO.setAddition4(market.getAddition4());
        marketDTO.setAddition5(market.getAddition5());
        marketDTO.setDataSourceCode(market.getDataSourceCode());
        marketDTO.setStatus(market.getStatus());
        marketDTO.setThirdMarketSourceStatus(market.getThirdMarketSourceStatus());
        marketDTO.setPlaceNumStatus(market.getPlaceNumStatus());
        marketDTO.setScopeId(market.getScopeId());
        marketDTO.setThirdMarketSourceId(market.getThirdMarketSourceId());
        marketDTO.setRemark(market.getRemark());
        marketDTO.setPlaceNum(market.getPlaceNum());
        marketDTO.setMarketHeadGap(market.getMarketHeadGap());
        marketDTO.setModifyTime(System.currentTimeMillis());
        marketDTO.setI18nNames(market.getI18nNames());
//        List<StandardMarketOddsMessageDTO> marketOddsList = market.getMarketOddsList();
//        if (CollectionUtils.isNotEmpty(marketOddsList)) {
//            List<StandardMarketOddsDTO> list = marketOddsList.stream().map(MarketUtils::toStandardMarketOddsDTO).collect(Collectors.toList());
//            marketDTO.setMarketOddsList(list);
//        }
        return marketDTO;
    }

    public static StandardMarketDTO toStandardMarketDTO(StandardSportMarket market) {
        StandardMarketDTO marketDTO = new StandardMarketDTO();
        marketDTO.setMarketCategoryId(market.getMarketCategoryId());
        marketDTO.setChildStandardCategoryId(market.getChildMarketCategoryId());
        marketDTO.setMarketSource(market.getMarketSource());
        marketDTO.setMarketType(market.getMarketType());
//      marketDTO.setTradeType();
        marketDTO.setOddsValue(market.getOddsValue());
        marketDTO.setOddsName(market.getOddsName());
        marketDTO.setOrderType(market.getOrderType());
        marketDTO.setAddition1(market.getAddition1());
        marketDTO.setAddition2(market.getAddition2());
        marketDTO.setAddition3(market.getAddition3());
        marketDTO.setAddition4(market.getAddition4());
        marketDTO.setAddition5(market.getAddition5());
        marketDTO.setDataSourceCode(market.getDataSourceCode());
        marketDTO.setStatus(market.getStatus());
        marketDTO.setThirdMarketSourceStatus(market.getThirdMarketSourceStatus());
        marketDTO.setPlaceNumStatus(market.getPlaceNumStatus());
        marketDTO.setEndEdStatus(market.getEndEdStatus());
        marketDTO.setScopeId(market.getScopeId());
        marketDTO.setThirdMarketSourceId(market.getThirdMarketSourceId());
        marketDTO.setRemark(market.getRemark());
        marketDTO.setPlaceNum(market.getPlaceNum());
        marketDTO.setMarketHeadGap(market.getMarketHeadGap2());
        marketDTO.setModifyTime(System.currentTimeMillis());
        marketDTO.setNameCode(market.getNameCode());
        marketDTO.setI18nNames(convertI18nNames(market.getI18nNames()));
        List<StandardSportMarketOdds> marketOddsList = market.getMarketOddsList();
        if (CollectionUtils.isNotEmpty(marketOddsList)) {
            List<StandardMarketOddsDTO> list = marketOddsList.stream().map(MarketUtils::toStandardMarketOddsDTO).collect(Collectors.toList());
            marketDTO.setMarketOddsList(list);
        }
        return marketDTO;
    }

    public static StandardMarketOddsDTO toStandardMarketOddsDTO(StandardSportMarketOdds marketOdds) {
        StandardMarketOddsDTO marketOddsDTO = new StandardMarketOddsDTO();
        marketOddsDTO.setActive(marketOdds.getActive());
        marketOddsDTO.setSettlementResultText(marketOdds.getSettlementResultText());
        marketOddsDTO.setSettlementResult(marketOdds.getSettlementResult());
        marketOddsDTO.setBetSettlementCertainty(marketOdds.getBetSettlementCertainty());
        marketOddsDTO.setOddsType(marketOdds.getOddsType());
        marketOddsDTO.setAddition1(marketOdds.getAddition1());
        marketOddsDTO.setAddition2(marketOdds.getAddition2());
        marketOddsDTO.setAddition3(marketOdds.getAddition3());
        marketOddsDTO.setAddition4(marketOdds.getAddition4());
        marketOddsDTO.setAddition5(marketOdds.getAddition5());
        marketOddsDTO.setName(marketOdds.getName());
        marketOddsDTO.setNameExpressionValue(marketOdds.getNameExpressionValue());
        marketOddsDTO.setMargin(marketOdds.getMargin() != null ? marketOdds.getMargin().doubleValue() : null);
        marketOddsDTO.setMarketDiffValue(marketOdds.getMarketDiffValue() != null ? marketOdds.getMarketDiffValue().doubleValue() : null);
        marketOddsDTO.setOddsValue(marketOdds.getOddsValue());
        marketOddsDTO.setOddsFieldsTemplateId(marketOdds.getOddsFieldsTempletId());
        marketOddsDTO.setOriginalOddsValue(marketOdds.getOriginalOddsValue());
        marketOddsDTO.setTargetSide(marketOdds.getTargetSide());
        marketOddsDTO.setOrderOdds(marketOdds.getOrderOdds());
        marketOddsDTO.setDataSourceCode(marketOdds.getDataSourceCode());
        marketOddsDTO.setThirdOddsFieldSourceId(marketOdds.getThirdOddsFieldSourceId());
        marketOddsDTO.setRemark(marketOdds.getRemark());
        marketOddsDTO.setModifyTime(System.currentTimeMillis());
        marketOddsDTO.setI18nNames(convertI18nNames(marketOdds.getI18nNames()));

        return marketOddsDTO;
    }

    public static StandardMarketOddsDTO toStandardMarketOddsDTO(StandardMarketOddsMessageDTO marketOdds) {
        StandardMarketOddsDTO marketOddsDTO = new StandardMarketOddsDTO();
        marketOddsDTO.setActive(marketOdds.getActive());
        marketOddsDTO.setSettlementResultText(marketOdds.getSettlementResultText());
        marketOddsDTO.setSettlementResult(marketOdds.getSettlementResult());
        marketOddsDTO.setBetSettlementCertainty(marketOdds.getBetSettlementCertainty());
        marketOddsDTO.setOddsType(marketOdds.getOddsType());
        marketOddsDTO.setAddition1(marketOdds.getAddition1());
        marketOddsDTO.setAddition2(marketOdds.getAddition2());
        marketOddsDTO.setAddition3(marketOdds.getAddition3());
        marketOddsDTO.setAddition4(marketOdds.getAddition4());
        marketOddsDTO.setAddition5(marketOdds.getAddition5());
        marketOddsDTO.setName(marketOdds.getName());
        marketOddsDTO.setNameExpressionValue(marketOdds.getNameExpressionValue());
        marketOddsDTO.setMarketDiffValue(marketOdds.getMarketDiffValue());
        marketOddsDTO.setOddsValue(marketOdds.getOddsValue());
        marketOddsDTO.setOddsFieldsTemplateId(marketOdds.getOddsFieldsTemplateId());
        marketOddsDTO.setOriginalOddsValue(marketOdds.getOriginalOddsValue());
        marketOddsDTO.setTargetSide(marketOdds.getTargetSide());
        marketOddsDTO.setOrderOdds(marketOdds.getOrderOdds());
        marketOddsDTO.setDataSourceCode(marketOdds.getDataSourceCode());
        marketOddsDTO.setThirdOddsFieldSourceId(marketOdds.getThirdOddsFieldSourceId());
        marketOddsDTO.setRemark(marketOdds.getRemark());
        marketOddsDTO.setModifyTime(System.currentTimeMillis());
        marketOddsDTO.setI18nNames(marketOdds.getI18nNames());

        return marketOddsDTO;
    }

    public static StandardMarketOddsDTO toStandardMarketOddsDTOs(StandardSportMarketOdds marketOdds) {
        StandardMarketOddsDTO marketOddsDTO = new StandardMarketOddsDTO();
        marketOddsDTO.setActive(marketOdds.getActive());
        marketOddsDTO.setSettlementResultText(marketOdds.getSettlementResultText());
        marketOddsDTO.setSettlementResult(marketOdds.getSettlementResult());
        marketOddsDTO.setBetSettlementCertainty(marketOdds.getBetSettlementCertainty());
        marketOddsDTO.setOddsType(marketOdds.getOddsType());
        marketOddsDTO.setAddition1(marketOdds.getAddition1());
        marketOddsDTO.setAddition2(marketOdds.getAddition2());
        marketOddsDTO.setAddition3(marketOdds.getAddition3());
        marketOddsDTO.setAddition4(marketOdds.getAddition4());
        marketOddsDTO.setAddition5(marketOdds.getAddition5());
        marketOddsDTO.setName(marketOdds.getName());
        marketOddsDTO.setNameExpressionValue(marketOdds.getNameExpressionValue());
        marketOddsDTO.setMargin(marketOdds.getMargin() != null ? marketOdds.getMargin().doubleValue() : null);
        marketOddsDTO.setMarketDiffValue(marketOdds.getMarketDiffValue() != null ? marketOdds.getMarketDiffValue().doubleValue() : null);
        marketOddsDTO.setOddsValue(marketOdds.getOddsValue());
        marketOddsDTO.setOddsFieldsTemplateId(marketOdds.getOddsFieldsTempletId());
        marketOddsDTO.setOriginalOddsValue(marketOdds.getOriginalOddsValue());
        marketOddsDTO.setTargetSide(marketOdds.getTargetSide());
        marketOddsDTO.setOrderOdds(marketOdds.getOrderOdds());
        marketOddsDTO.setDataSourceCode(marketOdds.getDataSourceCode());
        marketOddsDTO.setThirdOddsFieldSourceId(marketOdds.getThirdOddsFieldSourceId());
        marketOddsDTO.setRemark(marketOdds.getRemark());
        marketOddsDTO.setModifyTime(System.currentTimeMillis());
        marketOddsDTO.setI18nNames(convertI18nNames(marketOdds.getI18nNames()));

        return marketOddsDTO;
    }

    public static void main(String[] args) {
        Long playId = 19L;
        int marketCount = 7;
        BigDecimal marketNearDiff = BigDecimal.ONE;
        for (int i = 0; i < 10; i++) {
            BigDecimal mainMv = new BigDecimal("-4.5").add(new BigDecimal(i).multiply(new BigDecimal(1)));
            List<BigDecimal> list = generateMarketValues(playId, marketCount, mainMv, marketNearDiff);
            System.out.println(list);
            list.forEach(mv -> System.out.println(mv.negate()));
        }
    }

    public static List<BigDecimal> generateMarketValues(Long playId, int marketCount, BigDecimal mainMv, BigDecimal marketNearDiff) {
        // 全场让球玩法，初始盘口值是0时
        if (Basketball.Main.FULL_TIME.getHandicap().equals(playId) && BigDecimal.ZERO.compareTo(mainMv) == 0) {
            mainMv = RcsConstant.SPECIAL_MARKET_VALUE;
        }
        int count = marketCount + 3;
        // 奇数附加盘
        List<BigDecimal> oddMvList = new ArrayList<>(count);
        // 偶数附加盘
        List<BigDecimal> evenMvList = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            BigDecimal marketDiff = marketNearDiff.multiply(new BigDecimal(i));
            BigDecimal oddMv;
            BigDecimal evenMv;
            if (Basketball.isHandicap(playId)) {
                // 让分
                oddMv = mainMv.subtract(marketDiff).stripTrailingZeros();
                evenMv = mainMv.add(marketDiff).stripTrailingZeros();
            } else {
                // 大小
                oddMv = mainMv.add(marketDiff).stripTrailingZeros();
                evenMv = mainMv.subtract(marketDiff).stripTrailingZeros();
            }
            oddMvList.add(oddMv);
            evenMvList.add(evenMv);
        }
        List<BigDecimal> mvList = new ArrayList<>(marketCount + 1);
        mvList.add(mainMv.stripTrailingZeros());
        while (mvList.size() < marketCount + 1) {
            BigDecimal oddMv = getMv(oddMvList, playId);
            mvList.add(oddMv);
            BigDecimal evenMv = getMv(evenMvList, playId);
            mvList.add(evenMv);
        }
    
        mvList = mvList.subList(0, marketCount);
        log.info("生成盘口值列表：playId={}, marketCount={}, mainMv={}, marketNearDiff={}, mvList={}", playId, marketCount, mainMv, marketNearDiff, mvList);
        return mvList;
    }
    
    private static BigDecimal getMv(List<BigDecimal> mvList, Long playId) {
        int i = 0;
        while (i++ < 3) {
            BigDecimal mv = mvList.remove(0);
            if (Basketball.Main.FULL_TIME.getHandicap().equals(playId)) {
                if (!isSpecialMv(mv)) {
                    return mv;
                }
            } else if(Basketball.isHandicap(playId)) {
                if (BigDecimal.ZERO.compareTo(mv) != 0) {
                    return mv;
                }
            } else {
                return mv;
            }
        }
        return mvList.remove(0);
    }

    public static List<Map<String, BigDecimal>> generateMalayOddsList(Long playId, int marketCount, Map<Integer, BigDecimal> placeSpreadMap, BigDecimal marketNearOddsDiff,List<BigDecimal> marketValueList) {
        BigDecimal mainSpread = placeSpreadMap.get(NumberUtils.INTEGER_ONE);
    
        int index = 0;
        index = getIndex(marketValueList, index);
        BigDecimal halfNearOddsDiff = (index % 2 == 1) ? BigDecimal.valueOf(0.2).subtract(marketNearOddsDiff) : (BigDecimal.valueOf(0.2).subtract(marketNearOddsDiff).negate());
        
        List<Map<String, BigDecimal>> malayOddsList = new ArrayList<>(marketCount);
        for (int i = 0; i < marketCount; i++) {
            BigDecimal spread = placeSpreadMap.getOrDefault(i + 1, mainSpread);
            // 1 - spread / 2
            BigDecimal malayOdds = BigDecimal.ONE.subtract(spread.divide(new BigDecimal("2"), 6, BigDecimal.ROUND_HALF_UP)).setScale(2, BigDecimal.ROUND_DOWN);
            BigDecimal homeOdds = malayOdds;
            BigDecimal awayOdds = malayOdds;
            if (i > 0) {
                BigDecimal factor = (i % 2 == 1) ? new BigDecimal(i / 2 + 1) : new BigDecimal(i / 2).negate();
                BigDecimal multipleDiff = factor.multiply(marketNearOddsDiff);
                if(Basketball.isHandicap(playId)){
                    if (index > 0 && (i >= index) && ((i-index) % 2 == 0)) {
                        multipleDiff = factor.multiply(marketNearOddsDiff).add(halfNearOddsDiff);
                        log.info("替换盘口索引：{},正常赔率分差：{},替换外加；{}",index,marketNearOddsDiff,halfNearOddsDiff);
                    }
                }
                BigDecimal upOdds = malayOdds.add(multipleDiff).setScale(2, BigDecimal.ROUND_DOWN);
                homeOdds = checkMalayOdds(upOdds);
                BigDecimal downOdds = malayOdds.subtract(multipleDiff).setScale(2, BigDecimal.ROUND_DOWN);
                awayOdds = checkMalayOdds(downOdds);
            }
            Map<String, BigDecimal> map = Maps.newHashMap();
            map.put(RcsConstant.HOME_POSITION, homeOdds);
            map.put(RcsConstant.AWAY_POSITION, awayOdds);
            malayOddsList.add(map);
        }
        log.info("生成赔率列表：playId={}, marketCount={}, placeSpreadMap={}, marketNearOddsDiff={}, malayOddsList={}", playId, marketCount, placeSpreadMap, marketNearOddsDiff, malayOddsList);
        return malayOddsList;
    }
    
    
    /**
     * 寻找替换盘的位置
     * @param marketValueList
     * @param index
     * @return
     */
    public static int getIndex(List<BigDecimal> marketValueList, int index) {
        for (int i = 0; i < marketValueList.size(); i++) {
            if ((i + 1) < marketValueList.size()) {
                if (marketValueList.get(i).multiply(marketValueList.get(i + 1)).compareTo(BigDecimal.ZERO) < 0) {
                    index = i + 1;
                    break;
                }
                
            }
            if ((i + 2) < marketValueList.size()) {
                if (marketValueList.get(i).multiply(marketValueList.get(i + 2)).compareTo(BigDecimal.ZERO) < 0) {
                    index = i + 2;
                    break;
                }
            }
        }
        return index;
    }

    private static boolean isSpecialMv(BigDecimal mv) {
        if (mv == null) {
            return false;
        }
        return RcsConstant.SPECIAL_MARKET_VALUE.compareTo(mv.abs()) == 0 || BigDecimal.ZERO.compareTo(mv) == 0;
    }

    /**
     * 是否封盘校验
     * 只要出现0或±0.5的球头，独赢玩法自动封盘
     *
     * @param marketValues
     * @return
     */
    public static boolean isSealCheck(List<BigDecimal> marketValues) {
        if (CollectionUtils.isEmpty(marketValues)) {
            return false;
        }
        return marketValues.stream().anyMatch(MarketUtils::isSpecialMv);
    }

    public static String getNameExpressionValue(String oddsType, BigDecimal marketValue) {
        if (marketValue == null) {
            return null;
        }
        if (OddsTypeEnum.OVER.equalsIgnoreCase(oddsType) || OddsTypeEnum.UNDER.equalsIgnoreCase(oddsType)) {
            // 大小盘
            return marketValue.toPlainString();
        }
        if (OddsTypeEnum.HOME.equals(oddsType)) {
            // 主
            return marketValue.toPlainString();
        }
        if (OddsTypeEnum.AWAY.equals(oddsType)) {
            // 客
            return marketValue.negate().toPlainString();
        }
        return null;
    }

    /**
     * 校准马来赔
     *
     * @param malayOdds
     * @return
     */
    public static BigDecimal checkMalayOdds(BigDecimal malayOdds) {
        // 计算后的值 <= -1，则 计算后的值 + 2
        if (malayOdds.compareTo(BigDecimal.ONE.negate()) <= 0) {
            return malayOdds.add(new BigDecimal(NumberUtils.INTEGER_TWO));
        }
        // 计算后的值 > 1，则 计算后的值 - 2
        if (malayOdds.compareTo(BigDecimal.ONE) > 0) {
            return malayOdds.subtract(new BigDecimal(NumberUtils.INTEGER_TWO));
        }
        return malayOdds;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String getLinkId() {
        return getUUID() + "_trade";
    }

    public static String getLinkId(String suffix) {
        return getLinkId() + "_" + suffix;
    }

    /**
     * 构建盘口
     *
     * @param playId        玩法ID
     * @param subPlayId     子玩法ID
     * @param matchType     0-滚球，1-早盘
     * @param placeNum      位置
     * @param marketValue   盘口值
     * @param marketHeadGap 盘口差
     * @return
     */
    public static StandardMarketDTO buildStandardMarket(Long playId, Long subPlayId, Integer matchType, Integer placeNum, BigDecimal marketValue, BigDecimal marketHeadGap) {
        StandardMarketDTO market = new StandardMarketDTO();
        market.setMarketCategoryId(playId);
        market.setMarketType(matchType);
        if (marketValue != null) {
            market.setOddsValue(marketValue.toPlainString());
            market.setAddition1(marketValue.toPlainString());
        }
        if (Lists.newArrayList(145L, 146L).contains(playId)) {
            long section = subPlayId - playId * 100;
            market.setAddition2(String.valueOf(section));
        }
        market.setAddition3(null);
        market.setAddition4(null);
        market.setAddition5(null);
        market.setDataSourceCode(BaseConstants.DATA_SOURCE_CODE);
        market.setStatus(TradeStatusEnum.OPEN.getStatus());
        market.setThirdMarketSourceStatus(TradeStatusEnum.OPEN.getStatus());
        market.setPlaceNumStatus(TradeStatusEnum.OPEN.getStatus());
        market.setPlaceNum(placeNum);
        if (marketHeadGap != null) {
            market.setMarketHeadGap(marketHeadGap.doubleValue());
        }
        market.setModifyTime(System.currentTimeMillis());
        market.setChildStandardCategoryId(subPlayId);
        market.setEndEdStatus(0);
        return market;
    }

    /**
     * 构建投注项
     *
     * @param oddsType    投注项类型
     * @param waterDiff   水差
     * @param marketValue 盘口值
     * @return
     */
    public static StandardMarketOddsDTO buildStandardMarketOdds(String oddsType, BigDecimal waterDiff, BigDecimal marketValue) {
        StandardMarketOddsDTO marketOdds = new StandardMarketOddsDTO();
        marketOdds.setThirdSourceActive(1);
        marketOdds.setActive(1);
        marketOdds.setOddsType(oddsType);
        if (marketValue != null) {
            marketOdds.setNameExpressionValue(getNameExpressionValue(oddsType, marketValue));
        }
        if (waterDiff != null) {
            if (OddsTypeEnum.isHomeOddsType(oddsType)) {
                marketOdds.setMarketDiffValue(waterDiff.negate().doubleValue());
            } else if (OddsTypeEnum.isAwayOddsType(oddsType)) {
                marketOdds.setMarketDiffValue(waterDiff.doubleValue());
            }
        }
        if (OddsTypeEnum.isHomeOddsType(oddsType)) {
            marketOdds.setOrderOdds(1);
            marketOdds.setTargetSide(RcsConstant.HOME);
        } else if (OddsTypeEnum.isAwayOddsType(oddsType)) {
            marketOdds.setOrderOdds(2);
            marketOdds.setTargetSide(RcsConstant.AWAY);
        }
        marketOdds.setDataSourceCode(BaseConstants.DATA_SOURCE_CODE);
        marketOdds.setModifyTime(System.currentTimeMillis());
        return marketOdds;
    }

    public static List<I18nItemDTO> convertI18nNames(String i18nNames) {
        if (StringUtils.isBlank(i18nNames) || "null".equalsIgnoreCase(i18nNames)) {
            return null;
        }
        try {
            return JsonFormatUtils.fromJsonArray(i18nNames, I18nItemDTO.class);
        } catch (Exception e) {
        }
        return null;
    }
}
