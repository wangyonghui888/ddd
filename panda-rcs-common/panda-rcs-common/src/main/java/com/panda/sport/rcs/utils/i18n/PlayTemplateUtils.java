package com.panda.sport.rcs.utils.i18n;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;

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
    private static final String X = "X";
    private static final String Y = "Y";
    private static final List<Long> FROM_TO_PLAYS = Lists.newArrayList(32L, 33L, 34L, 231L, 232L, 233L);
    private static final List<Long> A1_PLAYS = Lists.newArrayList(28L, 30L, 31L, 109L, 110L, 120L, 125L, 133L, 148L, 201L, 208L, 214L, 222L, 224L, 225L, 230L, 235L, 237L, 255L, 261L, 265L, 266L, 267L);
    private static final List<Long> A2_PLAYS = Lists.newArrayList(145L, 146L, 162L, 163L, 164L, 165L, 166L, 170L, 175L, 176L, 177L, 178L, 184L, 185L, 186L, 187L, 189L, 190L, 191L, 192L, 193L, 194L, 196L, 197L, 253L, 254L, 262L, 263L, 264L, 268L);

    private static final List<Long> PLACEHOLDER_PLAYS;

    static {
        PLACEHOLDER_PLAYS = Lists.newArrayList(147L, 167L, 168L, 179L, 188L, 195L, 203L, 215L, 256L);
        PLACEHOLDER_PLAYS.addAll(FROM_TO_PLAYS);
        PLACEHOLDER_PLAYS.addAll(A1_PLAYS);
        PLACEHOLDER_PLAYS.addAll(A2_PLAYS);
    }

    private static final List<Long> ODDS_TYPE_1X2 = Lists.newArrayList(1L, 3L, 4L, 5L, 17L, 19L, 25L, 27L, 29L, 32L, 33L, 37L, 39L, 43L, 44L, 46L, 48L, 50L, 52L, 54L, 56L, 58L, 60L, 62L, 64L, 66L, 69L, 71L, 77L, 91L, 111L, 113L, 119L, 121L, 126L, 128L, 129L, 130L, 132L, 135L, 136L, 142L, 143L, 144L, 153L, 154L, 155L, 162L, 163L, 167L, 168L, 172L, 175L, 176L, 179L, 181L, 184L, 185L, 188L, 189L, 195L, 196L, 201L, 203L, 214L, 215L, 231L, 232L, 242L, 243L, 249L, 253L, 256L, 259L, 261L, 268L, 269L, 270L);
    private static final List<Long> CORRECT_SCORE = Lists.newArrayList(7L, 341L, 342L, 343L, 241L);
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
     * @param I18nBean  I18nBean
     * @param teamMap
     * teamMap.put(I18nBean.HOME_POSITION, BeanFactory.getHomeTeam());
       teamMap.put(I18nBean.AWAY_POSITION, BeanFactory.getAwayTeam());
            
     */
    public static <T> T handleMarketName(Long playId , I18nBean names,Map<String, I18nBean> teamMap,Boolean isReplaceMarket ,String... adds) {
        if (names == null) {
            return null;
        }
        
        String addition1 = adds != null && adds.length > 0 ? adds[0] : null;
        String addition2 = adds != null && adds.length > 1 ? adds[1] : null;
        String addition3 = adds != null && adds.length > 2 ? adds[2] : null;
        String addition4 = adds != null && adds.length > 3 ? adds[3] : null;
        String addition5 = adds != null && adds.length > 4 ? adds[4] : null;
        
        // 替换掉 主客队 占位符
        names.replaceTeam(teamMap);
        if (playId == 147L) {
            // A2,A1 -> 第{!quarternr}节首先获得{pointnr}分
            names.replaceQuarter(addition2);
            names.replacePoint(addition1);
        } else if (playId == 167L) {
            // A1,A2,A2+1 -> 第{!setnr}盘第{!gamenrX}局和第{!gamenrY}局谁获胜多
            names.replaceSet(addition1);
            names.replace(Placeholder.WHICH_GAME_X, addition2);
            BigDecimal gameY = CommonUtils.toBigDecimal(addition2, BigDecimal.ZERO).add(BigDecimal.ONE);
            names.replace(Placeholder.WHICH_GAME_Y, gameY.toPlainString());
        } else if (playId == 168L) {
            // A1,A2 -> 第{!setnr}盘第{gamenr}局获胜
            names.replaceSet(addition1);
            names.replaceGame(addition2);
        } else if (playId == 179L) {
            // A2,A1 -> 第{!gamenr}局第{!pointnr}分
            names.replaceGame(addition2);
            names.replacePoint(addition1);
        } else if (playId == 188L) {
            // A2,A1 -> 第{!framenr}局首先到达{pointnr}分
            names.replaceFrame(addition2);
            names.replacePoint(addition1);
        } else if (playId == 195L) {
            // A2,A1 -> 第{!framenr}局第{!xth}个进球的选手
            names.replaceFrame(addition2);
            names.replaceXth(addition1);
        } else if (playId == 203L) {
            // A1,A2 -> 第{!gamenr}局首先获得{pointnr}分
            names.replaceGame(addition1);
            names.replacePoint(addition2);
        } else if (playId == 215L) {
            // A1,A2 -> 第{!quarternr}节首先获得{pointnr}分
            names.replaceQuarter(addition1);
            names.replacePoint(addition2);
        } else if (playId == 256L) {
            // A1,A2 -> 第{!setnr}局谁先获得{pointnr}分
            names.replaceSet(addition1);
            names.replacePoint(addition2);
        } else if (FROM_TO_PLAYS.contains(playId)) {
            // 替换 15分钟 玩法中的 {from} 和 {to} 占位符
            names.replaceFromAndTo(addition2, addition3);
        } else if (A1_PLAYS.contains(playId)) {
            // A1
            names.replaceWithA1(addition1);
        } else if (A2_PLAYS.contains(playId)) {
            // A2
            names.replaceWithA2(addition2);
        }
        
        if(isReplaceMarket) {
        	return handlePlayName(playId, names, teamMap);
        }
        return JSONObject.parseObject(JSONObject.toJSONString(names),new TypeReference<T>() {});
    }
    
    /**
     * 盘口名称占位符替换
     *Object 类型是I18n防止各个服务的包名不一致，导致调用问题
     * @param i18nBeanNames  I18nBean
     * @param teamMap
     * teamMap.put(I18nBean.HOME_POSITION, BeanFactory.getHomeTeam());
       teamMap.put(I18nBean.AWAY_POSITION, BeanFactory.getAwayTeam());
            
     */
    public static <T> T handleMarketName(Long playId , Object playI18nBeanNames,Map<String, Object> teamI18nMap, String... adds) {
        if (playI18nBeanNames == null) {
            return null;
        }
        
        I18nBean names = JSONObject.parseObject(JSONObject.toJSONString(playI18nBeanNames),I18nBean.class);
        Map<String, I18nBean> teamMap = JSONObject.parseObject(JSONObject.toJSONString(teamI18nMap),new TypeReference<Map<String, I18nBean>>(){});
        
        return handleMarketName(playId, names, teamMap,true, adds);
    }

    /**
     * 玩法名称占位符替换
     *
     * @param category
     * @param teamMap
     */
    public static <T> T  handlePlayName(Long playId , Object i18nBeanNames,Map<String, Object> teamI18nMap) {
        if (i18nBeanNames == null) {
            return null;
        }
        
        I18nBean names = JSONObject.parseObject(JSONObject.toJSONString(i18nBeanNames),I18nBean.class);
        Map<String, I18nBean> teamMap = JSONObject.parseObject(JSONObject.toJSONString(teamI18nMap),new TypeReference<Map<String, I18nBean>>(){});
        
        return handlePlayName(playId, names, teamMap);
    }
    
    /**
     * 玩法名称占位符替换
     *
     * @param category
     * @param teamMap
     */
    public static <T> T handlePlayName(Long playId , I18nBean names,Map<String, I18nBean> teamMap) {
        if (names == null) {
            return null;
        }
        
        // 替换掉 主客队 占位符
        names.replaceTeam(teamMap);
        if (playId == 147L) {
            names.replaceQuarter(X);
            names.replacePoint(Y);
        } else if (playId == 167L) {
            names.replaceSet(X);
            names.replace(Placeholder.WHICH_GAME_X, Y);
            names.replace(Placeholder.WHICH_GAME_Y, Y + "+1");
        } else if (playId == 168L) {
            names.replaceSet(X);
            names.replaceGame(Y);
        } else if (playId == 179L) {
            names.replaceGame(X);
            names.replacePoint(Y);
        } else if (playId == 188L) {
            names.replaceFrame(X);
            names.replacePoint(Y);
        } else if (playId == 195L) {
            names.replaceFrame(X);
            names.replaceXth(Y);
        } else if (playId == 203L) {
            names.replaceGame(X);
            names.replacePoint(Y);
        } else if (playId == 215L) {
            names.replaceQuarter(X);
            names.replacePoint(Y);
        } else if (playId == 256L) {
            names.replaceSet(X);
            names.replacePoint(Y);
        } else if (FROM_TO_PLAYS.contains(playId)) {
            names.replaceFromAndTo(X, Y);
        } else if (A1_PLAYS.contains(playId)) {
            // A1
            names.replaceWithA1(X);
        } else if (A2_PLAYS.contains(playId)) {
            // A2
            names.replaceWithA2(X);
        }
        
        return JSONObject.parseObject(JSONObject.toJSONString(names),new TypeReference<T>() {});
    }
    
    
    
    /**
     * 玩法名称占位符替换
     *
     * @param category
     * @param teamMap
     */
    public static String handlePlayName(Long playId , String playNames,String homeName, String awayName) {
        if (playNames == null) {
            return null;
        }
        I18nBean names = new I18nBean(playNames);
        Map<String, I18nBean> teamMap = new HashMap<String, I18nBean>();
        teamMap.put(I18nBean.HOME_POSITION, new I18nBean(homeName));
        teamMap.put(I18nBean.AWAY_POSITION, new I18nBean(awayName));
        
        // 替换掉 主客队 占位符
        names.replaceTeam(teamMap);
        if (playId == 147L) {
            names.replaceQuarter(X);
            names.replacePoint(Y);
        } else if (playId == 167L) {
            names.replaceSet(X);
            names.replace(Placeholder.WHICH_GAME_X, Y);
            names.replace(Placeholder.WHICH_GAME_Y, Y + "+1");
        } else if (playId == 168L) {
            names.replaceSet(X);
            names.replaceGame(Y);
        } else if (playId == 179L) {
            names.replaceGame(X);
            names.replacePoint(Y);
        } else if (playId == 188L) {
            names.replaceFrame(X);
            names.replacePoint(Y);
        } else if (playId == 195L) {
            names.replaceFrame(X);
            names.replaceXth(Y);
        } else if (playId == 203L) {
            names.replaceGame(X);
            names.replacePoint(Y);
        } else if (playId == 215L) {
            names.replaceQuarter(X);
            names.replacePoint(Y);
        } else if (playId == 256L) {
            names.replaceSet(X);
            names.replacePoint(Y);
        } else if (FROM_TO_PLAYS.contains(playId)) {
            names.replaceFromAndTo(X, Y);
        } else if (A1_PLAYS.contains(playId)) {
            // A1
            names.replaceWithA1(X);
        } else if (A2_PLAYS.contains(playId)) {
            // A2
            names.replaceWithA2(X);
        }
        
        return names.getZs();
    }

}
