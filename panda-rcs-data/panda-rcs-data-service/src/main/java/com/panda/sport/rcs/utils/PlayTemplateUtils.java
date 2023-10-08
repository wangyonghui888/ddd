package com.panda.sport.rcs.utils;

import com.google.common.collect.Lists;
import com.panda.sport.rcs.constants.Placeholder;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.enums.OddsTypeEnum;
import com.panda.sport.rcs.factory.BeanFactory;
import com.panda.sport.rcs.mongo.I18nBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Project Name : panda-rcs-trade-group
 * @Package Name : panda-rcs-trade-group
 * @Description : 玩法模板工具类
 * @Author : Paca
 * @Date : 2020-11-19 20:39
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class PlayTemplateUtils {

    private static final String MINUS = "-";
    private static final String PLUS = "+";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String X = "X";
    private static final String Y = "Y";
    private static final List<Long> FROM_TO_PLAYS = Lists.newArrayList(32L, 33L, 34L, 231L, 232L, 233L);
    private static final List<Long> A1_PLAYS = Lists.newArrayList(28L, 30L, 31L, 109L, 110L, 120L, 125L, 133L, 148L, 201L, 208L, 214L, 222L, 224L, 225L, 230L, 235L, 237L, 255L, 261L, 265L, 266L, 267L, 344L);
    private static final List<Long> A2_PLAYS = Lists.newArrayList(145L, 146L, 162L, 163L, 164L, 165L, 166L, 170L, 175L, 176L, 177L, 178L, 184L, 185L, 186L, 187L, 189L, 190L, 191L, 192L, 193L, 194L, 196L, 197L, 253L, 254L, 262L, 263L, 264L, 268L);
    private static final List<Long> A3_PLAYS = Lists.newArrayList( 357L, 336L
            , 387L);

    private static final List<Long> PLACEHOLDER_PLAYS;

    static {
        PLACEHOLDER_PLAYS = Lists.newArrayList(147L, 167L, 168L, 179L, 188L, 195L, 203L, 215L, 256L);
        PLACEHOLDER_PLAYS.addAll(FROM_TO_PLAYS);
        PLACEHOLDER_PLAYS.addAll(A1_PLAYS);
        PLACEHOLDER_PLAYS.addAll(A2_PLAYS);
        PLACEHOLDER_PLAYS.add(387L) ;  //控制影响范围，所以未使用 PLACEHOLDER_PLAYS.addAll(A3_PLAYS);
    }

    private static final List<Long> ODDS_TYPE_1X2 = Lists.newArrayList(1L, 3L, 4L, 5L, 17L, 19L, 25L, 27L, 29L, 32L, 33L, 37L, 39L, 43L, 44L, 46L, 48L, 50L, 52L, 54L, 56L, 58L, 60L, 62L, 64L, 66L, 69L, 71L, 77L, 91L, 111L, 113L, 119L, 121L, 126L, 128L, 129L, 130L, 132L, 135L, 136L, 142L, 143L, 144L, 153L, 154L, 155L, 162L, 163L, 167L, 168L, 172L, 175L, 176L, 179L, 181L, 184L, 185L, 188L, 189L, 195L, 196L, 201L, 203L, 214L, 215L, 231L, 232L, 242L, 243L, 249L, 253L, 256L, 259L, 261L, 268L, 269L, 270L, 352L);
    private static final List<Long> CORRECT_SCORE = Lists.newArrayList(7L, 20L, 74L, 236L, 241L, 344L);
    private static final List<Long> EXACT_GOALS = Lists.newArrayList(8L, 9L, 14L, 21L, 22L, 23L, 73L, 239L);
    private static final List<Long> RANGE = Lists.newArrayList(31L, 68L, 117L, 190L, 210L, 218L, 226L, 227L, 228L, 239L);

    /**
     * 是否显示盘口名称
     *
     * @param playId
     * @return
     */
    public static boolean isShowMarketName(Long playId) {
        return PLACEHOLDER_PLAYS.contains(playId);
    }

    /**
     * 盘口名称占位符替换
     *
     * @param names
     * @param teamMap
     */
    public static void handleMarketName(Long playId, I18nBean names, Map<String, I18nBean> teamMap, String a1, String a2, String a3) {
        if (names == null) {
            return;
        }
        // 替换掉 主客队 占位符
        names.replaceTeam(teamMap);
        if (playId == 147L) {
            // A2,A1 -> 第{!quarternr}节首先获得{pointnr}分
            names.replaceQuarter(a2);
            names.replacePoint(a1);
        } else if (playId == 167L) {
            // A1,A2,A2+1 -> 第{!setnr}盘第{!gamenrX}局和第{!gamenrY}局谁获胜多
            names.replaceSet(a1);
            names.replace(Placeholder.WHICH_GAME_X, a2);
            BigDecimal gameY = toBigDecimal(a2, BigDecimal.ZERO).add(BigDecimal.ONE);
            names.replace(Placeholder.WHICH_GAME_Y, gameY.toPlainString());
        } else if (playId == 168L) {
            // A1,A2 -> 第{!setnr}盘第{gamenr}局获胜
            names.replaceSet(a1);
            names.replaceGame(a2);
        } else if (playId == 179L) {
            // A2,A1 -> 第{!gamenr}局第{!pointnr}分
            names.replaceGame(a2);
            names.replacePoint(a1);
        } else if (playId == 188L) {
            // A2,A1 -> 第{!framenr}局首先到达{pointnr}分
            names.replaceFrame(a2);
            names.replacePoint(a1);
        } else if (playId == 195L) {
            // A2,A1 -> 第{!framenr}局第{!xth}个进球的选手
            names.replaceFrame(a2);
            names.replaceXth(a1);
        } else if (playId == 203L) {
            // A1,A2 -> 第{!gamenr}局首先获得{pointnr}分
            names.replaceGame(a1);
            names.replacePoint(a2);
        } else if (playId == 215L) {
            // A1,A2 -> 第{!quarternr}节首先获得{pointnr}分
            names.replaceQuarter(a1);
            names.replacePoint(a2);
        } else if (playId == 256L) {
            // A1,A2 -> 第{!setnr}局谁先获得{pointnr}分
            names.replaceSet(a1);
            names.replacePoint(a2);
        } else if (FROM_TO_PLAYS.contains(playId)) {
            // 替换 15分钟 玩法中的 {from} 和 {to} 占位符
            names.replaceFromAndTo(a2, a3);
        } else if (A1_PLAYS.contains(playId)) {
            // A1
            names.replaceWithA1(a1);
        } else if (A2_PLAYS.contains(playId)) {
            // A2
            names.replaceWithA2(a2);
        } else if (A3_PLAYS.contains(playId)) {
            names.replaceInningnrX(a1);
        }
    }

    public static Integer getMarketOddsSortNo(Long playId, String oddsType, Integer orderOdds) {
        if (RcsConstant.TOTAL.contains(playId)) {
            // 大小盘
            if (OddsTypeEnum.Total.OVER.getOddsType().equalsIgnoreCase(oddsType)) {
                return 1;
            } else if (OddsTypeEnum.Total.UNDER.getOddsType().equalsIgnoreCase(oddsType)) {
                return 2;
            }
        } else if (RcsConstant.OVER_X_UNDER.contains(playId)) {
            if (OddsTypeEnum.OVER.equalsIgnoreCase(oddsType)) {
                return 1;
            } else if (OddsTypeEnum.UNDER.equalsIgnoreCase(oddsType)) {
                return 3;
            } else if (OddsTypeEnum.DRAW.equalsIgnoreCase(oddsType)) {
                return 2;
            }
        } else if (ODDS_TYPE_1X2.contains(playId)) {
            if (OddsTypeEnum.HOME.equalsIgnoreCase(oddsType)) {
                return 1;
            } else if (OddsTypeEnum.AWAY.equalsIgnoreCase(oddsType)) {
                return 3;
            } else if (OddsTypeEnum.DRAW.equalsIgnoreCase(oddsType)) {
                return 2;
            }
        } else if (RcsConstant.WINNING_MARGIN.contains(playId)) {
            // 净胜分
            if (StringUtils.containsIgnoreCase(oddsType, OddsTypeEnum.AND)) {
                String[] array = StringUtils.split(oddsType, OddsTypeEnum.AND);
                if (array != null && array.length == NumberUtils.INTEGER_TWO) {
                    int sortNo = getSortNo(array[1]);
                    if (sortNo < Integer.MAX_VALUE / BigInteger.TEN.intValue()) {
                        sortNo = NumberUtils.toInt(sortNo + StringUtils.EMPTY + array[0], sortNo);
                    }
                    return sortNo;
                }
            }
            return Integer.MAX_VALUE;
        } else if (CORRECT_SCORE.contains(playId)) {
            // 比分
            if (StringUtils.contains(oddsType, COLON)) {
                String[] array = StringUtils.split(oddsType, COLON);
                if (array != null && array.length == NumberUtils.INTEGER_TWO) {
                    int home = NumberUtils.toInt(array[0], 0);
                    int away = NumberUtils.toInt(array[1], 0);
                    if (home > away) {
                        return NumberUtils.toInt("1" + array[0] + array[1], Integer.MAX_VALUE);
                    } else if (home < away) {
                        return NumberUtils.toInt("3" + array[1] + array[0], Integer.MAX_VALUE);
                    } else {
                        return NumberUtils.toInt("2" + array[0] + array[1], Integer.MAX_VALUE);
                    }
                }else if (array != null && array.length > NumberUtils.INTEGER_TWO) {
                	//344 多重波胆投注
                	String[] slashArray = StringUtils.split(oddsType, SLASH);
                	if(slashArray.length>1) {
                		array = StringUtils.split(slashArray[0], COLON);
                        int home = NumberUtils.toInt(array[0], 0);
                        int away = NumberUtils.toInt(array[1], 0);
                        if (home > away) {
                            return  NumberUtils.toInt("1" + array[0] + array[1], Integer.MAX_VALUE);
                        } else if (home < away) {
                            return NumberUtils.toInt("3" + array[1] + array[0], Integer.MAX_VALUE);
                        } else {
                            return NumberUtils.toInt("2" + array[0] + array[1], Integer.MAX_VALUE);
                        }
                	}
                }
            } else if (OddsTypeEnum.OTHER.equalsIgnoreCase(oddsType)) {
                return Integer.MAX_VALUE;
            } else if (OddsTypeEnum.AWAY_OTHER.equalsIgnoreCase(oddsType)) {
            	return Integer.MAX_VALUE;//盘口位置为客队 
            } else if (OddsTypeEnum.DRAW_OTHER.equalsIgnoreCase(oddsType)) {
            	return Integer.MAX_VALUE;//盘口位置为其他
            } else if (OddsTypeEnum.HOME_OTHER.equalsIgnoreCase(oddsType)) {
            	return Integer.MAX_VALUE;//盘口位置为主队
            }
        } else if (EXACT_GOALS.contains(playId) || RANGE.contains(playId)) {
            // 准确进球数类、区间类玩法 投注项排序处理
            return getSortNo(oddsType);
        }
        return orderOdds;
    }

    private static int getSortNo(String oddsType) {
        if (StringUtils.isBlank(oddsType)) {
            return Integer.MAX_VALUE;
        }
        try {
            if (StringUtils.contains(oddsType, MINUS)) {
                String[] array = oddsType.split(MINUS);
                String value = Double.valueOf(array[0]).intValue() + StringUtils.EMPTY + Double.valueOf(array[1]).intValue();
                return Integer.parseInt(value);
            } else if (StringUtils.contains(oddsType, PLUS)) {
                return Double.valueOf(StringUtils.replace(oddsType, PLUS, StringUtils.EMPTY)).intValue() * 1000000;
            } else {
                return Double.valueOf(oddsType).intValue();
            }
        } catch (Throwable t) {
            return Integer.MAX_VALUE;
        }
    }

    public static BigDecimal toBigDecimal(String value, BigDecimal defaultValue) {
        try {
            if (StringUtils.isNotBlank(value)) {
                return new BigDecimal(value);
            }
        } catch (Exception e) {
        }
        return defaultValue;
    }
}
